package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class DFPRecord implements GeneralDFP{

    public String diskSerial;
    public String hostName;
    public Boolean isSSd;
    public Timestamp timestamp;
    public Double predictProbability;
    public String modelName;


    public DFPRecord(String diskSerial,String hostName,Boolean isSSd,Timestamp timestamp, Double predictProbability, String modelName) {
        this.hostName = hostName;
        this.diskSerial = diskSerial;
        this.isSSd=isSSd;
        this.timestamp = timestamp;
        this.predictProbability = predictProbability;
        this.modelName = modelName;
    }
}
