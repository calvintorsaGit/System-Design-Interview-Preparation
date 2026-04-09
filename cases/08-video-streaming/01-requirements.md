# 📋 Step 1: Clarify Requirements

---

## Questions to Ask

| Question | Typical Answer |
|----------|---------------|
| Upload or streaming or both? | Both |
| Max video size? | 10 GB |
| Supported resolutions? | 360p, 480p, 720p, 1080p, 4K |
| Live streaming? | Out of scope (focus: on-demand / VOD) |
| DAU? | 500M viewers, 5M uploaders |
| Geographic distribution? | Global (CDN needed) |

---

## ✅ Final Requirements

### Functional
1. **Upload video** — resumable, up to 10 GB
2. **Transcode** — convert to multiple resolutions and codecs
3. **Stream video** — adaptive bitrate (auto-adjust quality based on bandwidth)
4. **Search** — by title, description, tags
5. **Comments, likes, view count** — social features
6. **Recommendations** — personalized "watch next"

### Non-Functional
1. **Upload**: complete transcoding within 15 min for a 1-hour video
2. **Stream latency**: start playback < 2 seconds
3. **Availability**: 99.99%
4. **Scale**: 500M DAU, 1M concurrent streams
5. **Storage**: handle petabytes of video data

---

## Back-of-Envelope Estimation

```
Upload:
  5M uploaders × 1 video/week = ~700K videos/day
  Average video = 500 MB raw
  Daily upload = 700K × 500 MB = 350 TB/day of raw video

After transcoding (multiple resolutions):
  ~3x raw size = ~1 PB/day stored

Storage (1 year):
  365 PB raw... → Need S3-tier blob storage + lifecycle policies

Streaming:
  500M DAU × 5 videos/day = 2.5B video plays/day
  Peak: ~50K concurrent streams
```

---

*Next: [02 — High-Level Design →](./02-high-level-design.md)*
