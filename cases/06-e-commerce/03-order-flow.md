# 🛒 Step 3: Order Flow

> The checkout pipeline is a multi-step, multi-service process. This is WHERE things get hard.

---

## Checkout Flow (Happy Path)

```
User clicks "Place Order"
         │
         ▼
  ┌─────────────────┐
  │  1. VALIDATE     │  Cart Service: items still in stock? prices correct?
  └────────┬────────┘
           ▼
  ┌─────────────────┐
  │  2. RESERVE      │  Inventory Service: temporarily hold stock
  │     INVENTORY    │  (decrement available, increment reserved)
  └────────┬────────┘
           ▼
  ┌─────────────────┐
  │  3. CREATE       │  Order Service: persist order with status = PENDING
  │     ORDER        │
  └────────┬────────┘
           ▼
  ┌─────────────────┐
  │  4. PROCESS      │  Payment Service: charge user via Stripe/Midtrans
  │     PAYMENT      │
  └────────┬────────┘
           ▼
  ┌─────────────────┐
  │  5. CONFIRM      │  Order Service: status = CONFIRMED
  │     ORDER        │  Inventory Service: reserved → sold
  └────────┬────────┘
           ▼
  ┌─────────────────┐
  │  6. NOTIFY       │  Notification: order confirmation email/push
  │                  │  Clear cart
  └─────────────────┘
```

---

## What If Payment Fails? (The Hard Part)

This is why we need the **SAGA pattern** — covered in the next file.

```
Payment fails at step 4:
  → Step 3: Cancel the order (status = CANCELLED)
  → Step 2: Release reserved inventory
  → Notify user: "Payment failed, please try again"
```

---

## Idempotency: Preventing Double Charges

### Problem
User clicks "Pay" twice, or network retries the payment request.

### Solution: Idempotency Key

```java
@PostMapping("/api/v1/payments")
public PaymentResponse processPayment(
    @RequestHeader("Idempotency-Key") String idempotencyKey,  // Client-generated UUID
    @RequestBody PaymentRequest request
) {
    // Check if this key was already processed
    Optional<Payment> existing = paymentRepo.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
        return toResponse(existing.get());  // Return same result, don't charge again
    }

    // Process payment
    Payment payment = stripeGateway.charge(request);
    payment.setIdempotencyKey(idempotencyKey);
    paymentRepo.save(payment);
    return toResponse(payment);
}
```

---

## 🎤 Interview Tip

> *"The checkout is a distributed transaction across 4 services. I'd use the
> SAGA pattern with compensating transactions instead of 2PC, because 2PC
> doesn't scale well in microservices. And every external API call — especially
> payments — must be idempotent to handle retries safely."*

---

*Next: [04 — SAGA Pattern →](./04-saga-pattern.md)*
