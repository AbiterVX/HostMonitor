package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class IOrecord3 {
    private Integer oNumber;
    private Timestamp timestamp;

    public IOrecord3(Integer number, Timestamp timestamp) {
        this.oNumber = number;
        this.timestamp = timestamp;
    }

    public Integer getoNumber() {
        return oNumber;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "IOrecord3{" +
                "number=" + oNumber +
                ", timestamp=" + timestamp +
                '}';
    }
}