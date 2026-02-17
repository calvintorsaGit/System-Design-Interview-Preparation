# 🌐 Step 4: Distributed Implementation

> How to count correctly when you have 10 servers.

---

## The Race Condition Problem
If Server 1 reads `count=99` and Server 2 reads `count=99` at the same time:
- Both try to increment to 100.
- Both allow the request.
- One request should have been rejected!

## Solution: Redis + Lua Script (Atomic Operations)

Execute **check-and-decrement** as a single atomic step in Redis.

```lua
-- Lua Script for Token Bucket
local key = KEYS[1]
local tokens_needed = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local refill_rate = tonumber(ARGV[3])
local now = tonumber(ARGV[4])

-- Get current state
local state = redis.call("HMGET", key, "tokens", "last_refill")
local current_tokens = tonumber(state[1])
local last_refill = tonumber(state[2])

-- Refill logic
local elapsed = now - last_refill
local new_tokens = math.min(capacity, current_tokens + (elapsed * refill_rate))

-- Check logic
if new_tokens >= tokens_needed then
    new_tokens = new_tokens - tokens_needed
    redis.call("HMSET", key, "tokens", new_tokens, "last_refill", now)
    redis.call("EXPIRE", key, 60) -- cleanup
    return 1 -- Allowed
else
    return 0 -- Rejected (429)
end
```

## Spring Boot Integration (`Redisson` library)

Use a library that wraps this logic for you!

```java
@Component
public class RateLimiterService {

    private final RRateLimiter rateLimiter;

    public RateLimiterService(RedissonClient redisson) {
        this.rateLimiter = redisson.getRateLimiter("user:123");
        // 5 permits per 2 seconds
        rateLimiter.trySetRate(RateType.OVERALL, 5, 2, RateIntervalUnit.SECONDS);
    }

    public boolean allowRequest() {
        return rateLimiter.tryAcquire(1);
    }
}
```

---

*Next: [05 - Interview Script →](./05-interview-script.md)*
