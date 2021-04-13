package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class IOrecord {
    private Integer iNumber;
    private Integer oNumber;
    private Timestamp timestamp;

    public IOrecord(Integer iNumber, Integer oNumber, Timestamp timestamp) {
        this.iNumber = iNumber;
        this.oNumber = oNumber;
        this.timestamp = timestamp;
    }

    public Integer getiNumber() {
        return iNumber;
    }

    public Integer getoNumber() {
        return oNumber;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "IOrecord{" +
                "iNumber=" + iNumber +
                ", oNumber=" + oNumber +
                ", timestamp=" + timestamp +
                '}';
    }
}
