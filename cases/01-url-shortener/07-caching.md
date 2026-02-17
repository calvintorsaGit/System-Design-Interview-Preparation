# 🚀 Step 7: Caching Strategy

> In a read-heavy system (100:1 read-to-write ratio), caching is **critical**.
> Without it, your database will melt.

---

## 1. Where to Cache?

We place the cache **between the application and the database**.

```
       App Server
           │
     Check Cache?
     ┌─────┴─────┐
     ▼           ▼
   (Hit)       (Miss)
  Return      Read DB
  Value       Result
                 │
              Write to
               Cache
```

- **Technology**: Redis (fast, persistent, supports structures) or Memcached (simple, volatile).
- **Recommendation**: **Redis** because it supports replication, persistence, and advanced data structures (like HyperLogLog for analytics).

---

## 2. Caching Pattern: Cache-Aside (Lazy Loading)

This is the standard pattern for this use case.

### How It Works
1. App receives request for `shortCode`.
2. App checks Redis.
   - **Hit**: Return URL immediately.
   - **Miss**: Query Database → return URL → write to Redis with TTL.

### Spring Boot Implementation

```java
@Service
public class UrlService {

    private final StringRedisTemplate redis;
    private final UrlRepository db;

    public String getUrl(String shortCode) {
        // 1. Check Cache
        String cachedUrl = redis.opsForValue().get(shortCode);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        // 2. Check Database
        UrlEntity entity = db.findByShortCode(shortCode)
            .orElseThrow(() -> new NotFoundException());

        // 3. Update Cache (with TTL)
        redis.opsForValue().set(shortCode, entity.getLongUrl(), Duration.ofHours(24));

        return entity.getLongUrl();
    }
}
```

---

## 3. Eviction Policy

When Redis is full, what do we remove?

- **LRU (Least Recently Used)**: The best policy for this system.
- Why? We want to keep "hot" URLs (viral links) in memory and drop links no one has clicked in months.

**Configuration:**
```properties
# redis.conf
maxmemory-policy allkeys-lru
```

---

## 4. Cache Stampede (Thundering Herd)

### Problem
If a "viral" celebrity tweet link expires in the cache, thousands of requests hit the DB simultaneously.

### Solution 1: Mutex Lock
Only one request is allowed to rebuild the cache key. Others wait.

```java
// Conceptual Logic
String val = cache.get(key);
if (val == null) {
    if (lock.tryLock(key)) {
        val = db.get(key);
        cache.set(key, val);
        lock.release();
    } else {
        sleep(50);
        retry();
    }
}
```

### Solution 2: Logical Expiration (Better for High Scale)
Store the object with a `logical_ttl` field inside.
- If `logical_ttl` is expired:
  - Serve the **stale** data immediately (availability > strict consistency).
  - Fire an async background task to refresh the cache.

---

## 5. Cache Warming

When a new URL is created, should we cache it immediately?

- **Yes (Write-Throughish)**:
  - When `createShortUrl()` is called, we write to DB *and* Redis.
  - Why? The user who created the link will likely click it immediately to test it.

```java
public UrlResponse createShortUrl(...) {
    // ... support logic ...
    UrlEntity saved = repository.save(entity);

    // Pre-populate cache because user will likely click it soon
    redis.opsForValue().set(saved.getShortCode(), saved.getLongUrl(), Duration.ofHours(24));
    
    return response;
}
```

---

## 🎤 Interview Tip

> Don't just say "I'll use Redis." Say:
> *"I'll use the **Cache-Aside** pattern with an **LRU eviction policy** to keep viral links in memory. 
> To prevent cache stampedes on hot links, I'd implement **logical expiration** or a **mutex lock** mechanism."*

---

*Next: [08 - Trade-offs & Failure Scenarios →](./08-tradeoffs-and-failures.md)*
