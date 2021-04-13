package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TemperatureRecord {
    private Timestamp timestamp;
    private BigDecimal temp;
    private float tempf;

    public TemperatureRecord(BigDecimal temp,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.temp = temp;
        this.tempf=temp.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getTempf() {
        return tempf;
    }

    @Override
    public String toString() {
        return "TemperatureRecord{" +
                "timestamp=" + timestamp +
                ", tempf=" + tempf +
                '}';
    }
}
