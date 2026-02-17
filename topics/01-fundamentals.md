# 1. System Design Fundamentals & Building Blocks

> Master these first — they are the vocabulary of every system design conversation.

---

## 1.1 Client-Server Model

```
┌────────┐   HTTP / gRPC   ┌────────┐
│ Client │ ──────────────► │ Server │
└────────┘                  └────────┘
```

- **Client**: browser, mobile app, another service.
- **Server**: your Java backend (Spring Boot, Quarkus, etc.).
- Communication is typically **request/response** (synchronous) or **event-driven** (asynchronous).

---

## 1.2 Network Protocols You Must Know

| Protocol | Layer | Use Case | Java Support |
|----------|-------|----------|--------------|
| **HTTP/1.1** | Application | REST APIs | `HttpURLConnection`, Spring MVC |
| **HTTP/2** | Application | Multiplexed streams, gRPC | Netty, Spring Boot 2+ |
| **WebSocket** | Application | Real-time bi-directional | Spring WebSocket, `javax.websocket` |
| **TCP** | Transport | Reliable ordered delivery | `java.net.Socket` |
| **UDP** | Transport | Low-latency (video, gaming) | `java.net.DatagramSocket` |

### 💡 Interview Tip
> When you say "the client calls the server", the interviewer expects you to specify
> **which protocol** (REST over HTTP? gRPC? WebSocket?) and **why**.

---

## 1.3 Latency Numbers Every Developer Should Know

| Operation | Latency |
|-----------|---------|
| L1 cache reference | ~1 ns |
| L2 cache reference | ~4 ns |
| Main memory reference | ~100 ns |
| SSD random read | ~16 μs |
| HDD seek | ~4 ms |
| Same datacenter round trip | ~0.5 ms |
| Cross-continent round trip | ~150 ms |

### Why This Matters
Back-of-envelope estimation is a key interview skill. Example:
- "If each request does 1 DB read (1 ms) and 1 cache read (1 μs), we can handle ~1000 QPS per server."

---

## 1.4 Back-of-Envelope Estimation

### Common Approximations
```
1 Million  = 10^6
1 Billion  = 10^9

1 day      = 86,400 seconds  ≈ 10^5 seconds
1 month    = 2.6 million seconds ≈ 2.5 × 10^6 seconds
1 year     = 31.5 million seconds ≈ 3 × 10^7 seconds

1 KB       = 10^3 bytes
1 MB       = 10^6 bytes
1 GB       = 10^9 bytes
1 TB       = 10^12 bytes
```

### Example: Estimate Storage for a URL Shortener
- 100M new URLs/month
- Each URL entry ≈ 500 bytes (original URL + short code + metadata)
- Monthly storage = 100M × 500B = 50 GB/month
- 5-year storage = 50 GB × 60 = 3 TB

---

## 1.5 Horizontal vs. Vertical Scaling

| | Vertical Scaling (Scale Up) | Horizontal Scaling (Scale Out) |
|---|---|---|
| **What** | Bigger machine (more CPU, RAM) | More machines |
| **Pros** | Simple, no code changes | Virtually unlimited, fault tolerant |
| **Cons** | Hardware limits, single point of failure | Complexity (load balancing, data consistency) |
| **When** | Small-medium load, databases | High traffic, stateless services |

### Java Context
```java
// Stateless service — easy to scale horizontally
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);  // No server-local state
    }
}

// Session-sticky service — harder to scale
// ❌ Avoid storing state in server memory
@RestController
public class CartController {
    private Map<String, Cart> carts = new HashMap<>();  // ❌ Bad!
}
```

---

## 1.6 Single Points of Failure (SPOF)

Every system design answer should address: *What happens if component X dies?*

| Component | SPOF Mitigation |
|-----------|----------------|
| Web server | Multiple instances behind a load balancer |
| Database | Primary-replica replication, failover |
| Cache | Redis Sentinel / Cluster |
| Load balancer | Active-passive pair, DNS failover |
| Entire datacenter | Multi-region deployment |

---

## 1.7 CAP Theorem (Simplified)

In a **distributed** system, you can guarantee only **2 out of 3**:

| Letter | Meaning | Example |
|--------|---------|---------|
| **C** - Consistency | Every read gets the latest write | RDBMS (PostgreSQL) |
| **A** - Availability | Every request gets a (non-error) response | Cassandra, DynamoDB |
| **P** - Partition Tolerance | System works despite network splits | (Always needed in distributed systems) |

### Practical Takeaway
Since network partitions *will* happen, you're really choosing between:
- **CP** (consistency + partition tolerance): Banking systems, inventory counts → PostgreSQL, MongoDB
- **AP** (availability + partition tolerance): Social feeds, recommendations → Cassandra, DynamoDB

### 💡 Interview Tip
> Don't just say "CAP Theorem". Instead, say: *"For this use case, I'd choose CP because
> we can't afford stale inventory counts — customers might over-purchase."*

---

## 1.8 Consistency Models

| Model | Description | Speed | Use Case |
|-------|-------------|-------|----------|
| **Strong** | Read always returns latest write | Slowest | Financial transactions |
| **Eventual** | Reads may be stale, but converge | Fastest | Social media feeds |
| **Causal** | Preserves cause-effect ordering | Medium | Chat messages |
| **Read-your-writes** | You see your own writes immediately | Medium | User profile updates |

---

## 1.9 Key Architectural Patterns

### Monolith
```
┌─────────────────────────────────┐
│           Spring Boot App       │
│  ┌────┐ ┌────┐ ┌─────┐ ┌────┐ │
│  │Auth│ │User│ │Order│ │Pay │ │
│  └────┘ └────┘ └─────┘ └────┘ │
│         Single Deploy Unit      │
└─────────────────────────────────┘
```

### Microservices
```
┌────┐  ┌────┐  ┌─────┐  ┌────┐
│Auth│  │User│  │Order│  │Pay │
└──┬─┘  └──┬─┘  └──┬──┘  └──┬─┘
   │       │       │        │
   └───────┴───────┴────────┘
          Message Broker
```

### Event-Driven
```
Producer → Event Bus (Kafka) → Consumer A
                              → Consumer B
                              → Consumer C
```

---

## 1.10 DNS, CDN & Proxies

### DNS (Domain Name System)
- Translates `api.myapp.com` → `34.102.136.180`
- Can be used for load balancing (round-robin DNS)

### CDN (Content Delivery Network)
- Caches static content (images, JS, CSS) at edge locations
- Reduces latency for geographically distributed users
- Examples: CloudFront, Cloudflare, Akamai

### Reverse Proxy
```
Client → Reverse Proxy (Nginx) → Backend Server(s)
```
- Hides backend topology
- SSL termination
- Compression
- Rate limiting

---

## ✅ Practice Questions

1. **Estimate** the storage needed for a service that stores 10M chat messages per day, each ~1 KB, for 5 years.
2. **Explain** the difference between horizontal and vertical scaling. When would you choose each for a Java application?
3. **A service has a single database as its SPOF**. How would you redesign it for high availability?
4. You're designing a social media feed. Should you choose **CP or AP**? Justify your answer.
5. Your Spring Boot REST API needs to handle 50,000 QPS. Walk through your scaling strategy.

---

*Next: [02 - API Design →](./02-api-design.md)*
