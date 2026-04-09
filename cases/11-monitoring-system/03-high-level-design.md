# 🏛️ Step 3: High-Level Design

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           APPLICATION LAYER                                  │
│                                                                              │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐          │
│  │Service A│  │Service B│  │Service C│  │Service D│  │Service E│  ...       │
│  │(Java)   │  │(Python) │  │(Go)     │  │(Node)   │  │(Java)   │           │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘          │
│       │             │            │             │            │               │
│       └─────────────┴────────────┴─────────────┴────────────┘               │
│                                  │                                           │
│                    Filebeat / Fluentd / OpenTelemetry Agent                  │
│                    (deployed as sidecar or DaemonSet)                        │
└──────────────────────────────────┬───────────────────────────────────────────┘
                                   │
                                   ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           INGESTION LAYER                                    │
│                                                                              │
│                    ┌──────────────────────┐                                  │
│                    │       Kafka          │                                  │
│                    │  (Buffer + Decouple) │                                  │
│                    │  topic: logs         │                                  │
│                    │  topic: metrics      │                                  │
│                    │  topic: traces       │                                  │
│                    └──────────┬───────────┘                                  │
│                               │                                              │
│              ┌────────────────┼────────────────┐                             │
│              ▼                ▼                ▼                              │
│     ┌──────────────┐ ┌──────────────┐ ┌──────────────┐                      │
│     │  Logstash #1 │ │  Logstash #2 │ │  Logstash #3 │                      │
│     │  (parse,     │ │  (parse,     │ │  (parse,     │                      │
│     │   enrich,    │ │   enrich,    │ │   enrich,    │                      │
│     │   transform) │ │   transform) │ │   transform) │                      │
│     └──────┬───────┘ └──────┬───────┘ └──────┬───────┘                      │
│            └────────────────┼────────────────┘                               │
│                             │                                                │
└─────────────────────────────┼────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           STORAGE LAYER                                      │
│                                                                              │
│   ┌─────────────────────────────────────────────────────────────────┐        │
│   │                    Elasticsearch Cluster                        │        │
│   │                                                                 │        │
│   │  ┌──────────┐    ┌──────────┐    ┌──────────┐                  │        │
│   │  │   HOT    │    │   WARM   │    │   COLD   │                  │        │
│   │  │  (SSD)   │    │  (HDD)   │    │  (S3)    │                  │        │
│   │  │  0-7 days│    │ 7-30 days│    │ 30-365   │                  │        │
│   │  │  Fast    │    │  Slower  │    │ Archival │                  │        │
│   │  │  queries │    │  queries │    │  queries │                  │        │
│   │  └──────────┘    └──────────┘    └──────────┘                  │        │
│   └─────────────────────────────────────────────────────────────────┘        │
│                                                                              │
│   ┌─────────────────┐                                                       │
│   │   Prometheus     │  (for metrics — time-series DB)                      │
│   └─────────────────┘                                                       │
│                                                                              │
│   ┌─────────────────┐                                                       │
│   │   Jaeger         │  (for distributed traces)                            │
│   └─────────────────┘                                                       │
└──────────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                                    │
│                                                                              │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                    │
│   │   Kibana     │    │  Grafana    │    │  Alerting   │                    │
│   │  (Log search │    │  (Metrics   │    │  Service    │                    │
│   │   & dashbrd) │    │  dashboards)│    │ (PagerDuty, │                    │
│   │              │    │             │    │  Slack)     │                    │
│   └─────────────┘    └─────────────┘    └─────────────┘                    │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## The Four Layers

| Layer | Responsibility | Components |
|-------|---------------|------------|
| **Collection** | Generate & ship logs/metrics/traces | Filebeat, Fluentd, OpenTelemetry SDK |
| **Ingestion** | Buffer, parse, enrich, transform | Kafka, Logstash |
| **Storage** | Index, store, retain | Elasticsearch, Prometheus, Jaeger |
| **Presentation** | Query, visualize, alert | Kibana, Grafana, PagerDuty |

---

## Why Kafka Between Collection and Processing?

```
Without Kafka:
  500 services → Logstash → Elasticsearch
  If Elasticsearch is slow → Logstash blocks → logs are LOST

With Kafka:
  500 services → Kafka → Logstash → Elasticsearch
  If Elasticsearch is slow → Kafka buffers → no data loss
```

**Kafka provides back-pressure handling and data durability.**

---

## 🎤 Interview Tip

> *"I'd structure the system in four layers: collection, ingestion, storage, and
> presentation. The critical design choice is putting Kafka between collection
> and processing — it acts as a buffer that prevents data loss during downstream
> slowdowns and allows independent scaling of producers and consumers."*

---

*Next: [04 — ELK Deep Dive →](./04-elk-deep-dive.md)*
