# 📋 Step 1: Clarify Requirements

---

## Questions to Ask

| Question | Typical Answer |
|----------|---------------|
| What data types? | Strings, hashes, lists, sets |
| Max data size? | Fits in memory (< 100 GB per node) |
| Persistence needed? | Optional (snapshot / AOF) |
| Single node or distributed? | Distributed cluster |
| Read/write ratio? | 80:20 reads |
| Latency target? | < 1ms for single key operations |

---

## ✅ Final Requirements

### Functional
1. **GET / SET** — basic key-value operations
2. **TTL** — automatic key expiration
3. **Data structures** — strings, hashes, lists, sorted sets
4. **Cluster mode** — distribute data across multiple nodes
5. **Replication** — master-replica for fault tolerance
6. **Atomic operations** — INCR, DECR, SETNX

### Non-Functional
1. **Latency**: < 1 ms for GET/SET
2. **Throughput**: 100K+ ops/sec per node
3. **Availability**: survive node failures without downtime
4. **Scalability**: add nodes without downtime (resharding)
5. **Memory efficiency**: maximize useful data per GB of RAM

---

## 🎤 Interview Tip

> *"I'll design a distributed in-memory key-value cache like Redis. The key
> challenges are: distributing keys across nodes using consistent hashing,
> handling node failures through replication, and choosing the right eviction
> policy when memory is full."*

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
