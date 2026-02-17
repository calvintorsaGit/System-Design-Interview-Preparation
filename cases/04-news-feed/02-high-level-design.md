# 🏛️ Step 2: High-Level Design

> Two main flows: **Publishing a Post** and **Reading the Feed**.

---

## The Components

1.  **Post Service**: Stores the post content in DB / S3.
2.  **Graph Service**: Stores "who follows whom" (User Graph).
3.  **Fan-out Service**: Pushes post IDs to followers' feed cache.
4.  **Feed Service**: Retrieves the feed from cache.

## The Push Model (Fan-out on Write) **Recommended for Most Users**

When User A posts:
1.  Save post to DB.
2.  Find all followers of User A (Graph Service).
3.  **Push** the Post ID to every follower's "Home Timeline Cache" (Redis List).

When User B (follower) opens app:
1.  **Pull** from their own pre-computed Redis List.
2.  Get content details for those IDs.
3.  **Speed**: Extremely fast (O(1) read).

---

*Next: [03 - Push vs Pull →](./03-push-vs-pull.md)*
