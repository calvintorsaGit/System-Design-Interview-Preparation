# 🏛️ Step 2: High-Level Design

> Decouple everything with queues.

---

## The Flow

```
   Service A (Order)      Service B (Auth)
         │                      │
         ▼                      ▼
  ┌────────────────────────────────────┐
  │      Notification Service API      │
  │     (Validates request, dedups)    │
  └─────────────────┬──────────────────┘
                    │
           Publish Message (Async)
                    │
          ┌─────────▼─────────┐
          │   Message Queue   │ ◄── Kafka / RabbitMQ
          │ (Topic: "email")  │
          └─────────┬─────────┘
                    │
           Consume Message
                    │
          ┌─────────▼─────────┐
          │  Worker Service   │ ◄── Stateless Consumers
          │ (Retry logic here)│
          └─────┬────────┬────┘
                │        │
      ┌─────────▼─┐    ┌─▼─────────┐
      │ SendGrid  │    │  Twilio   │
      └───────────┘    └───────────┘
```

## Why this design? (Interview Answer)
- **Decoupling**: The Order Service doesn't wait for the email to send.
- **Buffering**: If SendGrid is down, messages wait in the queue. They are safe.
- **Scaling**: If queue grows large, we just add more Worker instances.

---

*Next: [03 - Message Queues →](./03-message-queues.md)*
