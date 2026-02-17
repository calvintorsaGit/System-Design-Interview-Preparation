# 🏛️ Step 2: High-Level Design

> Where does the Rate Limiter live?

---

## Placement Options

### Option 1: Inside Application Code (Sidecar)
- **Props**: Easy to customize logic.
- **Cons**: Every service needs its own implementation.

### Option 2: API Gateway (Recommended ✅)
- **Props**: Centralized, protects all downstream services.
- **Cons**: Single point of failure (needs high availability).

---

## Architecture Diagram

```
      Client
         │
         ▼
 ┌───────────────┐
 │  API Gateway  │  ◄── Checks Redis for counts
 │ (Rate Limiter)│
 └───────┬───────┘
         │
    (Allowed?)
   ┌─────┴─────┐
   ▼           ▼
  Yes          No
   │        Return 429
   │
   ▼
 Service A / B / C
```

---

*Next: [03 - Algorithms →](./03-algorithms.md)*
