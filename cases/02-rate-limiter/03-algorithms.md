# 🧮 Step 3: Core Algorithms

> Understand the "Big 4" algorithms. **Token Bucket** is usually the right answer.

---

## 1. Token Bucket (Standard ✅)
- **Concept**: A bucket holds `N` tokens. Refill `R` tokens per second.
- **Request**: Takes 1 token. If bucket empty → 429.
- **Pros**: Allows **bursts** of traffic. Memory efficient.
- **Cons**: Slightly complex to implement distributedly.

## 2. Leaky Bucket
- **Concept**: Queue with constant output rate.
- **Request**: Adds to queue. If queue full → 429.
- **Pros**: Smoothes out traffic bursts (stable rate).
- **Cons**: Drops bursts of valid traffic.

## 3. Fixed Window Counter
- **Concept**: Count requests in `12:00-12:01`, reset at `12:01`.
- **Pros**: Simple atomic counter.
- **Cons**: **Spike at edges** (e.g., 100 reqs at 12:00:59 and 100 reqs at 12:01:01 = 200 reqs in 2 seconds).

## 4. Sliding Window Log
- **Concept**: Store timestamp of every request. Count logs in last minute.
- **Pros**: Highly accurate.
- **Cons**: **Expensive memory** (stores every timestamp).

---

### 💡 Recommendation
> Use **Token Bucket** or **Sliding Window Counter** (a hybrid of 3 & 4).
> Amazon and Stripe use Token Bucket.

---

*Next: [04 - Distributed Implementation →](./04-distributed-implementation.md)*
