# 📈 Step 8: Scaling & Reliability

---

## Scaling Elasticsearch

### Horizontal Scaling

```
Problem: Single ES node can't handle 250K docs/sec + queries

Solution: Sharding across multiple data nodes

Index: logs-2026.04.09
  ├── Shard 0 → Data Node #1
  ├── Shard 1 → Data Node #2
  ├── Shard 2 → Data Node #3
  ├── Shard 3 → Data Node #4
  └── Shard 4 → Data Node #5

Write: distributed across 5 shards = 5× throughput
Query: scatter-gather across all shards, merge results
```

### Scaling Strategy

| Component | Scaling Approach |
|-----------|-----------------|
| **Filebeat/Fluentd** | DaemonSet (one per K8s node, auto-scales) |
| **Kafka** | Add brokers, increase partitions |
| **Logstash** | Add more consumer instances (consumer group) |
| **Elasticsearch** | Add data nodes, increase shards |
| **Kibana** | Multiple instances behind LB |

---

## Back-Pressure Handling

What happens when Elasticsearch can't keep up?

```
Normal:    Filebeat → Kafka → Logstash → Elasticsearch
           100K/sec   100K/sec  100K/sec    100K/sec ✅

Overload:  Filebeat → Kafka → Logstash → Elasticsearch
           250K/sec   250K/sec  250K/sec    100K/sec ❌ (ES is slow)

Without Kafka: Logstash blocks → Filebeat blocks → LOGS LOST!

With Kafka:    Filebeat → Kafka ──┐ → Logstash → Elasticsearch
               250K/sec   ▼       │    100K/sec    100K/sec
                        Buffer    │
                        (hours    │ When ES catches up,
                         of data) │ Kafka replays backlog
                                  └─→ Logstash → Elasticsearch
```

### Kafka's Role as a Shock Absorber
- **Retention**: Keep 24-72 hours of logs in Kafka
- **Back-pressure**: Logstash consumers pull at their own pace
- **Replayable**: If Logstash has a bug, fix it and replay from Kafka

---

## Multi-Tenancy (Team Isolation)

```
Team A (Order team):    Can only see logs where service IN [order-svc, payment-svc]
Team B (Product team):  Can only see logs where service IN [product-svc, catalog-svc]
Team C (Platform team): Can see ALL logs
```

### Implementation: Elasticsearch Spaces + RBAC

```json
// Kibana Space: "order-team"
{
  "index_pattern": "logs-*",
  "filter": {
    "terms": { "service": ["order-service", "payment-service"] }
  }
}

// Role: "order-team-viewer"
{
  "indices": [{
    "names": ["logs-*"],
    "privileges": ["read"],
    "query": {
      "terms": { "service": ["order-service", "payment-service"] }
    }
  }]
}
```

---

## High Availability

| Component | HA Strategy |
|-----------|-------------|
| Elasticsearch | 3 master nodes (quorum), replica shards |
| Kafka | 3 brokers, replication factor 3 |
| Logstash | Multiple instances in consumer group |
| Kibana | Multiple instances behind load balancer |
| Alerting | Active-passive with leader election |

### Monitoring the Monitoring System 🤔

```
Who watches the watchmen?

→ Use a separate, minimal monitoring system:
  - Simple uptime checks (e.g., Pingdom / UptimeRobot)
  - Lightweight metrics (node_exporter + external Prometheus)
  - PagerDuty direct integration (doesn't depend on your ELK)
```

---

## 🎤 Interview Tip

> *"The most important reliability feature is Kafka as a buffer between collection
> and indexing. It handles back-pressure — if Elasticsearch is temporarily slow,
> logs buffer in Kafka instead of being lost. And yes, I'd monitor the monitoring
> system itself using a separate, minimal health-check service."*

---

*Next: [09 — Trade-offs & Alternatives →](./09-tradeoffs-and-alternatives.md)*
