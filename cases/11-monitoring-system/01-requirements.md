# 1. Requirements — Centralized Monitoring & Logging System

> Step 1 of every system design interview: **clarify what we're building**.

---

## 1.1 The Problem

You're running 500+ microservices across multiple data centers. When something breaks at 3 AM:
- Which service is failing?
- What error is it throwing?
- When did it start?
- Which users are affected?
- What changed right before it broke?

Without centralized monitoring, engineers SSH into individual servers and `grep` through log files. That doesn't scale.

---

## 1.2 Questions to Ask the Interviewer

Always clarify scope before designing. Here are the key questions:

| Question | Why It Matters |
|----------|---------------|
| "Are we building log aggregation, metrics collection, or both?" | These are fundamentally different data models |
| "How many services will produce logs/metrics?" | Determines ingestion throughput |
| "What's the expected log volume per day?" | Drives storage and cost decisions |
| "Do we need real-time alerting or is batch analysis okay?" | Affects latency requirements |
| "How long do we need to retain data?" | 7 days vs 1 year = massive storage difference |
| "Do we need full-text search on logs?" | Determines storage engine choice |
| "Is this for a single team or multi-tenant (many teams)?" | Adds access control complexity |

---

## 1.3 Functional Requirements

| # | Requirement | Description |
|---|-------------|-------------|
| FR-1 | **Log Collection** | Collect structured & unstructured logs from all services |
| FR-2 | **Metrics Collection** | Collect time-series metrics (CPU, memory, request latency, error rates) |
| FR-3 | **Full-Text Search** | Search through billions of log lines by keyword, service, time range |
| FR-4 | **Dashboards** | Visual dashboards showing system health in real-time |
| FR-5 | **Alerting** | Trigger alerts when metrics exceed thresholds or anomalies are detected |
| FR-6 | **Log Correlation** | Trace a single request across multiple services (distributed tracing) |
| FR-7 | **Retention Management** | Automatically archive or delete old data based on policies |

---

## 1.4 Non-Functional Requirements

| # | Requirement | Target |
|---|-------------|--------|
| NFR-1 | **Ingestion Throughput** | Handle 1M+ log events per second |
| NFR-2 | **Search Latency** | < 5 seconds for queries across 7 days of data |
| NFR-3 | **Dashboard Latency** | < 2 seconds to load a dashboard |
| NFR-4 | **Alerting Latency** | < 60 seconds from event to alert |
| NFR-5 | **Availability** | 99.9% (monitoring must be more reliable than what it monitors) |
| NFR-6 | **Data Retention** | Hot: 7 days, Warm: 30 days, Cold: 1 year |
| NFR-7 | **Durability** | No log loss during normal operations; tolerate brief gaps during failures |
| NFR-8 | **Multi-Tenancy** | Isolate data and access per team/service |

---

## 1.5 Out of Scope (for a 45-min interview)

- APM (Application Performance Monitoring) code instrumentation details
- Infrastructure provisioning (Terraform, Ansible)
- Specific cloud vendor integration details
- Security audit logging (related but different domain)

---

## 1.6 The Three Pillars of Observability

Understanding this framework helps you structure your answer:

```
┌─────────────────────────────────────────────────────────────────┐
│                    OBSERVABILITY                                │
│                                                                 │
│   ┌──────────┐      ┌──────────┐      ┌──────────┐            │
│   │   LOGS   │      │ METRICS  │      │  TRACES  │            │
│   │          │      │          │      │          │            │
│   │ "What    │      │ "How     │      │ "Where   │            │
│   │  happened│      │  is the  │      │  did the │            │
│   │  ?"      │      │  system  │      │  request │            │
│   │          │      │  doing?" │      │  go?"    │            │
│   │ Text     │      │ Numbers  │      │ Request  │            │
│   │ events   │      │ over     │      │ flow     │            │
│   │ with     │      │ time     │      │ across   │            │
│   │ context  │      │          │      │ services │            │
│   └──────────┘      └──────────┘      └──────────┘            │
│                                                                 │
│   ELK Stack         Prometheus         Jaeger /                │
│   Splunk            Grafana            Zipkin                  │
│   Datadog Logs      Datadog Metrics    OpenTelemetry           │
└─────────────────────────────────────────────────────────────────┘
```

### 💡 Interview Tip
> Start by saying: *"Monitoring has three pillars — logs, metrics, and traces.
> For this design, I'll focus primarily on **log aggregation** (like ELK), but I'll explain
> how metrics and tracing plug into the same architecture."*

---

*Next: [02 — Estimation →](./02-estimation.md)*
