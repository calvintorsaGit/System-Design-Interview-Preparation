# 💾 Step 5: Database Design

> The database is the heart of a URL shortener. This is where most interview
> deep-dive questions will focus.

---

## Schema Design

### Core Table: `urls`

```sql
CREATE TABLE urls (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_code   VARCHAR(10)   NOT NULL UNIQUE,
    long_url     VARCHAR(2048) NOT NULL,
    user_id      BIGINT,                            -- nullable (anonymous users)
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at   TIMESTAMP,                          -- nullable (no expiration)
    click_count  BIGINT        NOT NULL DEFAULT 0
);

-- Fast lookup by short code (the most frequent query)
CREATE UNIQUE INDEX idx_short_code ON urls(short_code);

-- Find expired URLs for cleanup
CREATE INDEX idx_expires_at ON urls(expires_at) WHERE expires_at IS NOT NULL;

-- User's URLs
CREATE INDEX idx_user_id ON urls(user_id) WHERE user_id IS NOT NULL;
```

### Spring Boot Entity

```java
@Entity
@Table(name = "urls")
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    // constructors, getters, setters...
}
```

### Repository

```java
@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    Optional<UrlEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT u FROM UrlEntity u WHERE u.expiresAt < :now")
    List<UrlEntity> findExpiredUrls(@Param("now") LocalDateTime now);
}
```

---

## SQL vs NoSQL — The Big Trade-off

| Criteria | SQL (PostgreSQL / MySQL) | NoSQL (DynamoDB / Cassandra) |
|----------|--------------------------|------------------------------|
| **Schema** | Fixed schema, structured data | Flexible, key-value oriented |
| **Lookup speed** | Fast with index on `short_code` | Native key-value, very fast |
| **Scaling** | Vertical first, then sharding | Horizontal by design |
| **Joins** | Supported (user → urls) | Not supported / expensive |
| **Transactions** | Full ACID | Eventual consistency (usually) |
| **Best for** | < 1B records, need analytics | > 1B records, simple lookups |

### 💡 Recommendation for Interviews

> **Start with SQL** (PostgreSQL). It's simpler to explain, supports analytics
> queries, and handles our scale (6B records with sharding).
>
> Then mention: *"If we need to scale beyond what sharded PostgreSQL handles,
> we could migrate the hot path (short_code → long_url lookups) to DynamoDB
> for single-digit-millisecond reads."*

---

## Database Sharding

At 6 Billion URLs, a single database won't perform well. We need to **shard** (split data across multiple databases).

### Strategy 1: Hash-Based Sharding (Recommended ✅)

```
shard_id = hash(short_code) % number_of_shards
```

```
            short_code = "abc123"
                    │
         hash("abc123") % 4 = 2
                    │
    ┌───────┬───────┼───────┬───────┐
    ▼       ▼       ▼       ▼       
  Shard0  Shard1  Shard2  Shard3
                    │
              ┌─────┴─────┐
              │ abc123 →   │
              │ long_url   │
              └────────────┘
```

**Pros:** Even distribution, simple  
**Cons:** Adding shards requires re-hashing (mitigated by consistent hashing)

### Strategy 2: Range-Based Sharding

```
Shard A: short_codes starting with a-f
Shard B: short_codes starting with g-m
Shard C: short_codes starting with n-s
Shard D: short_codes starting with t-z, 0-9
```

**Pros:** Simple to understand, range queries possible  
**Cons:** Uneven distribution (hotspot problem)

### 💡 Interview Answer

> *"I'd use hash-based sharding on the short_code because it gives us even 
> distribution. To handle shard additions gracefully, I'd use consistent 
> hashing so we only need to redistribute a fraction of data."*

---

## Handling the Click Counter

Incrementing `click_count` on every redirect creates a **write bottleneck** on a read-heavy path.

### Solutions (pick one):

| Approach | How It Works | Trade-off |
|----------|-------------|-----------|
| **Direct DB update** | `UPDATE urls SET click_count = click_count + 1` | Simple but creates DB write on every read |
| **Batch counter in Redis** | Increment in Redis, flush to DB every N minutes | Fast reads, slightly delayed count |
| **Async event + Kafka** | Emit a click event, consumer updates count | Fully decoupled, most scalable |

### Spring Boot: Redis Counter Approach

```java
@Service
public class ClickCounterService {

    private final StringRedisTemplate redis;
    private final UrlRepository urlRepository;

    private static final String CLICK_PREFIX = "clicks:";

    // Called on every redirect — fast, no DB write
    public void recordClick(String shortCode) {
        redis.opsForValue().increment(CLICK_PREFIX + shortCode);
    }

    // Scheduled job: flush to DB every 5 minutes
    @Scheduled(fixedRate = 300_000)
    public void flushClicksToDB() {
        Set<String> keys = redis.keys(CLICK_PREFIX + "*");
        if (keys == null) return;

        for (String key : keys) {
            String shortCode = key.replace(CLICK_PREFIX, "");
            String countStr = redis.opsForValue().getAndDelete(key);
            if (countStr != null) {
                long clicks = Long.parseLong(countStr);
                urlRepository.incrementClickCount(shortCode, clicks);
            }
        }
    }
}
```

---

## Expired URL Cleanup

```java
@Component
public class UrlCleanupJob {

    private final UrlRepository urlRepository;
    private final StringRedisTemplate redis;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredUrls() {
        List<UrlEntity> expired = urlRepository.findExpiredUrls(LocalDateTime.now());

        for (UrlEntity url : expired) {
            redis.delete(url.getShortCode());       // remove from cache
            urlRepository.delete(url);               // remove from DB
        }
    }
}
```

---

## 🎤 Interview Tip

> A strong candidate addresses **all three concerns**:
> 1. ✅ Schema + indexing strategy
> 2. ✅ SQL vs NoSQL trade-off with a clear recommendation
> 3. ✅ Sharding strategy for scale
>
> Bonus points for mentioning the click counter write problem and how to solve it.

---

*Next: [06 - Core Algorithm →](./06-core-algorithm.md)*
