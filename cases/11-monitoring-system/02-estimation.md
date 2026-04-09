# 📊 Step 2: Back-of-Envelope Estimation

---

## Log Volume Estimation

```
Services:         500 microservices
Instances:        5 replicas each = 2,500 instances
Log rate:         100 log lines/sec per instance
Total:            2,500 × 100 = 250,000 log lines/sec

Average log size: 500 bytes
Throughput:       250K × 500 B = 125 MB/sec = ~10 TB/day

Retention:
  Hot (7 days):   70 TB  (fast SSD, Elasticsearch)
  Warm (30 days): 300 TB (cheaper storage, compressed)
  Cold (1 year):  3.6 PB (S3 / Glacier, archived)
```

---

## Metrics Volume

```
Metrics per instance: 200 unique metrics (CPU, mem, disk, custom)
Collection interval:  15 seconds
Total metrics:        2,500 × 200 = 500K unique time-series

Data points/sec:      500K / 15 = ~33K data points/sec
Each data point:      ~30 bytes (timestamp + value + labels)
Daily:                33K × 86,400 × 30 B ≈ 85 GB/day
```

---

## Infrastructure Requirements

| Component | Size | Why |
|-----------|------|-----|
| Elasticsearch cluster | 30+ nodes (hot), 20+ (warm) | 70 TB hot data, heavy indexing |
| Kafka cluster | 5+ brokers | 125 MB/sec ingest buffer |
| Logstash / Fluentd | 20+ instances | Parsing 250K events/sec |
| Kibana | 3+ instances (behind LB) | Dashboard queries |
| Alerting Service | 3+ instances | Rule evaluation |

---

## 🎤 Interview Tip

> *"At 250K log lines/sec, we're ingesting 10 TB/day. The biggest cost driver
> is Elasticsearch storage — I'd use a hot-warm-cold architecture to keep
> only 7 days on fast SSDs, with older data on cheaper storage."*

---

*Next: [03 — High-Level Design →](./03-high-level-design.md)*
