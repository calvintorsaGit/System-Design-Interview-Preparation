# 🏛️ Step 3: High-Level Design

> Draw the big picture first. Details come later.

---

## Architecture Diagram

```
                         ┌──────────────────────┐
                         │     Load Balancer     │
                         │    (Nginx / AWS ALB)  │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
              ┌──────────┐   ┌──────────┐   ┌──────────┐
              │  App #1  │   │  App #2  │   │  App #3  │
              │ (Spring  │   │ (Spring  │   │ (Spring  │
              │  Boot)   │   │  Boot)   │   │  Boot)   │
              └────┬─────┘   └────┬─────┘   └────┬─────┘
                   │              │              │
                   └──────────────┼──────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼                            ▼
              ┌──────────┐                ┌──────────┐
              │  Cache   │                │ Database │
              │ (Redis)  │                │(Postgres │
              │          │                │  / MySQL)│
              └──────────┘                └──────────┘
```

---

## Component Responsibilities

### 1. Load Balancer
- Distributes traffic across Spring Boot instances
- Health checks — removes unhealthy servers
- SSL termination

### 2. Spring Boot Application (Stateless)
- **POST /api/v1/shorten** → Generate short URL
- **GET /{shortCode}** → Redirect to original URL
- No server-local state → easy to scale horizontally

### 3. Cache (Redis)
- **Purpose**: Speed up the read path (redirect lookups)
- **Strategy**: Cache-aside (check cache first, fallback to DB)
- **Key**: `shortCode` → **Value**: `longUrl`
- **TTL**: Match URL expiration, or 24 hours for hot URLs

### 4. Database (PostgreSQL or MySQL)
- Persistent storage for all URL mappings
- Source of truth

---

## Request Flows

### Flow 1: Create Short URL (Write Path)

```
Client                  Spring Boot              Database
  │                         │                       │
  │  POST /api/v1/shorten   │                       │
  │  { "longUrl": "..." }   │                       │
  │ ───────────────────────► │                       │
  │                         │  Generate short code   │
  │                         │  (Base62 / hash)       │
  │                         │                       │
  │                         │  INSERT INTO urls      │
  │                         │ ─────────────────────► │
  │                         │                       │
  │                         │       OK              │
  │                         │ ◄───────────────────── │
  │                         │                       │
  │  { "shortUrl":          │                       │
  │    "short.ly/abc123" }  │                       │
  │ ◄─────────────────────  │                       │
```

### Flow 2: Redirect (Read Path — Cache Hit)

```
Client             Spring Boot           Redis           Database
  │                     │                   │                │
  │ GET /abc123         │                   │                │
  │ ──────────────────► │                   │                │
  │                     │  GET abc123       │                │
  │                     │ ────────────────► │                │
  │                     │                   │                │
  │                     │  "https://..."    │                │
  │                     │ ◄──────────────── │                │
  │                     │                   │                │
  │ 302 Redirect        │                   │                │
  │ Location: https://..│                   │                │
  │ ◄────────────────── │                   │                │
```

### Flow 3: Redirect (Read Path — Cache Miss)

```
Client             Spring Boot           Redis           Database
  │                     │                   │                │
  │ GET /abc123         │                   │                │
  │ ──────────────────► │                   │                │
  │                     │  GET abc123       │                │
  │                     │ ────────────────► │                │
  │                     │                   │                │
  │                     │  null (miss)      │                │
  │                     │ ◄──────────────── │                │
  │                     │                   │                │
  │                     │  SELECT * FROM    │                │
  │                     │  urls WHERE ...   │                │
  │                     │ ──────────────────────────────► │
  │                     │                   │                │
  │                     │  "https://..."    │                │
  │                     │ ◄────────────────────────────── │
  │                     │                   │                │
  │                     │  SET abc123       │                │
  │                     │ ────────────────► │   (backfill)   │
  │                     │                   │                │
  │ 302 Redirect        │                   │                │
  │ ◄────────────────── │                   │                │
```

---

## 301 vs 302 Redirect — Important Trade-off!

| | 301 (Moved Permanently) | 302 (Found / Temporary) |
|---|---|---|
| **Browser behavior** | Browser caches redirect, won't call server again | Browser always calls server |
| **Good for** | Reducing server load | Tracking click analytics |
| **Bad for** | Can't track clicks accurately | More server load |

### 💡 Recommendation
> Use **302** if you want analytics (most URL shorteners do).  
> Use **301** if pure redirect performance is the priority.

---

## 🎤 Interview Tip

> Draw this diagram on the whiteboard. Point to each component and say:
> *"The system has three layers: a load balancer for traffic distribution,
> stateless Spring Boot servers for business logic, and a caching layer
> in front of the database for fast reads."*
>
> Then walk through both the write and read flows.

---

*Next: [04 - API Design →](./04-api-design.md)*
