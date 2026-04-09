# ⚠️ Step 6: Trade-offs & Failure Scenarios

---

## Key Trade-offs

| Decision | Option A | Option B | Our Choice |
|----------|----------|----------|------------|
| Data structure | Custom Trie | Elasticsearch prefix | **Trie** (sub-ms) for pure autocomplete |
| Update model | Real-time mutation | Periodic rebuild | **Periodic rebuild** — safer, consistent snapshots |
| Ranking | Pure frequency | Frequency + recency + personal | **Hybrid** for better quality |
| Storage | In-memory only | Persistent + in-memory | **In-memory** with periodic S3 snapshots |

---

## Failure Scenarios

| What Breaks | Impact | Mitigation |
|-------------|--------|------------|
| Trie server OOM | No suggestions | Monitor memory; horizontal shard |
| Redis cache failure | All requests hit Trie | Trie handles full load (sized for it) |
| Rebuild job fails | Stale suggestions (old Trie) | Keep serving old Trie; alert on-call |
| Kafka lag (query logs) | Trending queries delayed | Acceptable: 15-30 min staleness is OK |
| Offensive query trending | PR disaster | Content filter pipeline on all suggestions |

---

## 🎤 Interview Tip

> *"The system is optimized for reads: 600K QPS served from Redis cache + in-memory Trie.
> Writes are batched — query logs flow through Kafka → aggregated every 15 min → Trie
> rebuilt and atomically swapped. The key insight is separating the read path
> (optimized for latency) from the write path (optimized for throughput)."*

---

*Next: [07 — Interview Script →](./07-interview-script.md)*
