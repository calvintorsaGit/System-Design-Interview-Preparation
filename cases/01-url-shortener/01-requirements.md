# 📋 Step 1: Clarify Requirements

> In an interview, **never** jump straight into design. Spend 3-5 minutes asking
> clarifying questions. This shows maturity and prevents wasted effort.

---

## Questions to Ask the Interviewer

### Functional Requirements

| Question | Typical Answer | Why It Matters |
|----------|---------------|----------------|
| Can users create custom short URLs? | Yes, optional | Adds collision checking logic |
| Do URLs expire? | Yes, configurable TTL | Need expiration tracking + cleanup |
| Do we track analytics (click count, referrer)? | Yes, basic analytics | Separate analytics write path |
| Do users need accounts, or is it anonymous? | Both | Auth layer + ownership tracking |

### Non-Functional Requirements

| Question | Typical Answer | Why It Matters |
|----------|---------------|----------------|
| What's the expected scale? | 100M URLs created/month | Drives DB & caching decisions |
| Read-to-write ratio? | 10:1 (reads dominate) | Cache-heavy architecture |
| What's the acceptable latency for redirect? | < 100ms | Need caching layer |
| How long do we store URLs? | 5 years | Drives storage estimation |
| Must it be highly available? | Yes, 99.99% | Redundancy, no SPOF |

---

## ✅ Final Requirements Summary

### Functional
1. Given a long URL → generate a short URL (e.g., `https://short.ly/abc123`)
2. Given a short URL → redirect (HTTP 301/302) to the original long URL
3. Optional: custom alias (e.g., `https://short.ly/my-promo`)
4. Optional: URL expiration
5. Optional: click analytics

### Non-Functional
1. **Low latency**: redirect < 100ms
2. **High availability**: the system should always be reachable (99.99%)
3. **Read-heavy**: read:write = 10:1
4. **Scale**: 100M new URLs per month
5. **URL should not be guessable** (no sequential IDs like `/1`, `/2`, `/3`)

---

## 🎤 Interview Tip

> Say out loud: *"Let me make sure I understand the scope. We need a service where
> users submit a long URL and get back a short one. When someone visits the short URL,
> they get redirected. The system is read-heavy and needs to be highly available.
> Are there any other features you'd like me to consider?"*

This shows the interviewer you're structured and collaborative.

---

*Next: [02 - Estimation →](./02-estimation.md)*
