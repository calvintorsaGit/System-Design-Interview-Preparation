# 📨 Step 3: Message Queues & Topics

> How to organize the flow.

---

## Topic Strategy

We split queues by **channel** and **priority**.

### Kafka Topics:
1.  `notification.sms.high` (OTPs - fast!)
2.  `notification.email.high` (Password reset)
3.  `notification.email.low` (Marketing newsletter)

### Why separate priorities?
- We assign **more workers** to the `high` priority topics.
- Ensuring a marketing blast (1M emails) doesn't block a user from receiving their OTP.

---

## Spring Boot Listener Example

```java
@KafKaListener(topics = "notification.email.high", groupId = "email-workers")
public void sendHighPriorityEmail(String message) {
    // 1. Parse message
    EmailRequest request = parse(message);

    // 2. Send via Provider
    try {
        sendGrid.send(request);
    } catch (Exception e) {
        // 3. Handle failure (Retry or DLQ)
        retryOrDlq(request);
    }
}
```

---

*Next: [04 - Reliability →](./04-reliability.md)*
