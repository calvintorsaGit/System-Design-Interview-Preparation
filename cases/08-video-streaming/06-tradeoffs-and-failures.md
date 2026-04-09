# ⚠️ Step 6: Trade-offs & Failure Scenarios

---

## Key Trade-offs

| Decision | Option A | Option B | Our Choice |
|----------|----------|----------|------------|
| Upload | Single HTTP POST | Chunked/resumable | **Chunked** — handles large files, resumable |
| Streaming protocol | Progressive download | HLS/DASH (ABR) | **HLS** — adaptive quality, industry standard |
| Storage | Self-hosted | S3/GCS | **S3** — petabyte scale, lifecycle policies |
| Video serving | Origin server | CDN | **CDN** — global, 95%+ cache hit for popular content |
| Search | PostgreSQL LIKE | Elasticsearch | **Elasticsearch** — full-text, relevance scoring |

---

## Failure Scenarios

| What Breaks | Impact | Mitigation |
|-------------|--------|------------|
| Transcoding worker crash | Video stuck in "processing" | Message stays in Kafka; another worker picks it up |
| S3 unavailable | Can't upload or stream | S3 has 99.999999999% durability; multi-region replication |
| CDN edge down | Viewers in region experience latency | CDN auto-routes to next nearest edge |
| Elasticsearch down | Search broken | Fallback to PostgreSQL title search (degraded) |
| Viral video (10M views/min) | CDN cache overwhelmed | Pre-warm CDN for trending videos; origin scaling |

---

## 🎤 Interview Script (Quick)

> *"I'd split the system into two independent pipelines: an async upload pipeline
> (S3 → Kafka → transcoding workers) and a streaming pipeline (CDN → origin S3).
> Video is served via HLS for adaptive bitrate, and search uses Elasticsearch
> with relevance boosting on title, tags, and view count."*

---

*Next: [07 — Interview Script →](./07-interview-script.md)*
