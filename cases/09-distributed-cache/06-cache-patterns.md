# 📐 Step 6: Cache Patterns

> How should your application interact with the cache? There are 4 major patterns.

---

## 1. Cache-Aside (Lazy Loading) ✅ Most Common

Application manages the cache explicitly.

```java
public User getUser(String id) {
    // 1. Check cache
    User cached = redis.get("user:" + id);
    if (cached != null) return cached;

    // 2. Cache miss → read from DB
    User user = userRepo.findById(id);

    // 3. Populate cache
    redis.set("user:" + id, user, Duration.ofMinutes(30));

    return user;
}
```

| Pros | Cons |
|------|------|
| Only requested data is cached | Cache miss = slow (DB + cache write) |
| Cache failure doesn't break reads | Data can become stale |

---

## 2. Write-Through

Every write goes to cache AND DB simultaneously.

```java
public void updateUser(User user) {
    userRepo.save(user);                           // Write DB
    redis.set("user:" + user.getId(), user);       // Write cache
}
```

| Pros | Cons |
|------|------|
| Cache always up-to-date | Higher write latency (two writes) |
| No stale data | Caches data that may never be read |

---

## 3. Write-Behind (Write-Back)

Write to cache immediately, async write to DB later.

```java
public void updateUser(User user) {
    redis.set("user:" + user.getId(), user);       // Write cache (fast)
    kafkaTemplate.send("db-sync", user);           // Async DB write
}
```

| Pros | Cons |
|------|------|
| Fastest writes | Data loss risk if cache crashes before DB sync |
| Batch DB writes | Complex implementation |

---

## 4. Refresh-Ahead

Proactively refresh cache entries before they expire.

```
TTL = 60 seconds
Refresh at 50 seconds (before expiry)
→ Background thread reloads from DB
→ Users never see a cache miss
```

---

## Cache Stampede Prevention

**Problem**: Popular key expires → 10,000 concurrent requests all hit the DB simultaneously.

```
Key "hot:data" expires at 12:00:00
   → 10,000 requests at 12:00:01 all see cache miss
   → 10,000 DB queries simultaneously → DB overloaded!
```

### Solution: Distributed Lock

```java
public Data getHotData() {
    Data cached = redis.get("hot:data");
    if (cached != null) return cached;

    // Only ONE thread reloads
    boolean locked = redis.setIfAbsent("lock:hot:data", "1", Duration.ofSeconds(10));
    if (locked) {
        Data fresh = db.loadHotData();
        redis.set("hot:data", fresh, Duration.ofMinutes(5));
        redis.delete("lock:hot:data");
        return fresh;
    } else {
        // Other threads wait and retry
        Thread.sleep(100);
        return getHotData();  // Retry — should be in cache now
    }
}
```

---

## 🎤 Interview Tip

> *"I'd use cache-aside for most read-heavy workloads — it's simple and only
> caches what's actually requested. To prevent cache stampedes on hot keys,
> I'd use a distributed lock so only one thread refreshes the cache while
> others wait briefly."*

---

*Next: [07 — Interview Script →](./07-interview-script.md)*
