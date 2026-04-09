# 🎤 Step 8: Interview Script

> Use this as a guide for how to walk through the design in a 45-minute interview.

---

## Opening (2 min)

> *"I'll design a WhatsApp-like chat system. The key challenges are: maintaining
> millions of persistent WebSocket connections, guaranteeing message ordering and
> delivery, and handling users who go offline."*

## Requirements (3 min)
- Clarify: 1-on-1 + group chat, 100M DAU, delivery receipts, presence

## Estimation (3 min)
- 100M DAU × 50 msgs/day = 5B messages/day ≈ 60K msgs/sec
- Each message ~200 bytes → 1 TB/day raw storage
- Need write-optimized DB (Cassandra)

## High-Level Design (10 min)
- Draw: Client → LB → Chat Servers → Kafka → Message Store
- Explain WebSocket choice and connection registry
- Show cross-server routing via Redis Pub/Sub

## Deep Dive (20 min)
Pick 2-3 of these based on interviewer interest:
1. **Message delivery flow** — sent/delivered/read status
2. **Group chat fan-out** — small vs large groups
3. **Presence service** — heartbeat + lazy evaluation
4. **Offline handling** — store-and-forward pattern

## Trade-offs & Wrap-up (5 min)
- WebSocket vs polling, Cassandra vs PostgreSQL, push vs pull presence
- Failure scenarios: server crash, Kafka down

---

> **Golden rule**: Always explain the *trade-off*, not just the choice.
> "I chose X over Y because…" is what separates senior from junior candidates.
