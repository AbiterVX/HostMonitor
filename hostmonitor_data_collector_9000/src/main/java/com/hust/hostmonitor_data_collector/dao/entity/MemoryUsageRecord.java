package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MemoryUsageRecord {
    private Timestamp timestamp;
    private BigDecimal memoryUsage;
    private float memoryUsagef;

    public MemoryUsageRecord(BigDecimal memoryUsage,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.memoryUsage = memoryUsage;
        this.memoryUsagef=memoryUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getMemoryUsagef() {
        return memoryUsagef;
    }

    @Override
    public String toString() {
        return "memoryUsageRecord{" +
                "timestamp=" + timestamp +
                ", memoryUsagef=" + memoryUsagef +
                '}';
    }
}
