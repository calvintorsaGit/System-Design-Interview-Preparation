# 🔄 Step 4: SAGA Pattern

> How do you handle distributed transactions when you can't use a single database transaction?

---

## The Problem

In a monolith:
```java
@Transactional  // One DB, one transaction — easy!
public void checkout(Order order) {
    inventoryRepo.reserve(order.getItems());
    orderRepo.save(order);
    paymentService.charge(order.getTotal());
}
// If anything fails → entire transaction rolls back
```

In microservices: **each service has its own database**. There is no single `@Transactional`.

---

## SAGA = A Sequence of Local Transactions + Compensations

```
Step 1: Reserve Inventory  ←── Compensation: Release Inventory
Step 2: Create Order       ←── Compensation: Cancel Order
Step 3: Process Payment    ←── Compensation: Refund Payment
Step 4: Confirm Order      ←── (no compensation needed, final step)
```

If Step 3 (Payment) fails:
```
✅ Step 1: Reserve Inventory
✅ Step 2: Create Order
❌ Step 3: Payment Failed!
🔄 Compensate Step 2: Cancel Order (status → CANCELLED)
🔄 Compensate Step 1: Release Inventory (reserved → available)
```

---

## Two SAGA Implementations

### 1. Choreography (Event-Driven) ✅ Simpler

Each service listens for events and reacts:

```
Inventory Service                Order Service               Payment Service
      │                               │                            │
      │  ◄── OrderCreated ──          │                            │
      │  Reserve stock                │                            │
      │  Emit: InventoryReserved ──►  │                            │
      │                               │  ──► PaymentRequested ──►  │
      │                               │                            │  Charge user
      │                               │  ◄── PaymentSucceeded ──  │
      │                               │  Update: CONFIRMED         │
```

| Pros | Cons |
|------|------|
| No central coordinator | Hard to track: "where is my order?" |
| Loosely coupled | Complex event chains |
| Easy to add new steps | Debugging distributed flows is hard |

### 2. Orchestration (Central Coordinator) ✅ Recommended for Complex Flows

An **Order Saga Orchestrator** drives the sequence:

```java
@Service
public class OrderSagaOrchestrator {

    public void execute(Order order) {
        try {
            // Step 1
            inventoryClient.reserve(order.getItems());

            // Step 2
            order.setStatus(OrderStatus.PENDING);
            orderRepo.save(order);

            // Step 3
            paymentClient.charge(order.getPayment());

            // Step 4: All good!
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepo.save(order);
            notificationClient.sendConfirmation(order);

        } catch (PaymentFailedException e) {
            // Compensate
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
            inventoryClient.release(order.getItems());
            notificationClient.sendPaymentFailed(order);

        } catch (InventoryInsufficientException e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
            notificationClient.sendOutOfStock(order);
        }
    }
}
```

| Pros | Cons |
|------|------|
| Clear workflow — easy to understand | Single point of coordination |
| Easy to add/reorder steps | Orchestrator can become complex |
| Better for complex business logic | Tighter coupling to orchestrator |

---

## 💡 When to Use Which?

| Scenario | Recommendation |
|----------|---------------|
| 2-3 services, simple flow | Choreography |
| 4+ services, complex business rules | **Orchestration** |
| Need visibility into saga state | Orchestration |
| Want maximum decoupling | Choreography |

---

## 🎤 Interview Tip

> *"I'd use an orchestration-based SAGA for the checkout flow because it has 4+ steps
> with complex failure handling. The orchestrator makes it easy to see the entire
> workflow in one place and add compensating actions for each step."*

---

*Next: [05 — Inventory Management →](./05-inventory.md)*
