# 🎤 Step 7: Interview Script

---

## Opening (2 min)
> *"I'll design a ride-sharing platform like Grab. The two hardest problems are:
> efficiently finding nearby drivers using geospatial indexing, and ingesting
> 333K GPS updates per second from active drivers."*

## Requirements (3 min)
- Rider requests ride, system matches best driver, real-time tracking
- 1M active drivers, 10M riders, peak 100K requests/min

## Estimation (3 min)
- 1M drivers × updates every 3s = 333K location writes/sec
- Each update ~100 bytes → 33 MB/sec
- Store in Redis (ephemeral) + Kafka (history)

## High-Level Design (10 min)
- Draw: Rider App → API GW → Trip/Matching/Location/Payment Services
- Redis GEO for driver positions, PostgreSQL for trips, Kafka for events

## Deep Dive (20 min)
1. **Geospatial indexing** — Geohash + Redis GEORADIUS
2. **Matching algorithm** — scoring (distance × rating × acceptance)
3. **Location ingestion** — Redis for current, Kafka for history
4. **Trip state machine** — requested → matched → in-progress → completed

## Trade-offs (5 min)
- Redis vs PostGIS, WebSocket vs HTTP for location, ETA approaches

---

> **Differentiator**: Show the GEORADIUS query and explain the matching score formula.
> Interviewers love concrete algorithms, not just box diagrams.
