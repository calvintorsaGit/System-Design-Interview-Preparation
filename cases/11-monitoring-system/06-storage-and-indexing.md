# 💾 Step 6: Storage & Indexing

---

## Hot-Warm-Cold Architecture

The key cost optimization: **not all data needs the same storage performance**.

```
┌────────────────┐    ┌────────────────┐    ┌────────────────┐
│     HOT        │    │     WARM       │    │     COLD       │
│                │    │                │    │                │
│ Age: 0-7 days  │    │ Age: 7-30 days │    │ Age: 30-365    │
│ Storage: SSD   │    │ Storage: HDD   │    │ Storage: S3    │
│ Replicas: 1    │    │ Replicas: 1    │    │ Replicas: 0    │
│ Queries: Fast  │    │ Queries: OK    │    │ Queries: Slow  │
│                │    │                │    │                │
│ Active writes  │    │ Read-only      │    │ Rarely queried │
│ + reads        │    │ Compressed     │    │ Frozen index   │
└───────┬────────┘    └───────┬────────┘    └───────┬────────┘
        │                     │                     │
        │     ILM moves data automatically          │
        └─────────────────────┴─────────────────────┘
```

### Index Lifecycle Management (ILM)

```json
// Elasticsearch ILM Policy
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_size": "50gb"
          }
        }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "shrink": { "number_of_shards": 1 },
          "forcemerge": { "max_num_segments": 1 },
          "allocate": { "require": { "data": "warm" } }
        }
      },
      "cold": {
        "min_age": "30d",
        "actions": {
          "searchable_snapshot": {
            "snapshot_repository": "s3-repo"
          }
        }
      },
      "delete": {
        "min_age": "365d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

---

## Elasticsearch Index Design for Logs

### Index Mapping

```json
{
  "mappings": {
    "properties": {
      "@timestamp":   { "type": "date" },
      "level":        { "type": "keyword" },
      "service":      { "type": "keyword" },
      "instance":     { "type": "keyword" },
      "environment":  { "type": "keyword" },
      "message":      { "type": "text", "analyzer": "standard" },
      "trace_id":     { "type": "keyword" },
      "stack_trace":  { "type": "text",    "index": false },
      "response_time":{ "type": "integer" }
    }
  }
}
```

| Type | When to Use |
|------|-------------|
| `keyword` | Exact match, filtering, aggregation (service name, log level) |
| `text` | Full-text search (log message, stack trace) |
| `date` | Time-based queries and sorting |
| `integer/long` | Numeric range queries (response time) |

### Shard Sizing Best Practices

| Guideline | Recommendation |
|-----------|---------------|
| Shard size | 10-50 GB per shard |
| Shards per index | 1 shard per 25 GB of expected data |
| Shards per node | Max 20 shards per GB of heap |
| Index per day | Allows easy deletion of old data |

---

## Metrics Storage: Prometheus (Time-Series DB)

Unlike logs (text), metrics are **numeric time-series**:

```
# Prometheus metric format
http_requests_total{service="order-service", status="200"} 15234
http_requests_total{service="order-service", status="500"} 42

http_request_duration_seconds{service="order-service", quantile="0.99"} 0.250
```

### Why Not Use Elasticsearch for Metrics?

| | Elasticsearch | Prometheus |
|---|---|---|
| Optimized for | Full-text search | Numeric time-series |
| Query language | KQL, Lucene | PromQL |
| Storage efficiency | ~10 bytes/value | ~1-2 bytes/value (compressed) |
| Aggregation speed | Fast for text | **Extremely fast for math** |

> **Rule of thumb**: Logs → Elasticsearch, Metrics → Prometheus, Traces → Jaeger

---

## 🎤 Interview Tip

> *"I'd use hot-warm-cold architecture with ILM to manage costs. Fresh data on SSDs
> for fast queries, 7-day-old data on HDDs (compressed, read-only), and 30+ day data
> as searchable snapshots on S3. This reduces storage costs by ~70% compared to
> keeping everything on SSDs."*

---

*Next: [07 — Alerting & Dashboards →](./07-alerting-and-dashboards.md)*
