package com.hust.hostmonitor_data_collector.dao.dmMapper;

import java.sql.Timestamp;

public class DMHardWithDFPRecord {
    public String diskSerial;
    public Timestamp timestamp;
    public Double predictProbability;
    public String modelName;
    public String hostName;
    public String hostIp;
    public Double size;
    public Byte isSSdByte;
    public Boolean isSSd;
    public String model;

    public DMHardWithDFPRecord(String diskSerial, Timestamp timestamp, Double predictProbability, String modelName, String hostName, String hostIp, Double size, Byte isSSdByte, String model) {
        this.diskSerial = diskSerial;
        this.timestamp = timestamp;
        this.predictProbability = predictProbability;
        this.modelName = modelName;
        this.hostName = hostName;
        this.hostIp=hostIp;
        this.size = size;
        this.isSSdByte=isSSdByte;
        this.isSSd = (int)this.isSSdByte>0? true:false;
        this.model = model;
    }
}
