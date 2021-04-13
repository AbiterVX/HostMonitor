package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BWrecord3 {
    private BigDecimal receiveBW;
    private Timestamp timestamp;
    private float receiveBWf;

    public BWrecord3(BigDecimal BW, Timestamp timestamp) {
        this.receiveBW = BW;
        this.timestamp = timestamp;
        this.receiveBWf=BW.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getReceiveBWf() {
        return receiveBWf;
    }

    @Override
    public String toString() {
        return "BWrecord3{" +
                "timestamp=" + timestamp +
                ", receiveBW=" + receiveBWf +
                '}';
    }
}
