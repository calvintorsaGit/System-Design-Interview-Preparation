package com.example.urlshortener.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeIdGenerator {

    private final long machineId;
    private final long epoch = 1704067200000L; // Jan 1, 2024
    private long sequence = 0;
    private long lastTimestamp = -1;

    public SnowflakeIdGenerator(@Value("${app.machine-id:1}") long machineId) {
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12 bits sequence
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22)
             | (machineId << 12)
             | sequence;
    }
}
