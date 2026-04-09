# System Design Interview Preparation

> Master system design by building real systems. Each case study teaches you the
> concepts through practical, interview-ready examples using **Spring Boot / Java**.

---

## Study Plan

Work through these cases **in order** — each one builds on concepts from the previous.

| # | Case Study | Key Concepts You'll Learn | Difficulty |
|---|-----------|--------------------------|------------|
| 1 | [URL Shortener (like Bit.ly)](./cases/01-url-shortener/) | Hashing, Base62, SQL vs NoSQL, caching, read-heavy systems, back-of-envelope estimation | Medium |
| 2 | [Rate Limiter](./cases/02-rate-limiter/) | Token bucket, sliding window, Redis, distributed coordination, middleware/filters | Medium |
| 3 | [Notification System](./cases/03-notification-system/) | Message queues (Kafka/RabbitMQ), pub/sub, retry & dead-letter, priority queues | Hard |
| 4 | [News Feed / Timeline (like Twitter)](./cases/04-news-feed/) | Fan-out on write vs read, caching layers, social graph, pagination | Hard |
| 5 | [Chat System (like WhatsApp)](./cases/05-chat-system/) | WebSocket, connection management, message ordering, presence, delivery receipts | Hard |
| 6 | [E-Commerce System (like Tokopedia)](./cases/06-e-commerce/) | Microservices, SAGA pattern, inventory management, payment, eventual consistency | Expert |
| 7 | [Ride-Sharing (like Gojek/Grab)](./cases/07-ride-sharing/) | Geospatial indexing, real-time matching, location tracking, ETA estimation | Expert |
| 8 | [Video Streaming (like YouTube)](./cases/08-video-streaming/) | Blob storage, CDN, transcoding pipeline, adaptive bitrate, search indexing | Expert |
| 9 | [Distributed Cache (like Redis)](./cases/09-distributed-cache/) | Consistent hashing, eviction policies, replication, cache stampede | Expert |
| 10 | [Search Autocomplete (like Google)](./cases/10-search-autocomplete/) | Trie, Elasticsearch, ranking algorithms, real-time updates | Expert |
| 11 | [Monitoring System (like ELK/Datadog)](./cases/11-monitoring-system/) | ELK stack, log pipelines, alerting, hot-warm-cold storage, distributed tracing | Expert |

---

## Every Case Study Follows This Structure

```
1. REQUIREMENTS     → What are we building? Functional & non-functional requirements
2. ESTIMATION       → QPS, storage, bandwidth (back-of-envelope math)
3. HIGH-LEVEL DESIGN → Architecture diagram with all major components
4. DEEP DIVE        → Database schema, API design, key algorithms
5. SPRING BOOT SKETCH → Simplified code showing the core idea  
6. TRADE-OFFS       → What did we choose and why?
7. FAILURE SCENARIOS → What breaks and how do we handle it?
8. INTERVIEW TIPS   → How to present this in an actual interview
```

---

## Quick Reference: Concept Cheat Sheet

As you work through the cases, you'll keep encountering these concepts.
Use this as a lookup table:

| Concept | What It Solves | Cases That Use It |
|---------|---------------|-------------------|
| **Consistent Hashing** | Distribute data across nodes evenly | #9, #6 |
| **CAP Theorem** | Choose between consistency & availability | #5, #6, #9 |
| **Message Queues** | Decouple producers & consumers | #3, #4, #6, #8, #11 |
| **Database Sharding** | Scale writes beyond one DB | #1, #4, #5, #7 |
| **Caching (Redis)** | Reduce latency for frequent reads | #1, #2, #4, #5, #10 |
| **Load Balancing** | Distribute traffic across servers | All |
| **CDN** | Serve static content from edge | #8 |
| **WebSocket** | Real-time bidirectional communication | #5, #7 |
| **Fan-out** | Push vs pull data distribution | #3, #4, #5 |
| **SAGA Pattern** | Distributed transactions | #6, #7 |
| **Event Sourcing** | Immutable event log as source of truth | #6 |
| **Geospatial Index** | Efficiently query by location | #7 |
| **Inverted Index** | Full-text search on large datasets | #8, #10, #11 |
| **Trie (Prefix Tree)** | Fast prefix matching | #10 |
| **ELK / Observability** | Centralized logging, metrics, tracing | #11 |
| **Idempotency** | Safe retries for critical operations | #6 |

---

## Suggested Timeline

| Week | Focus |
|------|-------|
| **Week 1** | Cases 1-2 (warm up with simpler systems) |
| **Week 2** | Cases 3-5 (messaging & real-time) |
| **Week 3** | Cases 6-7 (complex business logic) |
| **Week 4** | Cases 8-10 (infrastructure & search) |
| **Week 5** | Case 11 (monitoring & observability) |
| **Week 6+** | Mock interviews — practice explaining out loud |

---
*Created: 2026-02-17 | Updated: 2026-04-09*