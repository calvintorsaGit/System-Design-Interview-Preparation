# 👥 Step 6: Group Chat

> Group chat adds a fan-out challenge — one message must reach N members.

---

## Data Model

```sql
-- Group metadata
CREATE TABLE chat_groups (
    group_id   TEXT PRIMARY KEY,
    name       TEXT,
    created_by TEXT,
    created_at TIMESTAMP
);

-- Membership
CREATE TABLE group_members (
    group_id   TEXT,
    user_id    TEXT,
    role       TEXT,  -- 'admin', 'member'
    joined_at  TIMESTAMP,
    PRIMARY KEY (group_id, user_id)
);
```

---

## Message Fan-out for Groups

When Alice sends "Hello" to a 200-member group:

```
Alice ──ws──► Chat Server
                  │
                  │ 1. Persist message to Cassandra
                  │ 2. Get group members (200 users)
                  │ 3. For each online member:
                  │    - Lookup which chat server they're on
                  │    - Route message to that server
                  │ 4. For each offline member:
                  │    - Send push notification
                  │
                  ├──► Chat Server #1 → Bob, Charlie, Dave (online)
                  ├──► Chat Server #2 → Eve, Frank (online)
                  └──► Push Service → 195 offline members
```

### Group Size Limits

| Group Size | Fan-out Strategy | Latency |
|------------|-----------------|---------|
| Small (< 50) | Direct fan-out | < 200ms |
| Medium (50-500) | Kafka-backed fan-out | < 1s |
| Large / Channel (500+) | Pull-based (members fetch on open) | On demand |

---

## 🎤 Interview Tip

> *"For small groups I'd do direct fan-out through WebSocket servers.
> For large channels (like Slack), I'd switch to a pull model — members
> fetch new messages when they open the channel, reducing write amplification."*

---

*Next: [07 — Trade-offs & Failures →](./07-tradeoffs-and-failures.md)*
