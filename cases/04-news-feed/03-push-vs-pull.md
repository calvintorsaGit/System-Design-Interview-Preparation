# 🔄 Step 3: Push vs Pull (The Core Problem)

> How to handle Justin Bieber (100M followers)?

---

## 1. Push Model (Fan-out on Write)
- **Action**: When user posts, write to *every* follower's feed.
- **Pros**: Fast reads (pre-computed).
- **Cons**: **Slow writes** for celebrities. 100M writes takes time!

## 2. Pull Model (Fan-out on Read)
- **Action**: Do nothing on write. When follower opens app, fetch recent posts from everyone they follow and merge them.
- **Pros**: Instant write.
- **Cons**: **Slow reads**. Fetching 500 timelines and merging is heavy.

## 3. Hybrid Model (The Winner ✅)
- **Regular Users**: Use Push Model (fast read).
- **Celebrities**: Use Pull Model.
  - Don't push Bieber's post to 100M feeds.
  - When a user opens their feed, **merge** their pre-computed feed with Bieber's recent posts.

---

*Next: [04 - Pagination →](./04-pagination.md)*
