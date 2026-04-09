# 🎤 Step 7: Interview Script

---

## Opening (2 min)
> *"I'll design a YouTube-like video streaming platform. The key challenges are:
> handling petabytes of video storage, an asynchronous transcoding pipeline,
> and serving millions of concurrent streams via CDN with adaptive bitrate."*

## Requirements (3 min)
- Upload + stream, 500M DAU, global distribution, search, recommendations
- 700K videos uploaded/day, 350 TB/day raw storage

## High-Level Design (10 min)
- Draw two pipelines:
  - Upload: Client → S3 (pre-signed) → Kafka → Transcoding → S3 (output)
  - Stream: Client → CDN → Origin (S3)
- Metadata: PostgreSQL + Elasticsearch for search

## Deep Dive (20 min)
Pick 2-3:
1. **Chunked upload** — pre-signed URLs, resumability
2. **Transcoding pipeline** — Kafka-driven workers, multiple resolutions
3. **HLS streaming** — segments, manifests, adaptive bitrate
4. **CDN architecture** — edge caching, cache warming

## Trade-offs (5 min)
- HLS vs DASH, S3 costs, CDN cache strategies
- Transcoding cost optimization (only transcode to resolutions people actually watch)

---

> **Differentiator**: Draw the HLS manifest structure and explain how the player
> switches quality mid-stream. Most candidates only know "use a CDN" at a high level.
