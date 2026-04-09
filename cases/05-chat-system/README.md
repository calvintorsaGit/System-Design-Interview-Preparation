# Case 5: Chat System (like WhatsApp)

> Real-time messaging at scale — tests your knowledge of WebSocket, message ordering, and delivery guarantees.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | 1-on-1 chat, group chat, online status |
| [02-high-level-design.md](./02-high-level-design.md) | Connection layer, chat service, message store |
| [03-websocket-and-connections.md](./03-websocket-and-connections.md) | WebSocket vs polling, connection management |
| [04-message-flow.md](./04-message-flow.md) | Send, deliver, read receipts, ordering |
| [05-presence-service.md](./05-presence-service.md) | Online/offline/last-seen design |
| [06-group-chat.md](./06-group-chat.md) | Fan-out to group members, membership management |
| [07-tradeoffs-and-failures.md](./07-tradeoffs-and-failures.md) | Offline delivery, message loss prevention |
| [08-interview-script.md](./08-interview-script.md) | How to present this in an interview |

---

## 🎯 Key Concepts

- ✅ **WebSocket** — persistent bi-directional connections
- ✅ **Message Ordering** — sequence numbers & timestamps
- ✅ **Delivery Semantics** — sent, delivered, read
- ✅ **Presence / Heartbeat** — online/offline detection
- ✅ **Offline Message Queue** — store-and-forward
- ✅ **End-to-End Encryption** — Signal Protocol basics

---

## ⏱️ Estimated Study Time: 2-3 hours

Start with `01-requirements.md` and work through in order.
