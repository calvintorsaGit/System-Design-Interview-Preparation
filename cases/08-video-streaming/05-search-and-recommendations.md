# 🔍 Step 5: Search & Recommendations

---

## Video Search (Elasticsearch)

```java
// Index video metadata
@Document(indexName = "videos")
public class VideoDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Long)
    private long viewCount;

    @Field(type = FieldType.Date)
    private Instant uploadedAt;
}
```

### Multi-field Search with Boost

```json
{
  "query": {
    "bool": {
      "should": [
        { "match": { "title": { "query": "java tutorial", "boost": 3 } } },
        { "match": { "description": { "query": "java tutorial", "boost": 1 } } },
        { "match": { "tags": { "query": "java tutorial", "boost": 2 } } }
      ]
    }
  },
  "sort": [
    { "_score": "desc" },
    { "viewCount": "desc" }
  ]
}
```

---

## Recommendations (Simplified)

| Approach | How It Works | Pros | Cons |
|----------|-------------|------|------|
| **Content-based** | "You watched Java → suggest more Java" | Simple, no cold-start for items | Filter bubble |
| **Collaborative** | "Users like you also watched X" | Discovers unexpected content | Cold-start for new users |
| **Hybrid** | Combine both + trending | Best quality | Complex to build |

For an interview, explain the approach, don't implement ML:

```java
// Simplified: "watch next" based on tags + popularity
public List<Video> getRecommendations(String videoId, String userId) {
    Video current = videoService.findById(videoId);
    
    return elasticsearchClient.search(
        QueryBuilders.boolQuery()
            .should(termsQuery("tags", current.getTags()))
            .mustNot(termsQuery("id", watchHistory.getWatched(userId)))
            .boost("viewCount", 0.3),
        Sort.by("_score").descending()
    );
}
```

---

*Next: [06 — Trade-offs & Failures →](./06-tradeoffs-and-failures.md)*
