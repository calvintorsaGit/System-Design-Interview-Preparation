# 📋 Step 1: Clarify Requirements

> Chat systems mix real-time communication with persistent storage — get the scope right.

---

## Questions to Ask

| Question | Typical Answer | Why It Matters |
|----------|---------------|----------------|
| 1-on-1 chat, group chat, or both? | Both | Group chat adds fan-out complexity |
| Max group size? | 500 members | Determines fan-out strategy |
| Do we need read receipts? | Yes (sent ✓, delivered ✓✓, read ✓✓) | Adds acknowledgement flow |
| File/image sharing? | Yes, up to 10MB | Need blob storage + CDN |
| Online/last-seen status? | Yes | Presence service design |
| How long do we store messages? | Forever | Storage estimation changes |
| End-to-end encryption? | Mention, but don't deep dive | Shows security awareness |
| Expected scale? | 100M DAU, 50 messages/user/day = 5B messages/day | Drives architecture decisions |

---

## ✅ Final Requirements

### Functional
1. **1-on-1 messaging** — send text messages between two users
2. **Group messaging** — send to groups of up to 500 members
3. **Delivery status** — sent → delivered → read
4. **Online presence** — show online/offline/last-seen
5. **Message history** — persistent, scrollable chat history
6. **Push notifications** — notify offline users
7. **Media sharing** — images, files via upload + CDN

### Non-Functional
1. **Real-time delivery**: < 200ms latency for online users
2. **Message ordering**: messages appear in send order
3. **Reliability**: zero message loss
4. **Scale**: 100M DAU, 5B messages/day ≈ 60K messages/sec
5. **Availability**: 99.99%

---

## 🎤 Interview Tip

> *"I'll design a WhatsApp-like chat system supporting 1-on-1 and group messaging
> for 100M DAU. Key challenges are: maintaining persistent WebSocket connections
> at scale, guaranteeing message ordering and delivery, and handling offline users."*

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
