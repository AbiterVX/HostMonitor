package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class IOrecord2 {
    private Integer iNumber;
    private Timestamp timestamp;

    public IOrecord2(Integer number, Timestamp timestamp) {
        this.iNumber = number;
        this.timestamp = timestamp;
    }

    public Integer getiNumber() {
        return iNumber;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "IOrecord2{" +
                "number=" + iNumber +
                ", timestamp=" + timestamp +
                '}';
    }
}
