# 🏛️ Step 2: High-Level Design

> E-commerce is the textbook microservices example. Each domain = one service.

---

## Architecture Diagram

```
┌──────────┐     ┌──────────┐
│   Web    │     │  Mobile  │
│  Client  │     │  Client  │
└────┬─────┘     └────┬─────┘
     └────────┬───────┘
              ▼
     ┌────────────────┐
     │  API Gateway   │
     │  (Spring Cloud │
     │   Gateway)     │
     └───────┬────────┘
             │
  ┌──────────┼───────────┬──────────────┬───────────────┐
  ▼          ▼           ▼              ▼               ▼
┌──────┐ ┌──────┐  ┌─────────┐  ┌──────────┐   ┌──────────┐
│Product│ │ Cart │  │  Order  │  │ Payment  │   │Inventory │
│Service│ │Serv. │  │ Service │  │ Service  │   │ Service  │
└──┬───┘ └──┬───┘  └────┬────┘  └────┬─────┘   └────┬─────┘
   │        │           │            │               │
   ▼        ▼           ▼            ▼               ▼
┌──────┐ ┌──────┐  ┌─────────┐  ┌──────────┐   ┌──────────┐
│Mongo │ │Redis │  │Postgres │  │  Stripe  │   │Postgres  │
│(cat.)│ │(cart)│  │(orders) │  │ Midtrans │   │(stock)   │
└──────┘ └──────┘  └─────────┘  └──────────┘   └──────────┘
                        │
                        ▼
                  ┌──────────┐
                  │  Kafka   │     ──► Notification Service
                  │ (events) │     ──► Analytics Service
                  └──────────┘     ──► Shipping Service
```

---

## Service Boundaries

| Service | Responsibility | Database | Why Separate DB? |
|---------|---------------|----------|-------------------|
| **Product** | CRUD products, search, categories | MongoDB (flexible schema) | Catalog changes independently |
| **Cart** | Add/remove items, session-based | Redis (fast, ephemeral) | Cart is temporary data |
| **Order** | Create orders, track status | PostgreSQL (ACID) | Orders need strong consistency |
| **Payment** | Charge user, handle refunds | External (Stripe) | Isolate payment logic |
| **Inventory** | Stock counts, reservations | PostgreSQL (row-level locks) | Must be strongly consistent |
| **Notification** | Email, push, SMS | — | Async, decoupled |

---

## The Golden Rule: Database Per Service

```
❌ Shared Database (anti-pattern):
   Order Service ──┐
                   ├──► Single PostgreSQL ← hard to change schema,
   Inventory Svc ──┘                        tight coupling

✅ Database Per Service:
   Order Service ──► Orders DB (PostgreSQL)
   Inventory Svc ──► Inventory DB (PostgreSQL)
                     (communicate via events)
```

---

## 🎤 Interview Tip

> *"I'd decompose this into microservices by domain: Product, Cart, Order, Payment,
> and Inventory. Each has its own database — the Order service owns order data,
> the Inventory service owns stock counts. They communicate asynchronously via
> Kafka events. This gives independent deployability and fault isolation."*

---

*Next: [03 — Order Flow →](./03-order-flow.md)*
