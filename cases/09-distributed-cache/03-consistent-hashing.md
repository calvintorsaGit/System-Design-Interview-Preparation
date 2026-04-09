# 🔄 Step 3: Consistent Hashing

> The most important algorithm for distributed caches. Without it, adding/removing
> a node invalidates almost all cached data.

---

## The Problem with Simple Hashing

```
node = hash(key) % N   (where N = number of nodes)
```

When you go from 3 nodes to 4:
- `hash("user:123") % 3 = 1` → Node B
- `hash("user:123") % 4 = 3` → Node D  ← **MOVED!**

**~75% of keys remap** when adding 1 node → massive cache miss storm.

---

## Consistent Hashing Solution

Arrange nodes on a **virtual ring** (0 to 2^32):

```
           0
         ╱   ╲
       ╱       ╲
     A           B         Nodes placed at hash positions
     │           │
     │     ●     │         ← Key lands here, walks clockwise
     │   (key)   │            → assigned to Node B
       ╲       ╱
         ╲   ╱
           C
```

### How It Works
1. Hash each node name to a position on the ring
2. Hash each key to a position on the ring
3. Walk clockwise from the key's position → first node you hit owns that key

### When Adding a Node
Only keys between the new node and its predecessor are remapped:
```
Before: A, B, C → ~33% keys each
Add D between A and B:
  → Only keys between A and D move from B to D
  → ~25% of keys remapped (instead of 75%)
```

---

## Virtual Nodes (Vnodes)

**Problem**: With 3 physical nodes, distribution may be uneven.

**Solution**: Each physical node maps to 100-200 **virtual nodes** on the ring:

```
Physical Node A → vnode_A_001, vnode_A_002, ..., vnode_A_150
Physical Node B → vnode_B_001, vnode_B_002, ..., vnode_B_150
Physical Node C → vnode_C_001, vnode_C_002, ..., vnode_C_150
```

More points on the ring → more even distribution.

---

## Java Implementation (Simplified)

```java
public class ConsistentHashRing<T> {

    private final TreeMap<Long, T> ring = new TreeMap<>();
    private final int virtualNodes;
    private final MessageDigest md = MessageDigest.getInstance("MD5");

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.toString() + "#" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.toString() + "#" + i);
            ring.remove(hash);
        }
    }

    public T getNode(String key) {
        if (ring.isEmpty()) throw new IllegalStateException("No nodes");
        long hash = hash(key);
        // Find the first node clockwise from this hash
        Map.Entry<Long, T> entry = ring.ceilingEntry(hash);
        return (entry != null) ? entry.getValue() : ring.firstEntry().getValue();
    }

    private long hash(String input) {
        md.reset();
        byte[] digest = md.digest(input.getBytes());
        return ((long)(digest[0] & 0xFF) << 24) |
               ((long)(digest[1] & 0xFF) << 16) |
               ((long)(digest[2] & 0xFF) << 8)  |
               (digest[3] & 0xFF);
    }
}
```

---

## 🎤 Interview Tip

> *"Simple modular hashing causes a cache stampede when adding/removing nodes
> because ~N-1/N keys remap. Consistent hashing with virtual nodes ensures
> only K/N keys move (where K is total keys and N is number of nodes).
> This is how Redis Cluster, Cassandra, and DynamoDB distribute data."*

---

*Next: [04 — Eviction Policies →](./04-eviction-policies.md)*
