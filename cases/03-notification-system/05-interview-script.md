# 🎤 Step 5: The Interview Script

> Concise summary for the Notification System case.

---

## 1. Requirements
"I'll design a scalable notification service that supports Email, SMS, and Push. It must handle high throughput (10M pushes/hour) and prioritize OTPs over marketing messages."

## 2. Architecture
"I'll use an **Event-Driven Architecture**:
- **Producers** (Order Service) publish events to **Kafka**.
- **Topics** are split by channel and priority (e.g., `sms.high`, `email.low`).
- **Consumers** (Notification Workers) read messages and call 3rd-party APIs (SendGrid/Twilio)."

## 3. Reliability
"To ensure no messages are lost:
- **Retries**: If a provider fails, we retry with exponential backoff.
- **DLQ**: After Max Retries, failed messages go to a Dead Letter Queue for manual inspection."

## 4. Rate Limiting
"To avoid being blocked by SendGrid/Twilio, the consumers use a distributed **Rate Limiter** (Redis) to throttle outgoing requests."

---

*Case 3 Completed! Ready for Case 4?*
