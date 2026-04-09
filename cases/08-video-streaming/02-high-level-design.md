# 🏛️ Step 2: High-Level Design

> YouTube has two major pipelines: **upload** (write) and **streaming** (read).

---

## Architecture Diagram

```
                    UPLOAD PIPELINE                          STREAMING PIPELINE
                    ──────────────                          ──────────────────

┌──────────┐                                          ┌──────────┐
│ Creator  │                                          │  Viewer  │
│  Client  │                                          │  Client  │
└────┬─────┘                                          └────┬─────┘
     │                                                     │
     │ 1. Upload (chunked)                                │ 6. Stream video
     ▼                                                     ▼
┌──────────┐                                          ┌──────────┐
│ Upload   │                                          │   CDN    │
│ Service  │                                          │ (Edge)   │
└────┬─────┘                                          └────┬─────┘
     │                                                     │ cache miss
     │ 2. Store raw video                                  ▼
     ▼                                                ┌──────────┐
┌──────────┐                                          │  Origin  │
│   S3     │                                          │  Server  │
│ (Raw)    │                                          │ (S3 xcd) │
└────┬─────┘                                          └──────────┘
     │
     │ 3. Trigger transcoding
     ▼
┌────────────────────────────┐
│   Message Queue (SQS/Kafka)│
└────────────┬───────────────┘
             │
    ┌────────┼────────┐
    ▼        ▼        ▼
┌──────┐ ┌──────┐ ┌──────┐
│Trans │ │Trans │ │Trans │    4. Transcode to 360p, 720p, 1080p, 4K
│coder │ │coder │ │coder │       Generate thumbnails
│  #1  │ │  #2  │ │  #3  │
└──┬───┘ └──┬───┘ └──┬───┘
   └────────┼────────┘
            ▼
     ┌──────────┐
     │   S3     │     5. Store transcoded videos
     │(Transcod)│
     └──────────┘
```

---

## Key Services

| Service | Role |
|---------|------|
| **Upload Service** | Handle chunked/resumable upload, store raw to S3 |
| **Transcoding Service** | Convert video to multiple resolutions + codecs |
| **Metadata Service** | Title, description, tags, view count (PostgreSQL) |
| **Search Service** | Full-text search on metadata (Elasticsearch) |
| **CDN** | Serve video from edge locations (CloudFront, Akamai) |
| **Recommendation Service** | "Watch next" suggestions (collaborative filtering) |

---

## 🎤 Interview Tip

> *"The system is fundamentally split into an async upload pipeline and a
> read-heavy streaming pipeline. Uploads go through S3 → message queue →
> transcoding workers. Streaming is served entirely from CDN edge servers.
> These two pipelines scale independently."*

---

*Next: [03 — Video Upload Pipeline →](./03-video-upload-pipeline.md)*
