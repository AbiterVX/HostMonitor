package com.hust.hostmonitor_data_collector.dao.entity;

public class DiskHardWareInfo {
    public String diskSerial;
    public String hostName;
    public double size;
    public boolean isSSd;
    public String model;
    public String hostIp;

    public DiskHardWareInfo(String diskSerial, String hostName, double size, boolean isSSd, String model, String hostIp) {
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.size = size;
        this.isSSd = isSSd;
        this.model = model;
        this.hostIp=hostIp;
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
