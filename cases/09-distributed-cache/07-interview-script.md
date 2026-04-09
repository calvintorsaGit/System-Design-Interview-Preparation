# 🎤 Step 7: Interview Script

---

## Opening (2 min)
> *"I'll design a distributed in-memory cache like Redis. The three core challenges
> are: distributing keys across nodes with consistent hashing, choosing the right
> eviction policy, and handling node failures through replication."*

## Requirements (3 min)
- In-memory KV store, < 1ms latency, 100K+ ops/sec per node
- TTL support, cluster mode, replication

## High-Level Design (10 min)
- Single node: hash table + event loop (single-threaded)
- Cluster: 16384 hash slots across N masters, each with a replica
- Client routes to correct node via slot mapping

## Deep Dive (20 min)
Pick 2-3:
1. **Consistent hashing** — virtual ring, adding/removing nodes
2. **LRU eviction** — HashMap + doubly linked list, O(1) operations
3. **Replication & failover** — Sentinel, split-brain
4. **Cache patterns** — aside vs through vs behind, stampede prevention

## Trade-offs (5 min)
- Sync vs async replication (latency vs durability)
- LRU vs LFU (general vs frequency-biased)
- In-memory vs disk persistence (speed vs durability)

---

> **Differentiator**: Implement `ConsistentHashRing` or `LRUCache` in code.
> Interviewers rarely expect code for system design, so showing it is impressive.
