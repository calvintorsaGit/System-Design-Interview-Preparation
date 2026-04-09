# 🎤 Step 7: Interview Script

---

## Opening (2 min)
> *"I'll design a Google-like search autocomplete. The core challenge is serving
> prefix-based suggestions in < 100ms at 600K QPS, while keeping suggestions
> fresh with trending queries."*

## Requirements (3 min)
- Prefix matching, top-5 suggestions, < 100ms, 600K QPS
- Trending queries reflected within 15 min

## Estimation (3 min)
- 10B searches/day × 5 keystrokes = 600K autocomplete QPS
- 1B unique queries × 20 chars avg = ~5 GB in Trie

## High-Level Design (10 min)
- Read: Client → Redis Cache → In-Memory Trie
- Write: Query Logs → Kafka → Aggregation → Trie Rebuild → Atomic Swap
- Separate read/write paths for different optimization goals

## Deep Dive (20 min)
Pick 2-3:
1. **Trie with top-K cache** — pre-computed suggestions at each node
2. **Periodic rebuild** — double-buffering with atomic pointer swap
3. **Scaling** — Redis caching for hot prefixes, Trie sharding
4. **Ranking** — frequency × recency × personalization

## Trade-offs (5 min)
- Trie vs Elasticsearch, real-time vs periodic updates
- Client-side: debounce, local cache, speculative prefetch

---

> **Differentiator**: Code the Trie with the top-K optimization.
> Also mention client-side optimizations (debounce, abort stale requests)
> — it shows you think about the full stack, not just the backend.
