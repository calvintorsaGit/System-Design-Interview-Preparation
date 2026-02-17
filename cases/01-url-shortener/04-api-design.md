# 🔌 Step 4: API Design

> Define your APIs early. This gives the interviewer concrete endpoints to discuss.

---

## REST API Endpoints

### 1. Create Short URL

```
POST /api/v1/urls
```

**Request Body:**
```json
{
  "longUrl": "https://www.example.com/very/long/path?query=value",
  "customAlias": "my-promo",       // optional
  "expiresAt": "2026-12-31T23:59:59Z"  // optional
}
```

**Response (201 Created):**
```json
{
  "shortUrl": "https://short.ly/abc123",
  "longUrl": "https://www.example.com/very/long/path?query=value",
  "shortCode": "abc123",
  "expiresAt": "2026-12-31T23:59:59Z",
  "createdAt": "2026-02-17T10:00:00Z"
}
```

**Error Cases:**
| Status | When |
|--------|------|
| `400 Bad Request` | Invalid URL format |
| `409 Conflict` | Custom alias already taken |
| `429 Too Many Requests` | Rate limit exceeded |

---

### 2. Redirect to Long URL

```
GET /{shortCode}
```

**Response (302 Found):**
```
HTTP/1.1 302 Found
Location: https://www.example.com/very/long/path?query=value
```

**Error Cases:**
| Status | When |
|--------|------|
| `404 Not Found` | Short code doesn't exist |
| `410 Gone` | URL has expired |

---

### 3. Get URL Info (Optional)

```
GET /api/v1/urls/{shortCode}
```

**Response (200 OK):**
```json
{
  "shortCode": "abc123",
  "longUrl": "https://www.example.com/very/long/path?query=value",
  "createdAt": "2026-02-17T10:00:00Z",
  "expiresAt": "2026-12-31T23:59:59Z",
  "clickCount": 1542
}
```

---

### 4. Delete URL (Optional)

```
DELETE /api/v1/urls/{shortCode}
```

**Response:** `204 No Content`

---

## Spring Boot Implementation Sketch

```java
@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // ── Create Short URL ──────────────────────────────────
    @PostMapping("/api/v1/urls")
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request) {

        UrlResponse response = urlService.createShortUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ── Redirect ──────────────────────────────────────────
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String longUrl = urlService.getLongUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)              // 302
                .location(URI.create(longUrl))
                .build();
    }

    // ── Get URL Info ──────────────────────────────────────
    @GetMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlInfo(
            @PathVariable String shortCode) {

        UrlResponse response = urlService.getUrlInfo(shortCode);
        return ResponseEntity.ok(response);
    }

    // ── Delete URL ────────────────────────────────────────
    @DeleteMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<Void> deleteUrl(
            @PathVariable String shortCode) {

        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}
```

### DTOs

```java
// ── Request DTO ───────────────────────────────────────────
public record CreateUrlRequest(
    @NotBlank @URL String longUrl,
    @Size(min = 4, max = 20) String customAlias,    // optional
    LocalDateTime expiresAt                           // optional
) {}

// ── Response DTO ──────────────────────────────────────────
public record UrlResponse(
    String shortUrl,
    String longUrl,
    String shortCode,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    Long clickCount
) {}
```

---

## API Design Decisions Worth Discussing

### Why REST and not gRPC?
- URL shortener is a **public-facing** service → REST is more accessible
- Browsers need HTTP redirects → gRPC wouldn't work for the redirect endpoint
- For an **internal** shortener microservice, gRPC would be a valid choice

### Why `/api/v1/` prefix?
- Versioning allows backward-compatible evolution
- Separates API routes from redirect routes (e.g., `/{shortCode}`)

### Why `POST` for creation instead of `PUT`?
- The server generates the resource ID (short code)
- `PUT` implies the client specifies the exact resource URI → better for custom aliases
- Convention: `POST` for server-generated IDs

---

## 🎤 Interview Tip

> Before drawing the architecture, write out the **2-3 core API endpoints** on
> the whiteboard. This anchors the conversation and shows you think about the
> user-facing contract first.
>
> *"Before diving into internals, let me define the API surface. We need two
> core endpoints: POST to create a short URL, and GET to redirect."*

---

*Next: [05 - Database Design →](./05-database-design.md)*
