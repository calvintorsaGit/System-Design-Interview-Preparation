# 🛡️ Step 4: Reliability & Retries

> What if the 3rd party fails?

---

## The Retry Mechanism

We don't want to lose messages if SendGrid is down for 5 minutes.

### Standard Retry (In-Memory)
- Try 3 times immediately with exp backoff (`1s, 2s, 4s`).
- **Problem**: Blocks the consumer thread.

### Queue-Based Retry (Better for long processing)
- If failed: Publish to a `retry-queue`.
- A separate consumer reads from `retry-queue` with a delay.

### Dead Letter Queue (DLQ)
- If a message fails after $N$ retries (e.g., email address is invalid), move it to a `dead-letter-queue`.
- **Manual Inspection**: Developers can look at the DLQ to debug why messages are failing.

---

## Rate Limiting the Workers
- 3rd party providers have limits (e.g., 1000 emails/sec).
- If we auto-scale to 100 workers, we might accidentally DDoS SendGrid.
- **Solution**: Use a distributed **Rate Limiter** (from Case 2!) on the worker side to respect provider limits.

---

*Next: [05 - Interview Script →](./05-interview-script.md)*
