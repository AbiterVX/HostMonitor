package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class HardWithDFPRecord {
    public String diskSerial;
    public Timestamp timestamp;
    public Double predictProbability;
    public String modelName;
    public String hostName;
    public String hostIp;
    public Double size;
    public Boolean isSSd;
    public String model;

    public HardWithDFPRecord(String diskSerial, Timestamp timestamp, Double predictProbability, String modelName, String hostName,String hostIp, Double size, Boolean isSSd, String model) {
        this.diskSerial = diskSerial;
        this.timestamp = timestamp;
        this.predictProbability = predictProbability;
        this.modelName = modelName;
        this.hostName = hostName;
        this.hostIp=hostIp;
        this.size = size;
        this.isSSd = isSSd;
        this.model = model;
    }
}
