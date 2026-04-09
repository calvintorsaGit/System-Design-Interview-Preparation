# 📍 Step 5: Location Tracking

> 1M drivers sending GPS every 3 seconds = 333K writes/sec. This is a data firehose.

---

## Ingestion Pipeline

```
Driver App                      Backend
  │  { driverId, lat, lng,        │
  │    timestamp, speed,          │
  │    heading }                  │
  │  ─── every 3 sec ──────────► │
  │                               │ 1. Update Redis GEO (current position)
  │                               │ 2. Publish to Kafka (for history/analytics)
  │                               │ 3. Push to rider's map (if in active trip)
```

### Why Not Store Every Update in a Database?

```
333K writes/sec × 100 bytes = 33 MB/sec = 2.8 TB/day
```

**Solution**: 
- **Redis** for current position (overwrite on each update)
- **Kafka → S3/Data Lake** for historical tracking (batch writes)
- Only persist to DB for **active trips** (much smaller volume)

---

## Pushing Location to Rider (During Trip)

```java
// When driver updates location during an active trip
public void onDriverLocationUpdate(String driverId, Location loc) {
    // Update current position
    redis.opsForGeo().add("drivers:active", 
        new Point(loc.getLng(), loc.getLat()), driverId);
    
    // If driver is on an active trip, push to rider
    String tripId = redis.opsForValue().get("driver:trip:" + driverId);
    if (tripId != null) {
        String riderId = tripService.getRiderId(tripId);
        websocketService.sendToUser(riderId, new LocationUpdate(loc));
    }
}
```

---

## ETA Estimation

| Approach | Accuracy | Speed |
|----------|----------|-------|
| **Straight-line distance / avg speed** | Low | Fast |
| **Road network graph (Dijkstra/A*)** | High | Medium |
| **ML model (historical trip data)** | Highest | Medium |
| **External API (Google Maps)** | Very High | Depends on API |

For an interview, mention the graph approach and ML, but implement with simple estimation:

```java
public int estimateEtaMinutes(Location driver, Location destination) {
    double distanceKm = haversineDistance(driver, destination);
    double avgSpeedKmh = 30;  // Urban average
    return (int) Math.ceil(distanceKm / avgSpeedKmh * 60);
}
```

---

*Next: [06 — Trade-offs & Failures →](./06-tradeoffs-and-failures.md)*
