# Case 7: Ride-Sharing System (like Gojek/Grab)

> Location-aware, real-time matching — tests geospatial indexing, ETA estimation, and live tracking.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | Rider ↔ Driver matching, ETA, pricing |
| [02-high-level-design.md](./02-high-level-design.md) | Core services: Matching, Location, Trip, Payment |
| [03-geospatial-indexing.md](./03-geospatial-indexing.md) | Geohash, QuadTree, finding nearby drivers |
| [04-matching-algorithm.md](./04-matching-algorithm.md) | How to match rider with best driver |
| [05-location-tracking.md](./05-location-tracking.md) | Real-time GPS updates at scale |
| [06-tradeoffs-and-failures.md](./06-tradeoffs-and-failures.md) | Driver goes offline mid-trip, GPS drift |
| [07-interview-script.md](./07-interview-script.md) | How to present this |

---

## 🎯 Key Concepts

- ✅ **Geospatial Indexing** — Geohash, QuadTree, S2 Cells
- ✅ **Real-time Location Tracking** — high-frequency GPS updates
- ✅ **Matching Algorithm** — proximity + ETA + driver rating
- ✅ **ETA Estimation** — graph algorithms on road networks
- ✅ **Surge Pricing** — dynamic supply-demand pricing
- ✅ **Trip State Machine** — requested → matched → picked up → completed

---

## ⏱️ Estimated Study Time: 2-3 hours

Start with `01-requirements.md` and work through in order.
