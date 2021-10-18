package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class TemperatureRecord {
    private Timestamp timestamp;
    private Double temperature;

    public TemperatureRecord(Timestamp timestamp, Double temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "TemperatureRecord{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                '}';
    }
}
