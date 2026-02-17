# 🎯 System Design Interview Preparation — Learn by Real Cases

## 🗺️ Study Plan

Work through these cases **in order** — each one builds on concepts from the previous.

| # | Case Study | Key Concepts You'll Learn | Difficulty |
|---|-----------|--------------------------|------------|
| 1 | [URL Shortener (like Bit.ly)](./cases/01-url-shortener.md) | Hashing, Base62, SQL vs NoSQL, caching, read-heavy systems, back-of-envelope estimation | ⭐⭐ |
| 2 | [Rate Limiter](./cases/02-rate-limiter.md) | Token bucket, sliding window, Redis, distributed coordination, middleware/filters | ⭐⭐ |
| 3 | [Notification System](./cases/03-notification-system.md) | Message queues (Kafka/RabbitMQ), pub/sub, retry & dead-letter, priority queues | ⭐⭐⭐ |
| 4 | [News Feed / Timeline (like Twitter)](./cases/04-news-feed.md) | Fan-out on write vs read, caching layers, social graph, pagination | ⭐⭐⭐ |
| 5 | [Chat System (like WhatsApp)](./cases/05-chat-system.md) | WebSocket, connection management, message ordering, presence, delivery receipts | ⭐⭐⭐ |
| 6 | [E-Commerce System (like Tokopedia)](./cases/06-e-commerce.md) | Microservices, SAGA pattern, inventory management, payment, eventual consistency | ⭐⭐⭐⭐ |
| 7 | [Ride-Sharing (like Gojek/Grab)](./cases/07-ride-sharing.md) | Geospatial indexing, real-time matching, location tracking, ETA estimation | ⭐⭐⭐⭐ |
| 8 | [Video Streaming (like YouTube)](./cases/08-video-streaming.md) | Blob storage, CDN, transcoding pipeline, adaptive bitrate, search indexing | ⭐⭐⭐⭐ |
| 9 | [Distributed Cache (like Redis)](./cases/09-distributed-cache.md) | Consistent hashing, eviction policies, replication, cache stampede | ⭐⭐⭐⭐ |
| 10 | [Search Autocomplete (like Google)](./cases/10-search-autocomplete.md) | Trie, Elasticsearch, ranking algorithms, real-time updates | ⭐⭐⭐⭐ |

---


## 🧠 Quick Reference: Concept Cheat Sheet

As you work through the cases, you'll keep encountering these concepts.
Use this as a lookup table:

| Concept | What It Solves | Cases That Use It |
|---------|---------------|-------------------|
| **Consistent Hashing** | Distribute data across nodes evenly | #9, #6 |
| **CAP Theorem** | Choose between consistency & availability | #5, #6, #9 |
| **Message Queues** | Decouple producers & consumers | #3, #4, #6, #8 |
| **Database Sharding** | Scale writes beyond one DB | #1, #4, #5, #7 |
| **Caching (Redis)** | Reduce latency for frequent reads | #1, #2, #4, #10 |
| **Load Balancing** | Distribute traffic across servers | All |
| **CDN** | Serve static content from edge | #8 |
| **WebSocket** | Real-time bidirectional communication | #5, #7 |
| **Fan-out** | Push vs pull data distribution | #3, #4 |
| **SAGA Pattern** | Distributed transactions | #6, #7 |
| **Event Sourcing** | Immutable event log as source of truth | #6 |
| **Geospatial Index** | Efficiently query by location | #7 |

---
*Created: 2026-02-17 • Good luck with your interviews! 🚀*