# 🔌 Step 3: WebSocket & Connection Management

> The hardest part of a chat system is managing millions of persistent connections.

---

## WebSocket Handshake

```
Client                              Chat Server
  │                                      │
  │  GET /ws HTTP/1.1                    │
  │  Upgrade: websocket                  │
  │  Connection: Upgrade                 │
  │  Sec-WebSocket-Key: xxx              │
  │ ───────────────────────────────────► │
  │                                      │
  │  HTTP/1.1 101 Switching Protocols    │
  │  Upgrade: websocket                  │
  │ ◄─────────────────────────────────── │
  │                                      │
  │  ══════ WebSocket open ══════        │
  │  (TCP connection stays open)         │
```

---

## Connection Registry

When a user connects, we register which server they're on:

```
Redis:
  user:alice → chat-server-2
  user:bob   → chat-server-1
  user:carol → chat-server-3
```

### Spring Boot WebSocket Example

```java
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, String> redis;
    private final Map<String, WebSocketSession> localSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        localSessions.put(userId, session);

        // Register in Redis: this user is on THIS server
        redis.opsForValue().set("conn:" + userId, serverId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        localSessions.remove(userId);
        redis.delete("conn:" + userId);
    }
}
```

---

## Message Routing: Same Server vs Cross-Server

### Case 1: Both users on the same server
```
Alice ──ws──► Chat Server #1 ──ws──► Bob
                 (direct)
```

### Case 2: Users on different servers
```
Alice ──ws──► Chat Server #1
                    │
                    │ Publish to Kafka/Redis Pub-Sub
                    ▼
              Chat Server #2 ──ws──► Bob
```

### Cross-Server Routing with Redis Pub/Sub

```java
// Server 1: Alice sends message
public void handleMessage(String fromUser, String toUser, String content) {
    String targetServer = redis.opsForValue().get("conn:" + toUser);

    if (targetServer.equals(THIS_SERVER)) {
        // Direct delivery
        localSessions.get(toUser).sendMessage(new TextMessage(content));
    } else {
        // Publish to channel that target server subscribes to
        redis.convertAndSend("chat:" + targetServer, 
            new ChatMessage(fromUser, toUser, content));
    }
}
```

---

## Scaling WebSocket Servers

| Challenge | Solution |
|-----------|----------|
| Each server holds N connections | Scale horizontally (add more servers) |
| Load balancer must use sticky sessions | Use `user_id` hash for routing or IP hash |
| Reconnection storms | Implement exponential backoff on client |
| Memory per connection (~10KB) | 500K connections = ~5GB RAM per server |

---

## 🎤 Interview Tip

> *"WebSocket connections are stateful, so I'd use consistent hashing at the LB
> to route each user to the same server. For cross-server delivery, I'd use
> Redis Pub/Sub for real-time routing and Kafka as the durable backbone."*

---

*Next: [04 — Message Flow →](./04-message-flow.md)*
