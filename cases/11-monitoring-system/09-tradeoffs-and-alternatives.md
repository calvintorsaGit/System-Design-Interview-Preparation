# ⚖️ Step 9: Trade-offs & Alternatives

---

## ELK vs Alternatives

| Stack | Logs | Metrics | Traces | Cost | Complexity |
|-------|------|---------|--------|------|-----------|
| **ELK** (Elasticsearch + Logstash + Kibana) | ✅ Excellent | ⚠️ Possible but inefficient | ⚠️ Basic | Self-hosted: infra cost | High (operate ES cluster) |
| **PLG** (Promtail + Loki + Grafana) | ✅ Good (label-based) | ✅ (with Prometheus) | ✅ (with Tempo) | Much cheaper than ELK | Medium |
| **Prometheus + Grafana** | ❌ Not for logs | ✅ Excellent | ✅ (with Jaeger) | Low | Low |
| **Datadog** | ✅ Excellent | ✅ Excellent | ✅ Excellent | Very expensive (SaaS) | Very Low |
| **Splunk** | ✅ Excellent | ✅ Good | ✅ Good | Very expensive | Medium |

---

## When to Use What?

| Scenario | Recommended Stack | Why |
|----------|------------------|-----|
| Startup, small team | **Datadog** or **Grafana Cloud** | Don't operate infra, focus on product |
| Medium org, cost-sensitive | **PLG stack** (Loki + Prometheus + Grafana) | Loki is 10× cheaper than ES for logs |
| Large org, heavy log search | **ELK** | Nothing beats ES for log search quality |
| Enterprise, compliance | **Splunk** | SIEM, compliance dashboards built-in |
| Already on K8s | **PLG** + OpenTelemetry | Native Kubernetes observability |

---

## ELK vs Loki — The Big Trade-off

| | Elasticsearch | Grafana Loki |
|---|---|---|
| **Indexing** | Full-text index (every word) | Index **only labels** (service, level) |
| **Storage cost** | ~$2-5 per GB/month | ~$0.023 per GB/month (S3) |
| **Query speed** | Sub-second for any search | Fast for label queries, slow for full-text |
| **Best query** | `message: "payment failed"` | `{service="order-svc"} |= "payment failed"` |
| **Philosophy** | Index everything → search anything | "Grep at scale" → cheaper |

> **Loki's insight**: 90% of log queries filter by service + time first.
> You don't need to index every word — just labels.

---

## Distributed Tracing — The Missing Piece

Traces answer: "Request X hit 8 services — where did it slow down?"

```
Client → API Gateway → Order Service → Payment Service → Bank API
                                     → Inventory Service → DB
                          └─ Notification Service → Email Provider

Trace ID: abc-123-def-456
├── API Gateway         (2ms)
├── Order Service       (15ms)
│   ├── Payment Service (200ms)  ← SLOW! Found the bottleneck!
│   │   └── Bank API    (195ms)
│   └── Inventory Svc   (5ms)
└── Notification Svc    (3ms, async)

Total: 220ms
```

### OpenTelemetry (OTel) — The Standard

```java
// Spring Boot with OpenTelemetry auto-instrumentation
// Just add the Java agent — it instruments automatically:
//   java -javaagent:opentelemetry-javaagent.jar -jar app.jar

// For custom spans:
@Service
public class PaymentService {
    
    private final Tracer tracer;

    public PaymentResult processPayment(PaymentRequest req) {
        Span span = tracer.spanBuilder("processPayment")
            .setAttribute("orderId", req.getOrderId())
            .setAttribute("amount", req.getAmount())
            .startSpan();
        try (Scope scope = span.makeCurrent()) {
            PaymentResult result = callBankApi(req);
            span.setAttribute("paymentStatus", result.getStatus());
            return result;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

---

## 🎤 Interview Tip

> *"For this design I chose ELK because the requirement emphasizes log search
> quality. But I'd acknowledge the alternative: Loki is 10× cheaper because
> it only indexes labels, not full text. If budget is a concern, I'd recommend
> Loki for logs + Prometheus for metrics + Jaeger for traces, all unified
> under Grafana dashboards."*

---

*Next: [10 — Interview Script →](./10-interview-script.md)*
