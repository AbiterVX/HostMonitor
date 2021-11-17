package com.hust.hostmonitor_data_collector.utils.linuxsample.Entity;

import org.ehcache.xml.model.Disk;

public class DiskInfo implements Comparable<DiskInfo>{
    public String diskName;
    public double diskReadSpeed;
    public double diskWriteSpeed;
    public double diskUsedRadio;
    public long diskFSUsageAmount;
    public double diskIOPS;

    @Override
    public int compareTo(DiskInfo o) {
        return this.diskName.compareTo(o.diskName);
    }
}