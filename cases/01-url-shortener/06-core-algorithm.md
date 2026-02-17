# ⚙️ Step 6: Core Algorithm — Generating Short Codes

> This is the most technically interesting part of the URL shortener.
> Interviewers will deep-dive here.

---

## The Problem

We need to generate a **unique, short, non-guessable** code for each URL.

Constraints:
- 7 characters long (configurable)
- Uses characters: `a-z`, `A-Z`, `0-9` (Base62)
- No collisions (two URLs must not get the same code)
- Not sequential (shouldn't be able to guess the next URL)

### How Many Unique Codes?

```
Base62 alphabet: a-z (26) + A-Z (26) + 0-9 (10) = 62 characters
7-character codes: 62^7 = 3.5 TRILLION unique codes

We need capacity for 6 billion URLs (5 years) — that's only 0.17% of the space. ✅
```

---

## Approach 1: Hash + Truncate

### How It Works
1. Take the long URL
2. Hash it with MD5 or SHA-256
3. Take the first 7 characters of the hash (Base62 encoded)

```java
@Service
public class HashBasedCodeGenerator {

    public String generate(String longUrl) {
        // 1. Hash the URL
        String hash = DigestUtils.md5DigestAsHex(longUrl.getBytes());
        // hash = "e99a18c428cb38d5f260853678922e03" (32 hex chars)

        // 2. Convert to Base62 and take first 7 chars
        BigInteger number = new BigInteger(hash, 16);
        String base62 = toBase62(number);
        return base62.substring(0, 7);
    }

    private String toBase62(BigInteger number) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);

        while (number.compareTo(BigInteger.ZERO) > 0) {
            sb.append(chars.charAt(number.mod(base).intValue()));
            number = number.divide(base);
        }

        return sb.reverse().toString();
    }
}
```

### ⚠️ The Collision Problem

Truncating a hash to 7 characters **will** cause collisions. Two different long URLs could produce the same short code.

### Collision Resolution Strategy

```java
public String generateWithCollisionHandling(String longUrl) {
    String shortCode = generate(longUrl);

    // If collision, append a counter and re-hash
    int attempt = 0;
    while (urlRepository.existsByShortCode(shortCode)) {
        attempt++;
        shortCode = generate(longUrl + attempt);  // re-hash with suffix
    }

    return shortCode;
}
```

| Pros | Cons |
|------|------|
| Same long URL → same short code (dedup) | Collision handling adds DB lookups |
| No separate ID service needed | Under high load, collision checks can be slow |
| Simple to implement | Hash truncation loses entropy |

---

## Approach 2: Counter-Based + Base62 (Recommended ✅)

### How It Works
1. A unique ID generator produces a globally unique integer
2. Convert the integer to Base62 to get the short code

```java
@Service
public class CounterBasedCodeGenerator {

    private static final String BASE62_CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String toBase62(long id) {
        if (id == 0) return "a";

        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62_CHARS.charAt((int)(id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }

    // Example:
    // id = 123456789
    // base62 = "8m0Kx"  (5 chars, padded to 7 → "aa8m0Kx")
}
```

### The Key Question: Where Does the Unique ID Come From?

#### Option A: Database Auto-Increment

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
// short_code = toBase62(id)
```

| Pros | Cons |
|------|------|
| Simple | Sequential IDs are **guessable** |
| No extra service | Single DB is bottleneck |
| Guaranteed unique | Difficult across shards |

#### Option B: Twitter Snowflake (Recommended ✅)

A **distributed ID generator** that produces 64-bit unique IDs:

```
┌─────────────────────────────────────────────────────────────┐
│ 1 bit │  41 bits timestamp  │ 10 bits machine │ 12 bits seq │
│ (0)   │  (milliseconds)     │  ID             │  number     │
└─────────────────────────────────────────────────────────────┘

- 41 bits → ~69 years of timestamps
- 10 bits → 1024 machines
- 12 bits → 4096 IDs per millisecond per machine
```

```java
/**
 * Simplified Snowflake ID generator.
 * In production, use a library like "snowflake-id" or a service like
 * ZooKeeper-backed ID generation.
 */
@Component
public class SnowflakeIdGenerator {

    private final long machineId;
    private final long epoch = 1640995200000L; // 2022-01-01
    private long sequence = 0;
    private long lastTimestamp = -1;

    public SnowflakeIdGenerator(@Value("${app.machine-id}") long machineId) {
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12 bits
            if (sequence == 0) {
                // Wait for next millisecond
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22)
             | (machineId << 12)
             | sequence;
    }
}
```

| Pros | Cons |
|------|------|
| Distributed — no single point of failure | Clock sync matters |
| Time-sortable IDs | Slightly more complex setup |
| ~4 million IDs/sec per machine | Need to manage machine IDs |
| Non-guessable (timestamps encoded) | |

#### Option C: Pre-Generated Key Pool

Generate all possible 7-char codes in advance and store them in a `key_pool` table.

```
┌───────────────────────────┐
│     Key Pool Database     │
│                           │
│  key       │ used         │
│  ──────────┼───────────   │
│  abc1234   │ false        │
│  xyz5678   │ false        │
│  def9012   │ true         │
│  ...       │ ...          │
└───────────────────────────┘
```

```java
@Repository
public interface KeyPoolRepository extends JpaRepository<KeyEntity, String> {

    @Modifying
    @Query(value = """
        UPDATE key_pool SET used = true
        WHERE key_value = (
            SELECT key_value FROM key_pool
            WHERE used = false
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        )
        RETURNING key_value
        """, nativeQuery = true)
    Optional<String> claimNextKey();
}
```

| Pros | Cons |
|------|------|
| Fast O(1) key retrieval | Need to pre-generate keys |
| Zero collision risk | Requires key management |
| Simple service logic | Large key pool table (3.5T rows if all generated) |

---

## Comparison Summary

| Approach | Collision Risk | Speed | Complexity | Best For |
|----------|---------------|-------|-----------|----------|
| Hash + Truncate | Medium (needs resolution) | Fast | Low | Small-medium scale |
| Counter + Base62 (Snowflake) | None | Fast | Medium | **Large scale (recommended)** |
| Pre-Generated Pool | None | Fast | High (setup) | Very high throughput |

### 💡 Interview Recommendation

> Go with **Snowflake ID + Base62**. It's the most commonly expected answer, shows
> knowledge of distributed ID generation, and avoids collisions entirely.

---

## Full Service: Putting It Together

```java
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final SnowflakeIdGenerator idGenerator;
    private final CounterBasedCodeGenerator codeGenerator;
    private final StringRedisTemplate redisTemplate;

    public UrlResponse createShortUrl(CreateUrlRequest request) {
        // 1. Generate unique ID
        long id = idGenerator.nextId();

        // 2. Convert to short code
        String shortCode = codeGenerator.toBase62(id);

        // 3. Handle custom alias
        if (request.customAlias() != null) {
            if (urlRepository.existsByShortCode(request.customAlias())) {
                throw new ConflictException("Alias already taken");
            }
            shortCode = request.customAlias();
        }

        // 4. Persist
        UrlEntity entity = new UrlEntity();
        entity.setShortCode(shortCode);
        entity.setLongUrl(request.longUrl());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(request.expiresAt());
        urlRepository.save(entity);

        // 5. Warm the cache
        redisTemplate.opsForValue().set(shortCode, request.longUrl());

        // 6. Return response
        return new UrlResponse(
            "https://short.ly/" + shortCode,
            request.longUrl(),
            shortCode,
            entity.getCreatedAt(),
            entity.getExpiresAt(),
            0L
        );
    }

    public String getLongUrl(String shortCode) {
        // 1. Check cache
        String cached = redisTemplate.opsForValue().get(shortCode);
        if (cached != null) return cached;

        // 2. Check DB
        UrlEntity entity = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new NotFoundException("URL not found"));

        // 3. Check expiration
        if (entity.getExpiresAt() != null
                && entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GoneException("URL has expired");
        }

        // 4. Backfill cache
        redisTemplate.opsForValue().set(shortCode, entity.getLongUrl(),
            Duration.ofHours(24));

        return entity.getLongUrl();
    }
}
```

---

## 🎤 Interview Tip

> Walk through **two** approaches (hash-based and counter-based), then pick one
> and justify it. This shows you evaluate trade-offs rather than just memorizing.
>
> *"I considered hashing with collision resolution, but for our scale I'd prefer
> a Snowflake-based counter converted to Base62 — it gives us guaranteed
> uniqueness without needing to check for collisions on every write."*

---

*Next: [07 - Caching Strategy →](./07-caching.md)*
