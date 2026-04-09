# Case 6: E-Commerce System (like Tokopedia)

> The ultimate microservices design question — tests payment flows, inventory, SAGA pattern, and eventual consistency.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | Product catalog, cart, checkout, payment |
| [02-high-level-design.md](./02-high-level-design.md) | Microservices architecture with service boundaries |
| [03-order-flow.md](./03-order-flow.md) | Checkout → Payment → Fulfillment pipeline |
| [04-saga-pattern.md](./04-saga-pattern.md) | Distributed transactions with compensating actions |
| [05-inventory.md](./05-inventory.md) | Stock management, overselling prevention |
| [06-tradeoffs-and-failures.md](./06-tradeoffs-and-failures.md) | Payment failures, idempotency, double-charge prevention |
| [07-interview-script.md](./07-interview-script.md) | How to present this |

---

## 🎯 Key Concepts

- ✅ **Microservices Architecture** — service decomposition
- ✅ **SAGA Pattern** — distributed transactions without 2PC
- ✅ **Inventory Management** — preventing overselling
- ✅ **Idempotency** — safe retries for payment APIs
- ✅ **Eventual Consistency** — between order, payment, and inventory
- ✅ **Event Sourcing** — immutable event log for order state

---

## ⏱️ Estimated Study Time: 3-4 hours

Start with `01-requirements.md` and work through in order.
