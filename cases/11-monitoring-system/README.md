# Case 11: Centralized Monitoring & Logging System (like ELK / Datadog)

> "If you can't measure it, you can't improve it." — This is one of the most practical system design questions
> because **every production system needs monitoring**. Interviewers love it because it tests infrastructure thinking.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | Clarifying scope: logs vs metrics vs traces, functional & non-functional requirements |
| [02-estimation.md](./02-estimation.md) | Back-of-envelope math: log volume, storage, ingestion throughput |
| [03-high-level-design.md](./03-high-level-design.md) | Architecture diagram: collection → ingestion → storage → querying → alerting |
| [04-elk-deep-dive.md](./04-elk-deep-dive.md) | Elasticsearch, Logstash, Kibana — how each component works and fits together |
| [05-data-pipeline.md](./05-data-pipeline.md) | Log shipping, parsing, enrichment, and indexing pipeline |
| [06-storage-and-indexing.md](./06-storage-and-indexing.md) | Inverted indexes, time-series data, retention policies, hot-warm-cold architecture |
| [07-alerting-and-dashboards.md](./07-alerting-and-dashboards.md) | Rule-based vs ML-based alerting, dashboard design, SLO/SLI/SLA |
| [08-scaling-and-reliability.md](./08-scaling-and-reliability.md) | Scaling Elasticsearch, back-pressure, data loss prevention, multi-tenancy |
| [09-tradeoffs-and-alternatives.md](./09-tradeoffs-and-alternatives.md) | ELK vs Prometheus+Grafana vs Datadog vs Loki, when to use what |
| [10-interview-script.md](./10-interview-script.md) | How to present this in a 45-min system design interview |

---

## 🎯 Key Concepts You'll Master

- ✅ Three Pillars of Observability: Logs, Metrics, Traces
- ✅ ELK Stack architecture (Elasticsearch, Logstash, Kibana)
- ✅ Inverted index & full-text search
- ✅ Time-series data storage patterns
- ✅ Log ingestion pipelines (Kafka + Logstash/Fluentd)
- ✅ Hot-Warm-Cold storage architecture
- ✅ Alerting systems & anomaly detection
- ✅ SLI / SLO / SLA definitions
- ✅ Back-pressure and flow control
- ✅ Distributed tracing (OpenTelemetry, Jaeger)

---

## ⏱️ Estimated Study Time: 3-4 hours

Start with `01-requirements.md` and work through in order.
