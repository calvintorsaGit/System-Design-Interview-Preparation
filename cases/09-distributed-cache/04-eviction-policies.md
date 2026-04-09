# 🗑️ Step 4: Eviction Policies

> When memory is full, *which key do we delete?*

---

## Common Eviction Policies

| Policy | How It Works | Best For |
|--------|-------------|----------|
| **LRU** (Least Recently Used) | Delete the key not accessed for the longest time | General purpose ✅ |
| **LFU** (Least Frequently Used) | Delete the key accessed the fewest times | Hot/cold data distinction |
| **TTL** | Delete keys that have expired | Session data, temporary caches |
| **Random** | Delete a random key | When all keys have equal value |
| **noeviction** | Return error when full | When you can't afford to lose any key |

---

## LRU Implementation (Simplified)

Use a **HashMap + Doubly Linked List**:

```
Most Recent ←→ ... ←→ ... ←→ Least Recent
    HEAD                         TAIL
```

- **GET**: Move accessed key to HEAD → O(1)
- **SET**: Insert at HEAD. If full, evict TAIL → O(1)

```java
public class LRUCache<K, V> {

    private final int capacity;
    private final Map<K, Node<K, V>> map = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null); // sentinel
    private final Node<K, V> tail = new Node<>(null, null); // sentinel

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) return null;
        moveToHead(node);  // Mark as recently used
        return node.value;
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            if (map.size() >= capacity) {
                Node<K, V> evicted = tail.prev;  // Least recently used
                removeNode(evicted);
                map.remove(evicted.key);
            }
            Node<K, V> newNode = new Node<>(key, value);
            addToHead(newNode);
            map.put(key, newNode);
        }
    }
}
```

---

## Redis Approximated LRU

Redis doesn't track access order for ALL keys (too expensive). Instead:
1. Sample 5 random keys
2. Evict the one with the oldest "last accessed" timestamp
3. This approximation is ~95% as accurate as true LRU, with much less overhead

---

*Next: [05 — Replication & Failover →](./05-replication-and-failover.md)*
