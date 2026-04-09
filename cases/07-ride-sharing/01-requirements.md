# 📋 Step 1: Clarify Requirements

> Ride-sharing is a real-time, location-heavy system.

---

## Questions to Ask

| Question | Typical Answer |
|----------|---------------|
| Car rides only, or also bikes/food delivery? | Cars only (to scope it) |
| How many drivers / riders? | 1M drivers, 10M riders |
| Peak concurrent ride requests? | 100K requests/min |
| Do we need surge pricing? | Yes |
| Real-time tracking during trip? | Yes, update every 3-5 seconds |
| ETA accuracy requirement? | Within 2-3 min of actual |

---

## ✅ Final Requirements

### Functional
1. **Rider requests ride** — input pickup & destination
2. **Find nearby drivers** — within 5 km radius
3. **Match & dispatch** — assign best driver, driver accepts/rejects
4. **Real-time tracking** — show driver location on map during trip
5. **Trip completion & payment** — calculate fare, charge rider
6. **Surge pricing** — dynamic multiplier based on demand
7. **Rating** — rider rates driver and vice versa

### Non-Functional
1. **Matching latency**: < 10 seconds from request to driver match
2. **Location update frequency**: every 3 seconds per active driver
3. **Scale**: 1M active drivers updating location = 333K writes/sec
4. **Availability**: 99.99%
5. **Consistency**: trip and payment must be strongly consistent

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
