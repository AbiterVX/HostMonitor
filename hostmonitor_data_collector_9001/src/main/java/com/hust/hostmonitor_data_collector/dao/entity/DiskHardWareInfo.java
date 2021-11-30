package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class DiskHardWareInfo {
    public String diskSerial;
    public String hostName;
    public double size;
    public boolean isSSd;
    public String model;
    public String hostIp;
    public boolean state;
    public Timestamp modifiedTimestamp;
    public DiskHardWareInfo(String diskSerial, String hostName, double size, boolean isSSd, String model, String hostIp,Boolean state,Timestamp modifiedTimestamp) {
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size;
        this.isSSd = isSSd;
        this.model = model;
        this.hostIp=hostIp;
        this.state=state;
        this.modifiedTimestamp=modifiedTimestamp;
    }
    public DiskHardWareInfo(String diskSerial, String hostName, double size, Byte isSSd, String model, String hostIp,Byte state,Timestamp modifiedTimestamp){
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size;
        this.isSSd =  (int)isSSd>0? true:false;;
        this.model = model;
        this.hostIp=hostIp;
        this.state= (int)state>0? true:false;;
        this.modifiedTimestamp=modifiedTimestamp;
    }
    public DiskHardWareInfo(String diskSerial, String hostName, Float size, Byte isSSd, String model, String hostIp,Byte state,Timestamp modifiedTimestamp){
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size.doubleValue();
        this.isSSd =  (int)isSSd>0? true:false;;
        this.model = model;
        this.hostIp=hostIp;
        this.state= (int)state>0? true:false;;
        this.modifiedTimestamp=modifiedTimestamp;
    }
}
