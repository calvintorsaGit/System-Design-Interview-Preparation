package com.example.urlshortener.service;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String encode(long value) {
        if (value == 0) return String.valueOf(BASE62_CHARS.charAt(0));

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62_CHARS.charAt((int)(value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}
