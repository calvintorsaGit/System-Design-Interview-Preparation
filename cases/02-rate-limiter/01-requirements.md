# 📋 Step 1: Clarify Requirements

> What are we limiting and why?

---

## Questions to Ask

| Question | Typical Answer |
|----------|---------------|
| **Client-side or Server-side?** | Server-side API protection. |
| **What kind of limit?** | IP-based, User ID based, or API Key based. |
| **Scale?** | 1 Million active users, handling high throughput. |
| **Distributed?** | Yes, multiple servers must share the limit count. |
| **UX?** | Return `429 Too Many Requests` immediately. |

---

## ✅ Final Requirements

### Functional
1.  Limit requests based on **User ID** (or IP as fallback).
2.  Allow $X$ requests per minute/second.
3.  Return HTTP `429` when limit exceeded.

### Non-Functional
1.  **Low Latency**: Overhead < 20ms added to request.
2.  **High Accuracy**: Distributed servers must sync counts.
3.  **Fault Tolerant**: If limiter fails, default to allow traffic (fail-open) to avoid downtime.

---

*Next: [02 - High-Level Design →](./02-high-level-design.md)*
