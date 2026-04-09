# Case 9: Distributed Cache (like Redis)

> Build the tool you've been using in every other case study. Understand consistent hashing,
> eviction policies, and replication from the inside.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | In-memory KV store, TTL, cluster mode |
| [02-high-level-design.md](./02-high-level-design.md) | Single node → sharded cluster architecture |
| [03-consistent-hashing.md](./03-consistent-hashing.md) | How to distribute keys across nodes evenly |
| [04-eviction-policies.md](./04-eviction-policies.md) | LRU, LFU, TTL — what to delete when memory is full |
| [05-replication-and-failover.md](./05-replication-and-failover.md) | Master-replica, sentinel, split-brain |
| [06-cache-patterns.md](./06-cache-patterns.md) | Cache-aside, write-through, write-behind, stampede prevention |
| [07-interview-script.md](./07-interview-script.md) | How to present this |

---

## 🎯 Key Concepts

- ✅ **Consistent Hashing** — distributing keys across nodes
- ✅ **Eviction Policies** — LRU, LFU, Random, TTL
- ✅ **Replication** — master-replica for high availability
- ✅ **Cache Patterns** — aside, through, behind, refresh-ahead
- ✅ **Cache Stampede** — thundering herd prevention
- ✅ **Split-Brain** — network partition handling

---

## ⏱️ Estimated Study Time: 2-3 hours

Start with `01-requirements.md` and work through in order.
