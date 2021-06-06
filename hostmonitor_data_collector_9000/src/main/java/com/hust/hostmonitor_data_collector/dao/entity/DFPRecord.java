package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class DFPRecord {
    public String hostName;
    public String diskSerial;
    public Timestamp timestamp;
    public Double predictProbability;
    public String modelName;

    public DFPRecord(String diskSerial,String hostName,  Timestamp timestamp, Double predictProbability, String modelName) {
        this.hostName = hostName;
        this.diskSerial = diskSerial;
        this.timestamp = timestamp;
        this.predictProbability = predictProbability;
        this.modelName = modelName;
    }
}
