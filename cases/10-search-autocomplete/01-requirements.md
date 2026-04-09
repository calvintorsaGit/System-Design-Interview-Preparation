# 📋 Step 1: Clarify Requirements

---

## Questions to Ask

| Question | Typical Answer |
|----------|---------------|
| Autocomplete only, or full search results? | Autocomplete (suggest query strings) |
| How many suggestions per keystroke? | Top 5-10 |
| Ranking criteria? | Frequency + recency + personalization |
| Multi-language? | English only for scope |
| How fast must suggestions appear? | < 100ms from keystroke |
| How often do trends change? | Real-time (trending searches) |

---

## ✅ Final Requirements

### Functional
1. **Prefix matching** — User types "hel" → suggest "hello world", "help center", "hello kitty"
2. **Top-K suggestions** — Return top 5-10 by popularity
3. **Real-time trends** — New popular queries appear within minutes
4. **Filtering** — Remove offensive/inappropriate suggestions
5. **Personalization** (optional) — Boost user's recent searches

### Non-Functional
1. **Latency**: < 100ms per keystroke
2. **Scale**: 10B queries/day → 120K QPS
3. **Availability**: 99.99%
4. **Storage**: ~1B unique query strings
5. **Update frequency**: trends reflected within 5-15 min

---

## Estimation

```
Queries: 10B/day → ~120K QPS (peak ~240K QPS)
Unique queries: ~1B strings, avg 20 chars = 20 GB of text

But with Trie structure:
  - Shared prefixes reduce storage dramatically
  - ~5 GB for 1B queries in a compact Trie

Per-keystroke calls (avg 5 chars typed before clicking):
  120K QPS × 5 keystrokes = 600K autocomplete requests/sec (!!!)
  → Heavy caching needed
```

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
