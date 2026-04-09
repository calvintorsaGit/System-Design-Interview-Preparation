# 🔍 Step 4: ELK Stack Deep Dive

> **E**lasticsearch + **L**ogstash + **K**ibana — the most popular open-source logging stack.

---

## Elasticsearch — The Search & Storage Engine

### What Is It?
A distributed search engine built on Apache Lucene. Stores data as **JSON documents** indexed for full-text search.

### How It Stores Logs

```json
{
  "@timestamp": "2026-04-09T12:30:45.123Z",
  "level": "ERROR",
  "service": "order-service",
  "instance": "order-service-pod-3",
  "message": "Failed to process payment for order #12345",
  "trace_id": "abc-123-def-456",
  "stack_trace": "java.lang.NullPointerException at com.example...",
  "environment": "production",
  "region": "ap-southeast-1"
}
```

### Inverted Index (How Search Works)

```
Traditional DB:    document → words
Inverted Index:    word → [documents that contain it]

Index:
  "payment"     → [doc_1, doc_5, doc_99]
  "failed"      → [doc_1, doc_3, doc_5]
  "order-service" → [doc_1, doc_2, doc_5, doc_7]

Query: "failed AND payment" → intersection → [doc_1, doc_5]
```

This is why Elasticsearch can search billions of logs in seconds.

### Elasticsearch Cluster Architecture

```
┌──────────────────────────────────────────────────┐
│              Elasticsearch Cluster                │
│                                                   │
│  ┌────────┐  ┌────────┐  ┌────────┐             │
│  │Master  │  │Master  │  │Master  │  (3 master   │
│  │Node #1 │  │Node #2 │  │Node #3 │   nodes)     │
│  └────────┘  └────────┘  └────────┘              │
│                                                   │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐ │
│  │ Data   │  │ Data   │  │ Data   │  │ Data   │ │  (Data nodes
│  │Node #1 │  │Node #2 │  │Node #3 │  │Node #4 │ │   store shards)
│  │(Hot)   │  │(Hot)   │  │(Warm)  │  │(Warm)  │ │
│  └────────┘  └────────┘  └────────┘  └────────┘ │
│                                                   │
│  ┌────────┐  ┌────────┐                          │
│  │Ingest  │  │Ingest  │  (Ingest nodes =         │
│  │Node #1 │  │Node #2 │   Logstash alternative)  │
│  └────────┘  └────────┘                          │
└──────────────────────────────────────────────────┘
```

### Index Strategy: Index Per Day

```
logs-2026.04.07    ← rollover daily
logs-2026.04.08
logs-2026.04.09    ← today (hot)

Each index has:
  - 5 primary shards
  - 1 replica shard each
  - Total: 10 shards per index
```

**Why daily indices?** Easy to delete old data — just drop the index.

---

## Logstash — The Processing Pipeline

Logstash transforms raw logs before indexing:

```
INPUT             →        FILTER          →        OUTPUT
(where logs       →    (parse, enrich,     →    (where to send
 come from)       →     transform)         →     processed logs)
```

### Logstash Pipeline Example

```ruby
input {
  kafka {
    bootstrap_servers => "kafka:9092"
    topics => ["app-logs"]
    codec => json
  }
}

filter {
  # Parse Java log format
  grok {
    match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{GREEDYDATA:msg}" }
  }

  # Add geolocation from IP
  geoip {
    source => "client_ip"
  }

  # Drop debug logs in production (reduce volume by ~60%)
  if [level] == "DEBUG" {
    drop { }
  }

  # Add metadata
  mutate {
    add_field => { "environment" => "production" }
  }
}

output {
  elasticsearch {
    hosts => ["es-node-1:9200", "es-node-2:9200"]
    index => "logs-%{+YYYY.MM.dd}"
  }
}
```

---

## Kibana — Visualization & Search

Kibana provides:

| Feature | Description |
|---------|-------------|
| **Discover** | Search and filter through raw logs |
| **Dashboards** | Build visualizations (line charts, pie charts, heat maps) |
| **Alerts** | Rule-based alerting on log patterns |
| **Lens** | Drag-and-drop dashboard builder |
| **Dev Tools** | Direct Elasticsearch query console |

### Common Queries in Kibana

```
# Find all errors in order-service in the last hour
level: "ERROR" AND service: "order-service"

# Find payment failures
message: "payment failed" AND level: "ERROR"

# Trace a specific request
trace_id: "abc-123-def-456"
```

---

## 🎤 Interview Tip

> *"Elasticsearch stores logs as JSON documents with an inverted index for
> sub-second full-text search. Logstash acts as the ETL pipeline — parsing,
> enriching, and filtering logs before indexing. Kibana is the visualization
> layer for search, dashboards, and alerting."*

---

*Next: [05 — Data Pipeline →](./05-data-pipeline.md)*
