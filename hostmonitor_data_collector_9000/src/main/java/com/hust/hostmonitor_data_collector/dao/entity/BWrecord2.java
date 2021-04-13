package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BWrecord2 {
    private BigDecimal transmitBW;
    private Timestamp timestamp;
    private float transmitBWf;

    public BWrecord2(BigDecimal BW, Timestamp timestamp) {
        this.transmitBW = BW;
        this.timestamp = timestamp;
        this.transmitBWf=BW.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getTransmitBWf() {
        return transmitBWf;
    }

    @Override
    public String toString() {
        return "BWrecord2{" +
                "timestamp=" + timestamp +
                ", transmitBW=" + transmitBWf +
                '}';
    }
}
