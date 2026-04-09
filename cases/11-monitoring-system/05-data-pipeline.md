# 🔄 Step 5: Data Pipeline — Collection to Storage

---

## Log Collection Agents

| Agent | Pros | Cons | Best For |
|-------|------|------|----------|
| **Filebeat** | Lightweight (Go), built for ELK | Limited transformation | Simple log shipping |
| **Fluentd** | Rich plugins, CNCF project | Higher memory usage | Kubernetes environments |
| **Fluent Bit** | Ultra-lightweight | Fewer plugins than Fluentd | Edge/IoT |
| **OpenTelemetry Collector** | Unified (logs + metrics + traces) | Newer, evolving | Modern observability |

### Deployment Pattern (Kubernetes)

```
┌──────────────────────── Node ────────────────────────┐
│                                                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │ App Pod  │  │ App Pod  │  │ App Pod  │           │
│  │ (writes  │  │ (writes  │  │ (writes  │           │
│  │  to      │  │  to      │  │  to      │           │
│  │  stdout) │  │  stdout) │  │  stdout) │           │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘           │
│       │              │              │                 │
│       └──────────────┼──────────────┘                 │
│                      ▼                                │
│            ┌──────────────────┐                       │
│            │  Filebeat        │  ← DaemonSet          │
│            │  (one per node)  │    (reads /var/log)   │
│            └────────┬─────────┘                       │
│                     │                                 │
└─────────────────────┼─────────────────────────────────┘
                      │
                      ▼
                    Kafka
```

---

## Structured vs. Unstructured Logs

### ❌ Unstructured (hard to parse)
```
2026-04-09 12:30:45 ERROR OrderService - Failed to process order #123 for user john@example.com
```

### ✅ Structured JSON (recommended)
```json
{
  "@timestamp": "2026-04-09T12:30:45.000Z",
  "level": "ERROR",
  "service": "order-service",
  "message": "Failed to process order",
  "orderId": "123",
  "userId": "john@example.com",
  "errorType": "PaymentDeclined"
}
```

### Spring Boot: Structured Logging with Logback

```xml
<!-- logback-spring.xml -->
<configuration>
  <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
      <customFields>{"service":"order-service","env":"prod"}</customFields>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="JSON" />
  </root>
</configuration>
```

```java
// Application code
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.logstash.logback.argument.StructuredArguments.*;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void processOrder(Order order) {
        log.info("Processing order",
            kv("orderId", order.getId()),
            kv("userId", order.getUserId()),
            kv("amount", order.getTotal()));
        // Produces: {"message":"Processing order","orderId":"123","userId":"john",...}
    }
}
```

---

## Log Enrichment Pipeline

```
Raw Log → Parse → Enrich → Filter → Index

Parse:   Extract fields from unstructured text (grok patterns)
Enrich:  Add context (service name, region, geolocation from IP)
Filter:  Drop DEBUG logs, mask PII (emails, credit cards)
Index:   Send to Elasticsearch with proper mapping
```

### PII Masking Example

```ruby
# Logstash filter to mask emails
filter {
  mutate {
    gsub => [
      "message", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}", "***@***.***"
    ]
  }
}
```

---

## 🎤 Interview Tip

> *"Applications should emit structured JSON logs — this eliminates expensive
> regex parsing in Logstash. I'd deploy Filebeat as a DaemonSet in Kubernetes
> to ship logs to Kafka, then Logstash consumers enrich and index into
> Elasticsearch. PII masking happens in the pipeline, never stored raw."*

---

*Next: [06 — Storage & Indexing →](./06-storage-and-indexing.md)*
