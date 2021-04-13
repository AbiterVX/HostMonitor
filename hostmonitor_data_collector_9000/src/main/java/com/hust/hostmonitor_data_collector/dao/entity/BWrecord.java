package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BWrecord {
    private BigDecimal receiveBW;
    private BigDecimal transmitBW;
    private Timestamp  timestamp;
    private float receiveBWf;
    private float transmitBWf;

    public BWrecord(BigDecimal receiveBW, BigDecimal transmitBW, Timestamp timestamp) {
        this.receiveBW = receiveBW;
        this.transmitBW = transmitBW;
        this.timestamp = timestamp;
        receiveBWf=receiveBW.floatValue();
        transmitBWf=transmitBW.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getReceiveBWf() {
        return receiveBWf;
    }

    public float getTransmitBWf() {
        return transmitBWf;
    }

    @Override
    public String toString() {
        return "BWrecord{" +
                "timestamp=" + timestamp +
                ", receiveBWf=" + receiveBWf +
                ", transmitBWf=" + transmitBWf +
                '}';
    }
}
