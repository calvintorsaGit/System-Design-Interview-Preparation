# 📤 Step 3: Video Upload & Transcoding Pipeline

---

## Chunked / Resumable Upload

Large videos (1-10 GB) can't be uploaded in a single HTTP request. Use **chunked upload**:

```
Client                         Upload Service              S3
  │                                 │                       │
  │ 1. POST /upload/init            │                       │
  │    { filename, size, type }     │                       │
  │ ──────────────────────────────► │                       │
  │                                 │  Create multipart     │
  │  { uploadId, presignedUrls[] } │  upload in S3         │
  │ ◄────────────────────────────── │ ─────────────────►   │
  │                                 │                       │
  │ 2. PUT chunk 1 → presignedUrl  │                       │
  │ ─────────────────────────────────────────────────────► │
  │ 3. PUT chunk 2 → presignedUrl  │                       │
  │ ─────────────────────────────────────────────────────► │
  │    ... (network fails)         │                       │
  │ 4. PUT chunk 2 (retry!)       │                       │
  │ ─────────────────────────────────────────────────────► │
  │                                 │                       │
  │ 5. POST /upload/complete       │                       │
  │ ──────────────────────────────► │  Complete multipart  │
  │                                 │ ─────────────────►   │
```

### Key Benefits
- **Resumable**: If upload breaks at chunk 47/100, resume from 47
- **Parallel**: Upload multiple chunks simultaneously
- **Pre-signed URLs**: Client uploads directly to S3, bypassing backend

---

## Transcoding Pipeline

After raw video is in S3, we transcode it into multiple formats:

```
Raw video (1080p, 2GB, H.264)
           │
    ┌──────┼──────┬──────┐
    ▼      ▼      ▼      ▼
  360p   480p   720p   1080p     ← Resolution variants
  H.264  H.264  H.264  H.264    ← Codec
  150MB  300MB  800MB  2GB       ← Sizes

  + Generate thumbnail (at 25% mark)
  + Generate HLS manifest (.m3u8)
  + Extract subtitles (if embedded)
```

### Transcoding Worker

```java
@Service
public class TranscodingWorker {

    @KafkaListener(topics = "video-uploaded")
    public void onVideoUploaded(VideoUploadedEvent event) {
        String rawS3Key = event.getRawVideoKey();

        for (Resolution res : Resolution.values()) {
            // FFmpeg transcoding
            String outputKey = transcode(rawS3Key, res);
            s3Client.putObject(outputKey, transcodedFile);
        }

        // Generate HLS manifest
        String manifestKey = generateHlsManifest(event.getVideoId());

        // Update metadata: status = READY
        metadataService.markReady(event.getVideoId(), manifestKey);
    }
}
```

### Video Processing States

```
UPLOADING → UPLOADED → TRANSCODING → READY → PUBLISHED
                                 │
                                 └─ FAILED (retry up to 3 times)
```

---

## 🎤 Interview Tip

> *"Uploads go directly from the client to S3 via pre-signed URLs — the backend
> never touches the raw bytes. Once uploaded, a Kafka event triggers parallel
> transcoding workers that produce multiple resolution variants + an HLS manifest."*

---

*Next: [04 — Video Streaming →](./04-video-streaming.md)*
