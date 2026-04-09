# 📨 Step 4: Message Flow & Delivery

> The heart of the chat system — how a message travels from sender to receiver.

---

## End-to-End Message Flow

```
Alice (sender)                Chat Server          Kafka           Cassandra        Bob (receiver)
     │                             │                 │                │                  │
  1. │  Send message via WS        │                 │                │                  │
     │ ──────────────────────────► │                 │                │                  │
     │                             │                 │                │                  │
  2. │                             │  Produce msg    │                │                  │
     │                             │ ──────────────► │                │                  │
     │                             │                 │                │                  │
  3. │  ACK (sent ✓)               │                 │                │                  │
     │ ◄────────────────────────── │                 │                │                  │
     │                             │                 │                │                  │
  4. │                             │  Consume        │                │                  │
     │                             │ ◄────────────── │                │                  │
     │                             │                 │                │                  │
  5. │                             │  Persist msg    │                │                  │
     │                             │ ────────────────────────────►   │                  │
     │                             │                 │                │                  │
  6. │                             │  Deliver to Bob via WS          │                  │
     │                             │ ───────────────────────────────────────────────►   │
     │                             │                 │                │                  │
  7. │                             │  ACK from Bob   │                │                  │
     │                             │ ◄─────────────────────────────────────────────────│
     │                             │                 │                │                  │
  8. │  Delivered ✓✓                │                 │                │                  │
     │ ◄────────────────────────── │                 │                │                  │
```

---

## Message States

| State | Meaning | Trigger |
|-------|---------|---------|
| ✓ **Sent** | Server received the message | Server ACKs to sender |
| ✓✓ **Delivered** | Recipient's device received it | Recipient ACKs to server |
| ✓✓ **Read** (blue) | Recipient opened the chat | Recipient sends "read" event |

---

## Message Ordering

### Problem: Messages can arrive out of order
If Alice sends "Hello" then "How are you?", Bob must see them in that order.

### Solution: Sequence Numbers per Conversation

```java
public class ChatMessage {
    private String messageId;       // UUID
    private String chatId;          // conversation identifier
    private long sequenceNumber;    // monotonically increasing per chat
    private String senderId;
    private String content;
    private Instant timestamp;
    private MessageStatus status;   // SENT, DELIVERED, READ
}
```

```sql
-- Cassandra schema (partition by chat_id, order by sequence)
CREATE TABLE messages (
    chat_id     TEXT,
    sequence_no BIGINT,
    message_id  UUID,
    sender_id   TEXT,
    content     TEXT,
    created_at  TIMESTAMP,
    PRIMARY KEY (chat_id, sequence_no)
) WITH CLUSTERING ORDER BY (sequence_no ASC);
```

---

## Handling Offline Users

When Bob is offline:
1. Message is stored in Cassandra with status = `SENT`
2. Push notification sent via APNs/FCM
3. When Bob comes online → server queries undelivered messages → delivers in bulk → updates status to `DELIVERED`

```java
// On user reconnect
public void onUserOnline(String userId) {
    List<ChatMessage> pending = messageStore.findUndelivered(userId);
    for (ChatMessage msg : pending) {
        deliverToUser(userId, msg);
        msg.setStatus(MessageStatus.DELIVERED);
        messageStore.save(msg);
    }
}
```

---

## 🎤 Interview Tip

> *"I'd use a Kafka-backed pipeline for durable message delivery. The sender
> gets an ACK once the message is in Kafka (sent ✓). Delivery and read receipts
> are separate events flowing backward to the sender. For offline users,
> messages are persisted and replayed on reconnection."*

---

*Next: [05 — Presence Service →](./05-presence-service.md)*
