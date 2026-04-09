# 🏛️ Step 2: High-Level Design

---

## Single Node Architecture

```
┌─────────────────────────────────────────┐
│            Cache Server                  │
│                                          │
│   ┌──────────────────────────────────┐  │
│   │         Hash Table               │  │
│   │   key1 → value1                  │  │
│   │   key2 → value2                  │  │
│   │   key3 → value3                  │  │
│   └──────────────────────────────────┘  │
│                                          │
│   ┌───────────┐   ┌──────────────────┐  │
│   │  Event    │   │  Expiry Thread   │  │
│   │  Loop     │   │  (lazy + active) │  │
│   │ (single   │   │                  │  │
│   │ threaded) │   │                  │  │
│   └───────────┘   └──────────────────┘  │
└─────────────────────────────────────────┘
```

### Why Single-Threaded?
Redis processes commands in a **single thread** (event loop):
- No locks, no context switching
- 100K+ ops/sec because operations are all in-memory (microseconds each)
- Network I/O is the bottleneck, not CPU

---

## Distributed Architecture (Cluster Mode)

```
Client
  │
  │ hash(key) % slots → which node?
  ▼
┌─────────────────────────────────────────────────────┐
│                  Hash Slot Ring                       │
│    Slots 0-5460    Slots 5461-10922  Slots 10923-16383│
│         │              │                 │            │
│         ▼              ▼                 ▼            │
│    ┌─────────┐   ┌─────────┐      ┌─────────┐       │
│    │ Node A  │   │ Node B  │      │ Node C  │       │
│    │(master) │   │(master) │      │(master) │       │
│    └────┬────┘   └────┬────┘      └────┬────┘       │
│         │             │                │             │
│    ┌────┴────┐   ┌────┴────┐      ┌────┴────┐       │
│    │Replica  │   │Replica  │      │Replica  │       │
│    │  A'     │   │  B'     │      │  C'     │       │
│    └─────────┘   └─────────┘      └─────────┘       │
└─────────────────────────────────────────────────────┘
```

### How Redis Cluster Routes Keys
1. Total of **16384 hash slots**
2. `slot = CRC16(key) % 16384`
3. Each master node owns a range of slots
4. Client libraries know the slot-to-node mapping
5. If a key maps to another node → `MOVED` redirect

---

## Adding/Removing Nodes (Resharding)

When adding Node D:
1. Assign some slots from A, B, C to D
2. Migrate keys from those slots to D
3. Update slot mapping
4. **Zero downtime** — during migration, redirects handle in-flight requests

---

## 🎤 Interview Tip

> *"A single Redis node handles 100K+ ops/sec because it's single-threaded
> with all data in memory. To scale beyond one node, I'd use cluster mode
> with 16384 hash slots distributed across nodes, each with a replica for
> fault tolerance."*

---

*Next: [03 — Consistent Hashing →](./03-consistent-hashing.md)*
