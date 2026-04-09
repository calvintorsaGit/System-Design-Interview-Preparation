# 🟢 Step 5: Presence Service

> "Online", "Last seen 5 min ago" — a deceptively simple feature that's hard at scale.

---

## How Presence Works

### Heartbeat Model
1. Client sends a **heartbeat** ping every 30 seconds via WebSocket
2. Server updates `last_seen` timestamp in Redis
3. If no heartbeat for 60 seconds → mark as **offline**

```
Redis:
  presence:alice → { status: "online", last_seen: 1712345678 }
  presence:bob   → { status: "online", last_seen: 1712345650 }
  presence:carol → { status: "offline", last_seen: 1712344000 }
```

### Spring Boot Heartbeat Handler

```java
@Scheduled(fixedRate = 10_000) // Check every 10s
public void cleanupPresence() {
    long cutoff = Instant.now().minusSeconds(60).toEpochMilli();
    // Users with last_seen < cutoff → mark offline
    Set<String> onlineUsers = redis.opsForSet().members("online_users");
    for (String userId : onlineUsers) {
        String lastSeen = redis.opsForHash().get("presence:" + userId, "last_seen");
        if (Long.parseLong(lastSeen) < cutoff) {
            redis.opsForHash().put("presence:" + userId, "status", "offline");
            redis.opsForSet().remove("online_users", userId);
            broadcastPresenceChange(userId, "offline");
        }
    }
}
```

---

## Who Sees My Status?

### Fan-out Problem
If Alice has 500 friends, do we notify all 500 when she goes online?

### Solution: Lazy Evaluation
- **Don't push** presence to all friends
- When Bob opens a chat with Alice → **pull** Alice's status on demand
- Only **push** presence changes to users who are actively viewing that chat

```java
// Called when Bob opens chat with Alice
public String getPresence(String userId) {
    Map<Object, Object> data = redis.opsForHash().entries("presence:" + userId);
    if ("online".equals(data.get("status"))) {
        return "online";
    }
    return "Last seen " + formatTimestamp((String) data.get("last_seen"));
}
```

---

## Edge Cases

| Scenario | Solution |
|----------|----------|
| User switches networks (WiFi → 4G) | Brief disconnect → reconnect, presence stays "online" if within 60s |
| App killed / phone dies | No heartbeat → auto-offline after 60s |
| User on multiple devices | Track per-device; "online" if ANY device is active |

---

*Next: [06 — Group Chat →](./06-group-chat.md)*
