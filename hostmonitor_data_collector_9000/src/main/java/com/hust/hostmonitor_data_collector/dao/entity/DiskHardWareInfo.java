package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DiskHardWareInfo {
    private String diskSerial;
    private String hostName;
    private double size;
    private boolean isSSd;
    private String model;

    public DiskHardWareInfo(String diskSerial, String hostName, double size, boolean isSSd, String model) {
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size;
        this.isSSd = isSSd;
        this.model = model;
    }


    public String getDiskSerial() {
        return diskSerial;
    }

    public String getHostName() {
        return hostName;
    }

    public double getSize() {
        return size;
    }

    public boolean isSSd() {
        return isSSd;
    }

    public String getModel() {
        return model;
    }

    public void setDiskSerial(String diskSerial) {
        this.diskSerial = diskSerial;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setSSd(boolean SSd) {
        isSSd = SSd;
    }

    public void setModel(String model) {
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
