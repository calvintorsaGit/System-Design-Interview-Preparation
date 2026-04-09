# 📈 Step 5: Scaling

---

## Caching Hot Prefixes (Redis)

Most users type similar prefixes. Cache the top ones:

```
Redis:
  suggest:a   → ["amazon", "apple", "airbnb", "asos", "adidas"]
  suggest:am  → ["amazon", "amc", "american airlines", ...]
  suggest:ama → ["amazon", "amazon prime", "amazon music", ...]
```

**Hit rate**: ~80% of autocomplete requests hit the top 10K prefixes.

```java
public List<String> autocomplete(String prefix) {
    // 1. Check Redis cache
    List<String> cached = redis.opsForList().range("suggest:" + prefix, 0, 4);
    if (cached != null && !cached.isEmpty()) return cached;

    // 2. Cache miss → query Trie
    List<String> results = trieStore.query(prefix);

    // 3. Cache for 15 min
    redis.opsForList().leftPushAll("suggest:" + prefix, results);
    redis.expire("suggest:" + prefix, Duration.ofMinutes(15));

    return results;
}
```

---

## Sharding the Trie

When 1 Trie node can't hold all data (> 100 GB):

| Strategy | How | Pros | Cons |
|----------|-----|------|------|
| **By prefix range** | Shard 1: a-f, Shard 2: g-m, Shard 3: n-z | Simple routing | Uneven (some letters more popular) |
| **By hash** | `hash(prefix) % N` | Even distribution | Can't do range queries |
| **Weighted split** | Based on actual traffic | Best balance | Complex rebalancing |

```
Shard 1: a-g     (30% of queries)
Shard 2: h-n     (25% of queries)
Shard 3: o-s     (25% of queries)
Shard 4: t-z     (20% of queries)
```

---

## Client-Side Optimizations

| Optimization | How |
|--------------|-----|
| **Debounce** | Wait 200ms after last keystroke before sending request |
| **Local cache** | Cache recent prefix results in browser/app memory |
| **Speculative prefetch** | When user types "hel", prefetch "hell" and "help" |
| **Abort stale requests** | If user types "h", "he", "hel" fast, cancel "h" and "he" requests |

---

*Next: [06 — Trade-offs & Failures →](./06-tradeoffs-and-failures.md)*
