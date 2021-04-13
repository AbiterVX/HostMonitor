package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DiskUsageRecord {
    private Timestamp timestamp;
    private BigDecimal diskUsage;
    private float diskUsagef;

    public DiskUsageRecord(BigDecimal diskUsage,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.diskUsage = diskUsage;
        this.diskUsagef=diskUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getDiskUsagef() {
        return diskUsagef;
    }

    @Override
    public String toString() {
        return "diskUsageRecord{" +
                "timestamp=" + timestamp +
                ", diskUsagef=" + diskUsagef +
                '}';
    }
}
