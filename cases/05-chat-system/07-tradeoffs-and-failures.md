# ⚠️ Step 7: Trade-offs & Failure Scenarios

---

## Key Trade-offs

| Decision | Option A | Option B | Our Choice |
|----------|----------|----------|------------|
| Protocol | HTTP Long Polling | WebSocket | **WebSocket** — lower latency, bidirectional |
| Message Store | PostgreSQL | Cassandra | **Cassandra** — write-heavy, time-series access |
| Message Broker | Redis Pub/Sub | Kafka | **Kafka** — durable, handles back-pressure |
| Presence | Push to all friends | Pull on demand | **Pull** — avoids thundering herd |
| Ordering | Wall-clock time | Sequence numbers | **Sequence numbers** — clock skew safe |

---

## Failure Scenarios

| What Breaks | Impact | Mitigation |
|-------------|--------|------------|
| Chat server crashes | Users on that server disconnect | Client auto-reconnects to another server; pending messages in Kafka |
| Kafka broker down | Message delivery paused | Kafka replication (3 replicas); messages buffered on chat server |
| Cassandra node down | Message persistence delayed | Replication factor 3; write to any 2 of 3 nodes (quorum) |
| Redis (presence) down | Can't show online status | Degrade gracefully; hide presence, chat still works |
| Network partition | Users on different sides can't reach each other | Messages queued in Kafka; delivered when partition heals |

---

## 🎤 Interview Tip

> *"The key principle is: chat delivery must never lose messages. Even if a server
> crashes, messages are safely in Kafka. And I'd degrade gracefully — if presence
> is down, I'd hide the online dot rather than break the entire chat."*

---

*Next: [08 — Interview Script →](./08-interview-script.md)*
