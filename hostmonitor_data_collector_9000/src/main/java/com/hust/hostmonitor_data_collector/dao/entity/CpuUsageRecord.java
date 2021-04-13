package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CpuUsageRecord {
    private Timestamp timestamp;
    private BigDecimal cpuUsage;
    private float cpuUsagef;

    public CpuUsageRecord( BigDecimal cpuUsage,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.cpuUsage = cpuUsage;
        this.cpuUsagef=cpuUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getCpuUsagef() {
        return cpuUsagef;
    }

    @Override
    public String toString() {
        return "cpuUsageRecord{" +
                "timestamp=" + timestamp +
                ", cpuUsagef=" + cpuUsagef +
                '}';
    }
}
