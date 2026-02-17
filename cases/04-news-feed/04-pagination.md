# 📄 Step 4: Pagination (Cursor vs Offset)

> Why "Page 2" is tricky in feeds.

---

## The Problem with Offset Pagination
`SELECT * FROM posts LIMIT 10 OFFSET 10`

- If a new post arrives while user is on Page 1...
- When they go to Page 2, **the last item of Page 1 shifts to Page 2**.
- Result: **Duplicate posts** seen by user.

## The Solution: Cursor-Based Pagination
`SELECT * FROM posts WHERE id < :last_seen_id LIMIT 10`

- We track the ID of the last post loaded.
- We ask for "10 posts OLDER than ID X".
- **Pros**:
  - No duplicates (stable).
  - Faster (indexing on ID is efficient).
  - Works well with infinite scroll.

---

*Next: [05 - Interview Script →](./05-interview-script.md)*
