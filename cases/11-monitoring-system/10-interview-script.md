# 🎤 Step 10: Interview Script

> Use this as a guide for how to walk through the design in a 45-minute interview.

---

## Opening (2 min)

> *"I'll design a centralized monitoring and logging system like ELK or Datadog.
> Observability has three pillars — logs, metrics, and traces. I'll focus on
> log aggregation with the ELK stack, but explain how metrics (Prometheus)
> and traces (Jaeger) plug into the same architecture."*

## Requirements (3 min)
- 500 services, 250K log events/sec, 10 TB/day
- Full-text search, real-time dashboards, alerting
- Retention: hot (7d), warm (30d), cold (1yr)

## Estimation (3 min)
- 2,500 instances × 100 logs/sec = 250K events/sec
- 250K × 500 bytes = 125 MB/sec = 10 TB/day
- Hot storage: 70 TB SSD, Warm: 300 TB HDD, Cold: S3

## High-Level Design (10 min)
- Draw the four layers:
  1. **Collection**: Filebeat/Fluentd on each node
  2. **Ingestion**: Kafka (buffer) → Logstash (parse/enrich)
  3. **Storage**: Elasticsearch (hot-warm-cold), Prometheus (metrics)
  4. **Presentation**: Kibana (logs), Grafana (metrics), AlertManager

## Deep Dive (20 min)
Pick 2-3 based on interviewer interest:
1. **ELK internals** — inverted index, shard design, index-per-day
2. **Hot-warm-cold** — ILM policy, cost optimization
3. **Alerting** — Four Golden Signals, alert fatigue prevention
4. **Data pipeline** — structured logging, enrichment, PII masking
5. **Distributed tracing** — OpenTelemetry, trace visualization

## Trade-offs & Wrap-up (5 min)
- ELK vs Loki (full indexing vs label-based, cost comparison)
- Kafka as buffer (back-pressure handling, replay capability)
- "Monitoring the monitoring" — separate health checks

---

## Key Phrases to Use

| Concept | Phrase |
|---------|--------|
| Architecture | *"Four layers: collection, ingestion, storage, presentation"* |
| Kafka's role | *"Kafka acts as a shock absorber between log producers and Elasticsearch"* |
| Storage | *"Hot-warm-cold architecture reduces storage costs by 70%"* |
| Alerting | *"I'd base alerts on the Four Golden Signals: latency, traffic, errors, saturation"* |
| Trade-offs | *"ELK excels at log search quality; Loki is 10× cheaper but grep-based"* |

---

> **Golden rule**: This question tests infrastructure maturity and operational thinking.
> Show that you understand **cost**, **reliability**, and **operational burden** —
> not just "put logs in Elasticsearch."
