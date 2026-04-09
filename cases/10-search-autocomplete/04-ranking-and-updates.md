# 📊 Step 4: Ranking & Real-Time Updates

---

## Ranking Formula

```java
score = (0.5 × frequency) + (0.3 × recency) + (0.2 × personalBoost)
```

| Factor | Description | Example |
|--------|-------------|---------|
| **Frequency** | How often this query is searched | "weather" = 10M/day → high |
| **Recency** | Time-decayed frequency | "olympic 2024" → high now, lower in 6 months |
| **Personal** | User's own search history | If you searched "python" often → boost "python" suggestions |

### Time-Decay Formula

```java
// Exponential decay — recent searches weigh more
public double decayedFrequency(long rawCount, Instant lastUpdated) {
    long hoursAgo = Duration.between(lastUpdated, Instant.now()).toHours();
    return rawCount * Math.exp(-0.01 * hoursAgo);
}
```

---

## Updating the Trie (Write Path)

### Approach: Periodic Rebuild (Not Real-Time Mutation)

Mutating a live Trie under 600K QPS is dangerous. Instead:

```
┌────────────┐     ┌──────────┐     ┌──────────────┐     ┌──────────┐
│  Search    │────►│  Kafka   │────►│  Aggregation │────►│  Build   │
│  Queries   │     │  Logs    │     │  (every 15m) │     │ New Trie │
└────────────┘     └──────────┘     └──────────────┘     └────┬─────┘
                                                              │
                                                              ▼ swap
                                                    ┌──────────────────┐
                                                    │ Trie Store       │
                                                    │ (atomic pointer  │
                                                    │  swap: old → new)│
                                                    └──────────────────┘
```

### Atomic Swap (Double Buffering)

```java
public class TrieStore {
    private volatile AutocompleteTrie activeTrie;

    // Called by rebuild job every 15 min
    public void swapTrie(AutocompleteTrie newTrie) {
        this.activeTrie = newTrie;  // Atomic reference swap
        // Old trie is garbage collected
    }

    public List<String> query(String prefix) {
        return activeTrie.getSuggestions(prefix);  // Always reads consistent snapshot
    }
}
```

---

## Trending Detection (Spike Detection)

```java
// If a query's frequency in the last hour is 10x its 7-day average → trending
public boolean isTrending(String query) {
    double lastHour = getCount(query, Duration.ofHours(1));
    double weeklyAvg = getCount(query, Duration.ofDays(7)) / (7 * 24);
    return lastHour > weeklyAvg * 10;
}
```

---

*Next: [05 — Scaling →](./05-scaling.md)*
