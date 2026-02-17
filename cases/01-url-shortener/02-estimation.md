# 📊 Step 2: Back-of-Envelope Estimation

> This step impresses interviewers. It shows you think about scale before designing.

---

## Traffic Estimation

### Writes (URL creation)
```
100 Million new URLs / month
= 100M / (30 days × 24 hrs × 3600 sec)
= 100M / 2.6M seconds
≈ ~40 URLs created per second (write QPS)
```

### Reads (URL redirects)
```
Read:Write ratio = 10:1
Read QPS = 40 × 10 = ~400 redirects per second

Peak traffic = 2× average (rule of thumb)
Peak read QPS = ~800 redirects per second
```

> 💡 400 QPS is very manageable for a single Spring Boot server.
> But at scale (billions of records), the challenge is **storage and lookup speed**, not QPS.

---

## Storage Estimation

### Per URL record
| Field | Size |
|-------|------|
| Short URL key (7 chars) | 7 bytes |
| Long URL (avg) | 200 bytes |
| User ID | 8 bytes |
| Created timestamp | 8 bytes |
| Expiration timestamp | 8 bytes |
| Total | ~250 bytes |

### 5-Year Storage
```
URLs per month     = 100M
URLs over 5 years  = 100M × 12 × 5 = 6 Billion URLs
Storage            = 6B × 250 bytes = 1.5 TB
```

> 💡 1.5 TB is large but fits on a single modern server.
> However, we'll want to **shard** for performance and availability.

---

## Bandwidth Estimation

### Incoming (writes)
```
40 requests/sec × 250 bytes = 10 KB/s  (negligible)
```

### Outgoing (reads)
```
400 requests/sec × 250 bytes = 100 KB/s (negligible)
```

> Bandwidth is not a bottleneck for this system.

---

## Cache Estimation

If we follow the **80/20 rule** (80% of traffic goes to 20% of URLs):

```
Requests per day    = 400 × 86400 = ~35 Million reads/day
Cache 20% of daily  = 35M × 0.2 = 7 Million URLs in cache
Memory needed       = 7M × 250 bytes = ~1.75 GB
```

> 💡 **1.75 GB** fits easily in a single Redis instance.

---

## Summary Table

| Metric | Value |
|--------|-------|
| Write QPS | ~40/s |
| Read QPS | ~400/s (peak: ~800/s) |
| New URLs per month | 100M |
| Total URLs (5 years) | 6 Billion |
| Storage (5 years) | ~1.5 TB |
| Cache memory | ~1.75 GB |
| Bandwidth | < 1 MB/s |

---

## 🎤 Interview Tip

> Walk through the math out loud on the whiteboard. Interviewers don't care if your
> numbers are slightly off — they care that you **think about scale systematically**.
>
> A good phrase: *"Let me do a quick estimation to understand the scale we're 
> dealing with before I dive into the design."*

---

*Next: [03 - High-Level Design →](./03-high-level-design.md)*
