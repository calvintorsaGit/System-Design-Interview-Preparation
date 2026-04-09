# ⚠️ Step 6: Trade-offs & Failure Scenarios

---

## Key Trade-offs

| Decision | Option A | Option B | Our Choice |
|----------|----------|----------|------------|
| Location storage | PostgreSQL | Redis GEO | **Redis** — 333K writes/sec, in-memory |
| Matching model | First-come-first-served | Score-based ranking | **Score-based** — better UX |
| Location protocol | HTTP polling | WebSocket | **WebSocket** — lower overhead for frequent updates |
| ETA | Simple math | ML model | **Simple** for MVP, **ML** for production |

---

## Failure Scenarios

| What Breaks | Impact | Mitigation |
|-------------|--------|------------|
| Driver app crashes mid-trip | Rider stranded, fare uncalculated | Last known location + timeout → auto-complete or reassign |
| GPS drift (inaccurate location) | Wrong ETA, bad matching | Kalman filter on client, reject outlier updates |
| Redis (location) down | Can't find nearby drivers | Fallback to DB-based geospatial query (slower) |
| Driver doesn't accept in time | Rider waits | 15s timeout → auto-reject → next driver |
| Surge pricing bug | Users overcharged | Price cap + manual review for trips > $X |

---

## 🎤 Interview Script (Quick Version)

> *"I'll design a Grab-like system. The two novel challenges are geospatial matching
> using Redis GEO (GEORADIUS for nearby drivers) and ingesting 333K location updates/sec.
> I'd use a score-based matching algorithm weighted by distance, rating, and acceptance rate,
> with Redis locks to prevent double-matching."*

---

*Next: [07 — Interview Script →](./07-interview-script.md)*
