# 🌍 Step 3: Geospatial Indexing

> The core data structure problem: "Find all drivers within 5 km of this rider."

---

## Approach 1: Geohash

Geohash converts a 2D coordinate (lat, lng) into a 1D string:

```
(lat: -6.2088, lng: 106.8456) → "qqguw"  (precision 5 = ~5km cell)
```

### How It Works
1. Divide the world into a grid of cells
2. Each cell gets a string code
3. Nearby locations share a **common prefix**

```
Precision │ Cell Size
    1      │ 5000 km
    2      │ 1250 km
    3      │ ~156 km
    4      │ ~39 km
    5      │ ~5 km    ← Good for "nearby drivers"
    6      │ ~1.2 km
    7      │ ~150 m
```

### Finding Nearby Drivers with Geohash

```java
// Driver location update
public void updateDriverLocation(String driverId, double lat, double lng) {
    String geohash = Geohash.encode(lat, lng, 6);  // ~1.2 km precision
    
    // Redis: store driver in geospatial index
    redis.opsForGeo().add("drivers:active", 
        new Point(lng, lat), driverId);
}

// Find nearby drivers
public List<String> findNearbyDrivers(double lat, double lng, double radiusKm) {
    GeoResults<RedisGeoCommands.GeoLocation<String>> results =
        redis.opsForGeo().radius("drivers:active",
            new Circle(new Point(lng, lat), 
                new Distance(radiusKm, Metrics.KILOMETERS)),
            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .sortAscending()
                .limit(20)
        );
    
    return results.getContent().stream()
        .map(r -> r.getContent().getName())
        .toList();
}
```

---

## Approach 2: QuadTree

Recursively divide the map into 4 quadrants. Dense areas get more subdivisions.

```
┌─────────┬─────────┐
│         │    NE   │
│   NW    ├────┬────┤
│         │    │    │  ← Dense area gets subdivided more
├─────────┼────┴────┤
│         │         │
│   SW    │   SE    │
└─────────┴─────────┘
```

| | Geohash | QuadTree |
|---|---------|----------|
| **Type** | Hash (string) | Tree (in-memory) |
| **Good for** | Redis/DB index | In-memory spatial queries |
| **Edge cases** | Boundary issues (neighbors may have different prefix) | Rebalancing on updates |
| **Used by** | Redis GEO, Elasticsearch | Uber's H3, custom implementations |

---

## Redis GEO Commands (Production Choice ✅)

Redis has built-in geospatial support using a sorted set + geohash:

```
GEOADD drivers:active 106.8456 -6.2088 "driver:123"
GEOADD drivers:active 106.8500 -6.2100 "driver:456"

-- Find drivers within 5km of a point
GEORADIUS drivers:active 106.8456 -6.2088 5 km ASC COUNT 20
→ ["driver:123", "driver:456"]
```

---

## 🎤 Interview Tip

> *"I'd use Redis GEO commands — they use geohash internally and support
> GEORADIUS queries which give me all drivers within N km, sorted by distance.
> For 1M drivers, this runs in O(N+log(M)) time where N is results and M is total entries."*

---

*Next: [04 — Matching Algorithm →](./04-matching-algorithm.md)*
