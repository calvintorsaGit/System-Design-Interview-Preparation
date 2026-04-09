# 🎤 Step 7: Interview Script

---

## Opening (2 min)
> *"I'll design an e-commerce checkout system like Tokopedia. The core challenge is
> orchestrating a distributed transaction across inventory, orders, and payments
> without losing data or overselling."*

## Requirements (3 min)
- Focus: checkout pipeline (cart → order → payment → fulfillment)
- 10M products, 500K peak concurrent users, flash sale support

## Estimation (3 min)
- Flash sale peak: 10K orders/sec
- Each order ~2KB → 20 MB/sec write throughput
- 1M orders/day normal → ~12 orders/sec average

## High-Level Design (10 min)
- Draw microservices: Product, Cart, Order, Payment, Inventory, Notification
- Database-per-service, Kafka for events
- API Gateway for routing + auth

## Deep Dive (20 min)
Pick 2-3 based on interviewer interest:
1. **SAGA pattern** — orchestration-based checkout flow
2. **Inventory concurrency** — optimistic locking + Redis for flash sales
3. **Idempotency** — preventing double charges
4. **Event-driven architecture** — Kafka event flows between services

## Trade-offs (5 min)
- SAGA vs 2PC, optimistic vs pessimistic locking
- Payment timeout → webhook + reconciliation

---

> **Key differentiator**: Show you understand **compensating transactions**.
> "If payment fails, here's exactly how I roll back inventory and cancel the order."
