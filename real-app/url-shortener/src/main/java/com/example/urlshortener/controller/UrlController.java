package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // 1. Create Short URL
    @PostMapping("/api/v1/shorten")
    public ResponseEntity<ShortenResponse> createShortUrl(@RequestBody @Valid ShortenRequest request) {
        String shortCode = urlService.shorten(request.getLongUrl(), request.getCustomAlias(), request.getExpiresAt());
        
        String shortUrl = "http://localhost:8080/" + shortCode;
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortenResponse(shortCode, shortUrl, request.getLongUrl()));
    }

    // 2. Redirect
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlService.resolve(shortCode);
        
        return ResponseEntity.status(HttpStatus.FOUND) // 302 Found
                .location(URI.create(longUrl))
                .build();
    }

    @Data
    public static class ShortenRequest {
        @NotBlank
        private String longUrl;
        private String customAlias;
        private LocalDateTime expiresAt;
    }

    @Data
    public static class ShortenResponse {
        private final String shortCode;
        private final String shortUrl;
        private final String longUrl;
    }
}
