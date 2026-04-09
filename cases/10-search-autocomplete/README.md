# Case 10: Search Autocomplete (like Google)

> Type-ahead search — tests Trie data structures, ranking, and real-time updates at scale.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | Prefix-based suggestions, top-K ranking |
| [02-high-level-design.md](./02-high-level-design.md) | Trie service, Elasticsearch, ranking pipeline |
| [03-trie-data-structure.md](./03-trie-data-structure.md) | Trie implementation, prefix lookup, top-K |
| [04-ranking-and-updates.md](./04-ranking-and-updates.md) | Frequency-based ranking, real-time updates |
| [05-scaling.md](./05-scaling.md) | Sharding tries, caching hot prefixes |
| [06-tradeoffs-and-failures.md](./06-tradeoffs-and-failures.md) | Trie vs Elasticsearch, update freshness |
| [07-interview-script.md](./07-interview-script.md) | How to present this |

---

## 🎯 Key Concepts

- ✅ **Trie (Prefix Tree)** — O(L) prefix lookup
- ✅ **Top-K Algorithm** — ranking suggestions by frequency
- ✅ **Elasticsearch** — alternative to custom Trie
- ✅ **Real-time Updates** — incorporating new search trends
- ✅ **Prefix Caching** — cache results for popular prefixes
- ✅ **Trie Sharding** — distributing across nodes

---

## ⏱️ Estimated Study Time: 2-3 hours

Start with `01-requirements.md` and work through in order.
