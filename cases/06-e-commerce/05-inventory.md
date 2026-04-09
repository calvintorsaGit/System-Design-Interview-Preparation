# 📦 Step 5: Inventory Management

> The #1 rule: **never sell more than you have**. This is a concurrency problem.

---

## The Overselling Problem

```
Initial stock: 1 iPhone left

Thread A (User Alice):                Thread B (User Bob):
  SELECT stock FROM products          SELECT stock FROM products
  WHERE id = 'iphone'                 WHERE id = 'iphone'
  → stock = 1 ✅                       → stock = 1 ✅

  UPDATE stock = 0                    UPDATE stock = 0
  INSERT INTO orders...               INSERT INTO orders...

  ❌ Both users "bought" the last iPhone!
```

---

## Solution 1: Pessimistic Locking (SELECT FOR UPDATE)

```sql
BEGIN;
  SELECT stock FROM products WHERE id = 'iphone' FOR UPDATE;  -- Locks the row
  -- Thread B will WAIT here until Thread A commits
  UPDATE products SET stock = stock - 1 WHERE id = 'iphone';
  INSERT INTO orders (product_id, ...) VALUES ('iphone', ...);
COMMIT;
```

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") String id);
}
```

| Pros | Cons |
|------|------|
| Guaranteed correctness | Blocks concurrent requests (slow) |
| Simple to implement | Deadlock risk with multiple locks |

---

## Solution 2: Optimistic Locking (Version Column) ✅

```java
@Entity
public class Product {
    @Id
    private String id;
    private int stock;

    @Version
    private int version;  // Auto-incremented by JPA on update
}
```

```java
// If two threads read version=5 and both try to update,
// only one succeeds. The other gets OptimisticLockException → retry.
public void decrementStock(String productId) {
    int retries = 3;
    while (retries-- > 0) {
        try {
            Product p = productRepo.findById(productId).orElseThrow();
            if (p.getStock() <= 0) throw new OutOfStockException();
            p.setStock(p.getStock() - 1);
            productRepo.save(p);  // Will fail if version changed
            return;
        } catch (OptimisticLockException e) {
            // Retry — someone else updated it first
        }
    }
    throw new ConcurrencyException("Too many concurrent updates");
}
```

| Pros | Cons |
|------|------|
| No blocking — high throughput | Retries under high contention |
| Works well for normal traffic | Flash sales = lots of retries |

---

## Solution 3: Redis Atomic Decrement (For Flash Sales) ✅

```java
public boolean tryReserveStock(String productId) {
    Long remaining = redisTemplate.opsForValue()
        .decrement("stock:" + productId);

    if (remaining < 0) {
        // Oversold — roll back
        redisTemplate.opsForValue().increment("stock:" + productId);
        return false;
    }
    return true;
}
```

| Pros | Cons |
|------|------|
| Extremely fast (single-threaded Redis) | Redis data is volatile — need DB sync |
| Handles 100K+ ops/sec | Must pre-load stock into Redis |

---

## Inventory State Machine

```
  AVAILABLE ──(reserve)──► RESERVED ──(confirm)──► SOLD
       ▲                      │
       └───(release/timeout)──┘
```

- **Reserve**: Hold stock during checkout (TTL = 15 min)
- **Confirm**: Payment succeeded → mark as sold
- **Release**: Payment failed or timeout → return to available

---

## 🎤 Interview Tip

> *"For normal traffic, I'd use optimistic locking. For flash sales with 100K
> concurrent users, I'd pre-load stock counts into Redis and use atomic DECR —
> it's single-threaded, so no race conditions. The DB is synced asynchronously."*

---

*Next: [06 — Trade-offs & Failures →](./06-tradeoffs-and-failures.md)*
