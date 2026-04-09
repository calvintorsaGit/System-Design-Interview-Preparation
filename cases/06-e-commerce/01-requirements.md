# 📋 Step 1: Clarify Requirements

> E-commerce is broad — scope it down or you'll run out of time.

---

## Questions to Ask

| Question | Typical Answer | Why It Matters |
|----------|---------------|----------------|
| Which part of e-commerce? | Focus on **checkout flow** | Avoids designing everything |
| Number of products? | 10M SKUs | Catalog + search complexity |
| Peak concurrent users? | 500K during flash sales | Drives scaling strategy |
| Payment methods? | Credit card, e-wallet | Payment gateway integration |
| Do we need real-time inventory? | Yes — no overselling | Concurrency control on stock |
| Multi-seller / marketplace? | Yes (like Tokopedia) | Separates seller → buyer flows |

---

## ✅ Final Requirements

### Functional
1. **Product Catalog** — browse, search, filter products
2. **Shopping Cart** — add/remove items, persist across sessions
3. **Checkout & Order** — place order, calculate total + shipping
4. **Payment** — process payment via gateway (Stripe/Midtrans)
5. **Inventory** — decrement stock, prevent overselling
6. **Order Tracking** — order status updates (processing → shipped → delivered)
7. **Notifications** — email/push for order confirmation, shipping updates

### Non-Functional
1. **Consistency**: Inventory must be strongly consistent (no overselling)
2. **Availability**: 99.99% — downtime = lost revenue
3. **Latency**: Product page < 200ms, checkout < 2s
4. **Scale**: Handle 10K orders/sec during flash sales
5. **Idempotency**: Payment retries must not double-charge

---

## 🎤 Interview Tip

> *"E-commerce is a large domain. I'll focus on the **checkout and order pipeline**
> — how a user goes from cart to confirmed order — because that's where the hard
> distributed systems problems live: inventory, payments, and consistency."*

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
