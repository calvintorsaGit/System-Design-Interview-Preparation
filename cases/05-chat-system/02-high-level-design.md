# 🏛️ Step 2: High-Level Design

> The chat system has two distinctive layers: a **real-time connection layer** and a **persistent storage layer**.

---

## Architecture Diagram

```
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Mobile  │   │   Web    │   │  Desktop │
│  Client  │   │  Client  │   │  Client  │
└────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │              │
     └──────────────┼──────────────┘
                    │  WebSocket
                    ▼
          ┌──────────────────┐
          │   Load Balancer  │
          │ (Sticky Sessions)│
          └────────┬─────────┘
                   │
     ┌─────────────┼─────────────┐
     ▼             ▼             ▼
┌─────────┐  ┌─────────┐  ┌─────────┐
│  Chat   │  │  Chat   │  │  Chat   │
│ Server  │  │ Server  │  │ Server  │
│   #1    │  │   #2    │  │   #3    │
└────┬────┘  └────┬────┘  └────┬────┘
     │            │            │
     └────────────┼────────────┘
                  │
    ┌─────────────┼──────────────────┐
    ▼             ▼                  ▼
┌────────┐  ┌──────────┐    ┌─────────────┐
│Message │  │ Presence │    │   Message   │
│ Queue  │  │ Service  │    │   Store     │
│(Kafka) │  │ (Redis)  │    │(Cassandra/  │
│        │  │          │    │ HBase)      │
└────────┘  └──────────┘    └─────────────┘
```

---

## Component Responsibilities

### 1. Chat Servers (WebSocket Gateway)
- Maintain **persistent WebSocket connections** with clients
- Each server holds ~100K-500K concurrent connections
- **Stateful** — must know which users are connected to which server
- Routes messages between users

### 2. Connection Registry (Redis / ZooKeeper)
- Maps: `user_id → chat_server_id`
- When User A sends to User B: lookup which server B is on, route the message there
- Updated on connect/disconnect

### 3. Message Queue (Kafka)
- Decouples message sending from delivery
- Guarantees **at-least-once delivery**
- Handles spikes — if a chat server is overwhelmed, messages buffer in Kafka

### 4. Message Store (Cassandra / HBase)
- **Write-heavy** — 60K inserts/sec
- **Time-series access pattern** — "show me messages in chat X, sorted by time"
- Cassandra is ideal: partition by `chat_id`, cluster by `timestamp`

### 5. Presence Service (Redis)
- Tracks online/offline/last-seen for each user
- Uses **heartbeat** (client pings every 30s)
- If no heartbeat for 60s → mark offline

### 6. Push Notification Service
- For **offline users** — sends push via APNs (iOS) / FCM (Android)
- Triggered when message delivery fails (user not connected)

---

## Key Design Decision: Why WebSocket?

| Approach | Latency | Server Load | Bi-directional? | Best For |
|----------|---------|-------------|-----------------|----------|
| **HTTP Polling** | High (client polls every N sec) | Very High | No | Simple dashboards |
| **Long Polling** | Medium | High | No | Notifications |
| **Server-Sent Events** | Low | Medium | No (server → client only) | Live feeds |
| **WebSocket** ✅ | Very Low | Low | Yes | **Chat, gaming** |

> WebSocket maintains a persistent TCP connection. After the initial HTTP handshake,
> both client and server can send data at any time without the overhead of HTTP headers.

---

## 🎤 Interview Tip

> *"I'd use WebSocket for the connection layer because chat requires low-latency
> bi-directional communication. The chat servers are stateful — they maintain
> connection maps — while the message processing pipeline behind them is stateless
> and horizontally scalable via Kafka."*

---

*Next: [03 — WebSocket & Connections →](./03-websocket-and-connections.md)*
