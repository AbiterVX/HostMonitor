package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class RealDiskFailure {
    public Timestamp timestamp;
    public String diskSerial;
    public String diskModel;
    public boolean isSSD;

    public RealDiskFailure(Timestamp timestamp, String diskSerial, String diskModel, boolean isSSD) {
        this.timestamp = timestamp;
        this.diskSerial = diskSerial;
        this.diskModel=diskModel;
        this.isSSD=isSSD;
    }
}
