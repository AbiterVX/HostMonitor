package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DiskHardWareInfo {
    public String diskSerial;
    public String hostName;
    public double size;
    public boolean isSSd;
    public String model;

    public DiskHardWareInfo(String diskSerial, String hostName, double size, boolean isSSd, String model) {
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size;
        this.isSSd = isSSd;
        this.model = model;
    }




    @Override
    public String toString() {
        return "DiskHardWareInfo{" +
                "diskSerial='" + diskSerial + '\'' +
                ", hostName='" + hostName + '\'' +
                ", size=" + size +
                ", isSSd=" + isSSd +
                ", model='" + model + '\'' +
                '}';
    }
}
