# 🎤 Step 5: The Interview Script

> Concise summary for the Rate Limiter case.

---

## 1. Requirements
"I'll design a distributed API rate limiter that handles high throughput with low latency (<20ms overhead). It will limit by User ID or IP."

## 2. Placement
"I'll place the limiter at the **API Gateway** level so it protects all downstream microservices centrally."

## 3. Algorithm
"I'll use the **Token Bucket** algorithm because it's memory efficient and allows for small bursts of traffic (unlike Leaky Bucket)."

## 4. Distributed State
"To handle synchronization across multiple gateway instances, I'll use **Redis** to store the counters. Since standard Redis commands act separately (GET then SET), I'll use a **Lua script** to make the `check-refill-decrement` operation **atomic** and prevent race conditions."

## 5. Failure Mode
"If Redis goes down, the limiter will **fail open** (allow all traffic) to ensure availability is prioritized over strict limiting."

---

*Case 2 Completed! Ready for Case 3?*
