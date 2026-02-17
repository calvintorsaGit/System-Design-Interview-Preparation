package com.example.urlshortener.service;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository repository;
    private final SnowflakeIdGenerator idGenerator;
    private final Base62Encoder encoder;
    private final StringRedisTemplate redis;

    private static final String CACHE_PREFIX = "short:";
    private static final String CLICK_PREFIX = "clicks:";

    @Transactional
    public String shorten(String longUrl, String customAlias, LocalDateTime expiresAt) {
        String shortCode;

        if (customAlias != null && !customAlias.isEmpty()) {
            if (repository.existsByShortCode(customAlias)) {
                throw new IllegalArgumentException("Alias '" + customAlias + "' is already taken.");
            }
            shortCode = customAlias;
        } else {
            long id = idGenerator.nextId();
            shortCode = encoder.encode(id);
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setLongUrl(longUrl);
        mapping.setExpiresAt(expiresAt);

        repository.save(mapping);

        // Pre-warm cache (write-through)
        cacheUrl(shortCode, longUrl);

        return shortCode;
    }

    public String resolve(String shortCode) {
        // 1. Check Cache
        String cachedUrl = redis.opsForValue().get(CACHE_PREFIX + shortCode);
        if (cachedUrl != null) {
            recordClickAsync(shortCode);
            return cachedUrl;
        }

        // 2. Main Logic: Check DB
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL Not Found"));

        if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(mapping);
            throw new RuntimeException("URL Expired");
        }

        // 3. Cache Miss: Update Cache
        cacheUrl(shortCode, mapping.getLongUrl());

        recordClickAsync(shortCode);
        
        return mapping.getLongUrl();
    }

    private void cacheUrl(String shortCode, String longUrl) {
        redis.opsForValue().set(CACHE_PREFIX + shortCode, longUrl, Duration.ofHours(24));
    }

    // --- Distributed Click Counting ---

    private void recordClickAsync(String shortCode) {
        // Increment in Redis immediately (fast)
        redis.opsForValue().increment(CLICK_PREFIX + shortCode);
    }

    // Flush clicks from Redis to DB every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void flushClicks() {
        Set<String> keys = redis.keys(CLICK_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String shortCode = key.substring(CLICK_PREFIX.length());
            String count = redis.opsForValue().getAndDelete(key);

            if (count != null) {
                long clicks = Long.parseLong(count);
                try {
                    repository.incrementClickCount(shortCode, clicks);
                } catch (Exception e) {
                    log.error("Failed to update clicks for {}", shortCode, e);
                    // In real porduction, you might push back to Redis to retry
                }
            }
        }
    }
}
