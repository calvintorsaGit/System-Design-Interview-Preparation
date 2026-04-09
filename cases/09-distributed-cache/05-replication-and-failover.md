# 🔁 Step 5: Replication & Failover

---

## Master-Replica Replication

```
┌──────────┐    async replication    ┌──────────┐
│  Master  │ ───────────────────────► │ Replica  │
│  (R/W)   │                          │  (Read)  │
└──────────┘                          └──────────┘
```

- **Writes** go to master only
- **Reads** can go to replicas (eventually consistent)
- Replication is **asynchronous** — a small lag is possible

---

## Failover with Sentinel

```
┌──────────┐    ┌──────────┐    ┌──────────┐
│ Sentinel │    │ Sentinel │    │ Sentinel │      (monitors master health)
│    #1    │    │    #2    │    │    #3    │
└────┬─────┘    └────┬─────┘    └────┬─────┘
     │               │               │
     └───────────────┼───────────────┘
                     │ monitors
                     ▼
              ┌──────────┐           ┌──────────┐
              │  Master  │ ──rep───► │ Replica  │
              └──────────┘           └──────────┘
```

When master dies:
1. Sentinels detect failure (quorum = 2/3 agree)
2. Promote replica to master
3. Redirect clients to new master
4. When old master recovers → becomes replica

---

## Split-Brain Problem

```
Network partition splits the cluster:

  Side A:                    Side B:
  ┌──────────┐              ┌──────────┐
  │  Master  │   ╳ ╳ ╳ ╳   │ Replica  │
  │ (still   │              │(promoted │
  │  thinks  │              │ to new   │
  │  it's    │              │ master)  │
  │  master) │              │          │
  └──────────┘              └──────────┘

  Both accept writes → DATA DIVERGENCE!
```

### Mitigation
- **Minimum replicas to write**: Master refuses writes if it can't reach a quorum of replicas
- When partition heals: one side's writes are lost (last-write-wins or conflict resolution)

---

## 🎤 Interview Tip

> *"I'd use master-replica replication for read scaling and fault tolerance.
> Sentinel provides automatic failover. The trade-off is: async replication
> means a small window of data loss during failover. For stronger guarantees,
> you'd need synchronous replication, which adds latency."*

---

*Next: [06 — Cache Patterns →](./06-cache-patterns.md)*
