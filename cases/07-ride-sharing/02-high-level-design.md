# 🏛️ Step 2: High-Level Design

---

## Architecture Diagram

```
┌──────────┐      ┌──────────┐
│  Rider   │      │  Driver  │
│   App    │      │   App    │
└────┬─────┘      └────┬─────┘
     │                  │
     └────────┬─────────┘
              ▼
     ┌────────────────┐
     │  API Gateway   │
     └───────┬────────┘
             │
  ┌──────────┼──────────────┬──────────────┐
  ▼          ▼              ▼              ▼
┌──────┐ ┌──────────┐ ┌──────────┐  ┌──────────┐
│ Trip │ │ Matching │ │ Location │  │ Payment  │
│ Svc  │ │ Service  │ │ Service  │  │ Service  │
└──┬───┘ └────┬─────┘ └────┬─────┘  └────┬─────┘
   │          │            │              │
   ▼          ▼            ▼              ▼
┌──────┐ ┌──────────┐ ┌──────────┐  ┌──────────┐
│ PG   │ │  Redis   │ │  Redis   │  │ Stripe   │
│(trips)│ │(geospatial)│(location) │  │          │
└──────┘ └──────────┘ └──────────┘  └──────────┘
```

---

## Service Responsibilities

| Service | Responsibility | Key Technology |
|---------|---------------|----------------|
| **Location Service** | Ingest driver GPS updates (333K writes/sec) | Redis GEO commands |
| **Matching Service** | Find nearby + best driver for rider | Geospatial query + scoring |
| **Trip Service** | Manage trip lifecycle (state machine) | PostgreSQL (ACID) |
| **Payment Service** | Calculate fare, charge rider | Stripe + idempotency |
| **Pricing Service** | Surge pricing based on supply/demand | In-memory calculation |
| **Notification Service** | Push notifications to rider/driver | FCM/APNs |

---

## Trip State Machine

```
REQUESTED → MATCHED → DRIVER_EN_ROUTE → ARRIVED → IN_PROGRESS → COMPLETED → PAID
     │          │                                                      │
     └─ CANCELLED (by rider)                                    DISPUTED
               │
     └─ REJECTED (by driver) → re-match
```

---

## 🎤 Interview Tip

> *"The two hardest parts are: (1) ingesting 333K location updates per second
> from drivers, and (2) efficiently finding the nearest available drivers
> for a rider within a geospatial radius. I'd use Redis GEO for both."*

---

*Next: [03 — Geospatial Indexing →](./03-geospatial-indexing.md)*
