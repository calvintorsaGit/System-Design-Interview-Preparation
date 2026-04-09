# 🌳 Step 3: Trie Data Structure

> The Trie (prefix tree) is THE data structure for autocomplete.

---

## What Is a Trie?

A tree where each path from root to node represents a **prefix**:

```
            (root)
           / | \
          h   w   b
          |   |   |
          e   o   a
         / \  |   |
        l   a r   n
        |   |  \   \
        l   d   l   k
        |       |
        o       d
        
  Stores: "hello", "head", "world", "bank"
```

### Key Properties
- **Shared prefixes** save space: "hello" and "head" share "he"
- **Prefix lookup**: O(L) where L = length of prefix
- Each node can store metadata: **frequency count**, top-K suggestions

---

## Trie with Top-K Cache at Each Node

The key optimization: **pre-compute top-5 at every node** so we don't traverse on query.

```
Node "he" → top5: ["hello world", "hello", "health", "help", "heart"]
Node "hel" → top5: ["hello world", "hello", "help center", "help", "helmet"]
```

### Java Implementation

```java
public class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    List<String> topSuggestions = new ArrayList<>(5);  // Pre-computed top 5
    boolean isEndOfWord;
    int frequency;
}

public class AutocompleteTrie {

    private final TrieNode root = new TrieNode();

    // Insert a query with its frequency
    public void insert(String query, int frequency) {
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            // Update top-5 at each node along the path
            updateTopK(node.topSuggestions, query, frequency);
        }
        node.isEndOfWord = true;
        node.frequency = frequency;
    }

    // Query: get suggestions for a prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }
        return node.topSuggestions;  // O(1) — already pre-computed!
    }

    private void updateTopK(List<String> topK, String query, int freq) {
        // Add/update and keep only top 5 by frequency
        topK.remove(query);
        topK.add(query);
        topK.sort((a, b) -> getFrequency(b) - getFrequency(a));
        if (topK.size() > 5) topK.remove(topK.size() - 1);
    }
}
```

---

## Time & Space Complexity

| Operation | Complexity |
|-----------|-----------|
| Insert | O(L) where L = query length |
| Search prefix | O(L) — then O(1) for cached top-K |
| Space | O(N × L) worst case, much less with shared prefixes |
| Rebuild from logs | O(N × L) for N queries |

---

## 🎤 Interview Tip

> *"I'd use a Trie with pre-computed top-5 suggestions at every node. This
> means the query 'hel' → O(3) to reach the node → O(1) to return pre-cached
> suggestions. The trade-off is higher memory usage and rebuild cost, but
> read latency is sub-millisecond."*

---

*Next: [04 — Ranking & Updates →](./04-ranking-and-updates.md)*
