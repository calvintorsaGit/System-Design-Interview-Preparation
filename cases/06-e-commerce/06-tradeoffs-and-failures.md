# ⚠️ Step 6: Trade-offs & Failure Scenarios

---

## Key Trade-offs

| Decision | Option A | Option B | Our Choice |
|----------|----------|----------|------------|
| Architecture | Monolith | Microservices | **Microservices** — independent scaling per domain |
| Distributed Tx | 2-Phase Commit | SAGA | **SAGA** — scales better, no distributed locks |
| Cart Storage | Database | Redis | **Redis** — ephemeral data, fast access |
| Inventory Lock | Pessimistic | Optimistic | **Optimistic** (normal), **Redis DECR** (flash sales) |
| Inter-service Comm | Sync (REST) | Async (Kafka) | **Kafka** for order events, **REST** for reads |

---

## Failure Scenarios

| What Breaks | Impact | Mitigation |
|-------------|--------|------------|
| Payment gateway timeout | User charged but we don't know | Idempotency key + webhook callback from Stripe |
| Inventory service down | Can't check stock | Circuit breaker; show "try again" to user |
| Order service down | Can't create orders | Kafka backlog; process when recovered |
| Redis (cart) crashes | Users lose cart | Async persist to DB every 5 min as backup |
| Kafka down | Events stuck | Kafka replication (3 brokers); fallback to sync |
| Double click on "Pay" | Double charge risk | Idempotency key on every payment request |

---

## 🎤 Interview Tip

> *"The most critical failure scenario is the payment gateway timing out —
> the user might be charged but we didn't receive confirmation. I'd handle
> this with idempotency keys and a webhook listener from Stripe that
> updates the order status asynchronously, plus a reconciliation job
> that checks for dangling payments every hour."*

---

*Next: [07 — Interview Script →](./07-interview-script.md)*
