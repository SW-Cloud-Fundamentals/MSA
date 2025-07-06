package com.example.articleservice.Model;

public final class Snowflake {
    private static final long EPOCH = 1700000000000L;  // 2023-11-15
    private static final long NODE_ID = 1L;

    private static long sequence = 0L;
    private static long lastTs  = -1L;

    public static synchronized long nextId() {
        long ts = System.currentTimeMillis();
        if (ts == lastTs) {
            sequence = (sequence + 1) & 0x3FF;         // 10-bit seq
            if (sequence == 0) while (ts <= lastTs) ts = System.currentTimeMillis();
        } else sequence = 0L;
        lastTs = ts;
        return ((ts - EPOCH) << 22) | (NODE_ID << 12) | sequence;
    }
}