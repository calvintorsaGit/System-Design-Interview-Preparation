# 🎬 Step 4: Video Streaming & CDN

---

## Adaptive Bitrate Streaming (ABR)

The player automatically switches quality based on the viewer's bandwidth:

```
Viewer (fast WiFi)     → streams 1080p
Viewer (slow 3G)       → streams 360p
Viewer (speed changes) → seamlessly switches mid-playback
```

### How HLS (HTTP Live Streaming) Works

1. Video is split into **small segments** (2-10 seconds each)
2. Each segment exists in **multiple quality levels**
3. A **manifest file** (.m3u8) lists all available segments

```
video_123/
├── manifest.m3u8          ← Master playlist
├── 360p/
│   ├── playlist.m3u8      ← Segment list for 360p
│   ├── segment_001.ts
│   ├── segment_002.ts
│   └── ...
├── 720p/
│   ├── playlist.m3u8
│   ├── segment_001.ts
│   └── ...
└── 1080p/
    ├── playlist.m3u8
    ├── segment_001.ts
    └── ...
```

### Master Manifest Example
```
#EXTM3U
#EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360
360p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=2500000,RESOLUTION=1280x720
720p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
1080p/playlist.m3u8
```

The player picks the highest quality it can sustain without buffering.

---

## CDN (Content Delivery Network)

```
                           Origin (S3)
                               │
                    ┌──────────┼──────────┐
                    ▼          ▼          ▼
              ┌─────────┐ ┌─────────┐ ┌─────────┐
              │  Edge   │ │  Edge   │ │  Edge   │
              │  Tokyo  │ │ London  │ │  SFO    │
              └────┬────┘ └────┬────┘ └────┬────┘
                   │           │           │
              Viewer in    Viewer in   Viewer in
              Jakarta      Berlin      New York
```

### How It Works
1. Viewer requests `video_123/720p/segment_042.ts`
2. CDN edge checks local cache
3. **Cache hit** → serve immediately (< 50ms)
4. **Cache miss** → fetch from origin (S3), cache locally, then serve

### What to Put on CDN
| Content | CDN? | TTL |
|---------|------|-----|
| Video segments (.ts) | ✅ Yes | 1 year (immutable) |
| Thumbnails | ✅ Yes | 24 hours |
| HLS manifests | ✅ Yes | 1 hour (may update) |
| Metadata (API) | ❌ No | Dynamic content |

---

## 🎤 Interview Tip

> *"I'd use HLS for adaptive bitrate streaming — the video is split into 10-second
> segments at multiple quality levels. A CDN serves these segments from edge
> locations worldwide. Since segments are immutable, cache hit rates exceed 95%
> for popular videos."*

---

*Next: [05 — Search & Recommendations →](./05-search-and-recommendations.md)*
