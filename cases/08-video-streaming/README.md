# Case 8: Video Streaming System (like YouTube)

> Blob storage, transcoding pipelines, CDN, and adaptive bitrate — the ultimate media infrastructure question.

---

## 📁 Files in This Case Study

| File | What You'll Learn |
|------|------------------|
| [01-requirements.md](./01-requirements.md) | Upload, stream, search, recommendations |
| [02-high-level-design.md](./02-high-level-design.md) | Upload pipeline, streaming pipeline, search |
| [03-video-upload-pipeline.md](./03-video-upload-pipeline.md) | Chunked upload, transcoding, thumbnail generation |
| [04-video-streaming.md](./04-video-streaming.md) | CDN, adaptive bitrate (HLS/DASH), edge caching |
| [05-search-and-recommendations.md](./05-search-and-recommendations.md) | Elasticsearch for search, collaborative filtering |
| [06-tradeoffs-and-failures.md](./06-tradeoffs-and-failures.md) | Transcoding failures, CDN cache invalidation |
| [07-interview-script.md](./07-interview-script.md) | How to present this |

---

## 🎯 Key Concepts

- ✅ **Blob Storage** — S3 for raw/transcoded video
- ✅ **Transcoding Pipeline** — FFmpeg, resolution + codec conversion
- ✅ **CDN** — edge caching for low-latency streaming
- ✅ **Adaptive Bitrate Streaming** — HLS/DASH protocols
- ✅ **Chunked Upload** — resumable uploads for large files
- ✅ **Search Indexing** — Elasticsearch for video metadata

---

## ⏱️ Estimated Study Time: 2-3 hours

Start with `01-requirements.md` and work through in order.
