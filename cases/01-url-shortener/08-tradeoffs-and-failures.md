# ⚖️ Step 8: Trade-offs & Failure Scenarios

> Be honest about what breaks. Every system has limits.

---

## 1. Trade-offs We Made

| Decision | Why we chose it | The Downside |
|----------|----------------|--------------|
| **SQL Database** | Structured data, easy to manage | Harder to scale writes horizontally than NoSQL |
| **302 Redirect** | Allows tracking analytics | Slower for user (server round-trip) |
| **Snowflake IDs** | No collisions, distributed | Complex setup (ZooKeeper/etc) |

---

## 2. Failure Scenarios

### What if the Cache fails?
- **Impact:** Massive load hits the DB.
- **Fix:** Replica nodes (Redis Sentinel) + Limit DB connections so it doesn't crash.

### What if the DB shard fails?
- **Impact:** Some URLs become unavailable.
- **Fix:** Master-Slave replication. Promote slave to master.

### What if the ID Generator fails?
- **Impact:** Cannot create new URLs.
- **Fix:** Run multiple ID generators. If one fails, load balancer routes to others.

---

*Next: [09 - Interview Script →](./09-interview-script.md)*
