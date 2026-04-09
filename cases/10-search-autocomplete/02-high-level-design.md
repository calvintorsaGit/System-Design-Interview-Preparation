# 🏛️ Step 2: High-Level Design

---

## Architecture Diagram

```
User types: "hel"
     │
     ▼
┌──────────────┐
│  API Gateway │
└──────┬───────┘
       │
       ▼
┌──────────────┐       ┌────────────────┐
│  Suggestion  │──────►│  Prefix Cache  │   (Redis: "hel" → [hello, help, ...])
│   Service    │       │    (Redis)     │
└──────┬───────┘       └────────────────┘
       │ cache miss
       ▼
┌──────────────┐
│  Trie Store  │    (In-memory Trie, or Elasticsearch prefix query)
│              │
└──────┬───────┘
       │
       │ periodic rebuild
       ▼
┌──────────────┐       ┌────────────────┐
│  Aggregation │◄──────│  Query Logs    │
│   Service    │       │  (Kafka → S3)  │
│  (MapReduce) │       └────────────────┘
└──────────────┘
```

---

## Data Flow

### Read Path (autocomplete query)
```
1. User types "hel" → API call to Suggestion Service
2. Check Redis cache for prefix "hel"
3. Cache hit → return cached top-5 suggestions
4. Cache miss → query Trie Store → return results → cache in Redis
```

### Write Path (updating trends)
```
1. Every search query is logged to Kafka
2. Aggregation service processes logs every 5-15 min
3. Updates frequency counts in Trie Store
4. Invalidates stale cache entries
```

---

## Two Approaches to the Trie Store

| | Custom In-Memory Trie | Elasticsearch |
|---|---|---|
| **Latency** | < 1ms (in-memory) | 5-20ms |
| **Complexity** | Build and maintain yourself | Managed service |
| **Scaling** | Manual sharding by prefix | Built-in sharding |
| **Features** | Basic prefix + frequency | Fuzzy matching, multi-language, boosting |
| **Best for** | Pure autocomplete at extreme scale | Autocomplete + search combined |

---

## 🎤 Interview Tip

> *"I'd separate the system into a fast read path (Trie + Redis cache) and
> an async write path (Kafka → aggregation → Trie rebuild). The read path
> handles 600K requests/sec using heavy caching. The write path updates
> trends every 15 minutes without impacting read latency."*

---

*Next: [03 — Trie Data Structure →](./03-trie-data-structure.md)*
