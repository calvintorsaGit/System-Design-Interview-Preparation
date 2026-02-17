# 📋 Step 1: Clarify Requirements

> Are we building Twitter (text) or Instagram (media)?

---

## Requirements

### Functional
1.  **Post**: User can create a post (text/image).
2.  **Follow**: User can fallow other users.
3.  **Feed**: User sees posts from people they follow in reverse chronological order.

### Non-Functional
1.  **Fast Loading**: Feed must load in < 200ms.
2.  **Eventual Consistency**: It's okay if a post appears a few seconds late on a follower's feed.
3.  **Scale**: 100M Daily Active Users (DAU). Some users have millions of followers (celebrities).

---

*Next: [02 - High-Level Design →](./02-high-level-design.md)*
