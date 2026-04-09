# 🤝 Step 4: Matching Algorithm

> Finding the "best" driver isn't just about distance.

---

## Matching Score

```java
public double calculateMatchScore(Driver driver, RideRequest ride) {
    double distanceScore = 1.0 / (1 + distanceKm(driver, ride.getPickup()));  // Closer = higher
    double ratingScore   = driver.getRating() / 5.0;                          // Higher rating = better
    double acceptScore   = driver.getAcceptanceRate();                        // Reliable drivers first

    return (0.5 * distanceScore) + (0.3 * ratingScore) + (0.2 * acceptScore);
}
```

---

## Dispatch Flow

```
Rider requests ride
        │
        ▼
  1. GEORADIUS → Get 20 nearest available drivers
        │
        ▼
  2. Score each driver (distance + rating + acceptance rate)
        │
        ▼
  3. Sort by score, pick top driver
        │
        ▼
  4. Send request to driver (push notification)
        │
        ├── Driver ACCEPTS → Create trip, notify rider
        │
        └── Driver REJECTS / TIMEOUT (15s) → Try next driver
              │
              └── All 20 reject → Expand radius, retry
                    │
                    └── Still failed → "No drivers available"
```

---

## Handling Concurrent Requests

**Problem**: Two riders request at the same time, both get matched to the same driver.

**Solution**: Driver lock with Redis

```java
public boolean tryLockDriver(String driverId, String rideRequestId) {
    // SET driver:lock:{id} {rideRequestId} NX EX 30
    Boolean locked = redis.opsForValue()
        .setIfAbsent("driver:lock:" + driverId, rideRequestId, 
            Duration.ofSeconds(30));
    return Boolean.TRUE.equals(locked);
}
```

---

*Next: [05 — Location Tracking →](./05-location-tracking.md)*
