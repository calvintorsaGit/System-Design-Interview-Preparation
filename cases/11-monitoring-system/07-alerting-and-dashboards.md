# 🔔 Step 7: Alerting & Dashboards

---

## SLI / SLO / SLA — The Foundation of Alerting

| Term | Definition | Example |
|------|-----------|---------|
| **SLI** (Service Level Indicator) | A metric that measures service quality | 99.2% of requests return < 200ms |
| **SLO** (Service Level Objective) | The target value for an SLI | "99.9% of requests must succeed" |
| **SLA** (Service Level Agreement) | A contract with consequences if SLO is violated | "If uptime < 99.9%, customer gets credit" |

### The Four Golden Signals (Google SRE)

| Signal | What It Measures | Alert When |
|--------|-----------------|------------|
| **Latency** | Time to serve a request | p99 > 500ms for 5 min |
| **Traffic** | Requests per second | Sudden drop > 50% (possible outage) |
| **Errors** | Failure rate | Error rate > 1% for 5 min |
| **Saturation** | Resource utilization | CPU > 85% for 10 min |

---

## Alerting Architecture

```
┌──────────────┐
│ Elasticsearch│──► Watcher (log-based rules)
│ (Logs)       │    "More than 100 ERROR logs in 5 min"
└──────────────┘
                           │
┌──────────────┐           │
│ Prometheus   │──► AlertManager
│ (Metrics)    │    "p99 latency > 500ms for 5 min"
└──────────────┘           │
                           ▼
                  ┌─────────────────┐
                  │  Notification   │
                  │  Router         │
                  └────────┬────────┘
                           │
            ┌──────────────┼──────────────┐
            ▼              ▼              ▼
       ┌─────────┐  ┌──────────┐  ┌──────────┐
       │  Slack  │  │ PagerDuty│  │  Email   │
       │  #alerts│  │ (on-call)│  │          │
       └─────────┘  └──────────┘  └──────────┘
```

### Prometheus Alert Rule Example

```yaml
# prometheus-rules.yml
groups:
  - name: order-service
    rules:
      - alert: HighErrorRate
        expr: |
          rate(http_requests_total{service="order-service",status=~"5.."}[5m])
          /
          rate(http_requests_total{service="order-service"}[5m])
          > 0.01
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Order service error rate > 1%"
          description: "Error rate is {{ $value | humanizePercentage }}"

      - alert: HighLatency
        expr: |
          histogram_quantile(0.99, 
            rate(http_request_duration_seconds_bucket{service="order-service"}[5m])
          ) > 0.5
        for: 5m
        labels:
          severity: warning
```

---

## Alert Anti-Patterns

| Anti-Pattern | Problem | Solution |
|-------------|---------|----------|
| **Alert fatigue** | Too many alerts → team ignores them | Only alert on actionable conditions |
| **Threshold too sensitive** | Random spikes trigger false alarms | Use `for: 5m` (sustained condition) |
| **No severity levels** | Everything is "critical" | Use `critical` (wake up) vs `warning` (investigate next day) |
| **Alerting on symptoms not causes** | "High CPU" → so what? | Alert on impact: "Error rate > 1%" |

---

## Dashboard Design Best Practices

### The Dashboard Hierarchy

```
Level 1: Overview Dashboard   → "Is everything healthy?" (GREEN/RED per service)
Level 2: Service Dashboard    → "How is Order Service doing?" (latency, errors, throughput)
Level 3: Debug Dashboard      → "What's broken?" (individual endpoint metrics, recent errors)
```

### Example: Service Health Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│  ORDER SERVICE DASHBOARD                    Last 1 hour     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Request Rate         Error Rate          p99 Latency       │
│  ┌──────────┐         ┌──────────┐        ┌──────────┐     │
│  │   📈     │         │   ⚠️     │        │   📈     │     │
│  │ 1.2K/sec │         │  0.3%    │        │  142ms   │     │
│  └──────────┘         └──────────┘        └──────────┘     │
│                                                             │
│  CPU Usage            Memory Usage        Active Pods       │
│  ┌──────────┐         ┌──────────┐        ┌──────────┐     │
│  │   45%    │         │   72%    │        │    5/5   │     │
│  └──────────┘         └──────────┘        └──────────┘     │
│                                                             │
│  Recent Errors (last 15 min)                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ 12:30:45 ERROR  PaymentDeclined  orderId=123        │   │
│  │ 12:28:12 ERROR  ConnectionTimeout  to inventory-svc │   │
│  │ 12:25:01 WARN   SlowQuery  duration=2.3s            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎤 Interview Tip

> *"I'd base alerting on the Four Golden Signals from Google SRE: latency, traffic,
> errors, and saturation. Alerts fire only on sustained conditions (e.g., 5 min)
> to avoid false alarms, and I'd route critical alerts to PagerDuty and warnings
> to Slack. Every alert must be actionable — if the on-call engineer can't
> do anything about it, it shouldn't be an alert."*

---

*Next: [08 — Scaling & Reliability →](./08-scaling-and-reliability.md)*
