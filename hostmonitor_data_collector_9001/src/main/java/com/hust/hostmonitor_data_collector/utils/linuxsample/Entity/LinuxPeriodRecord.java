package com.hust.hostmonitor_data_collector.utils.linuxsample.Entity;

import java.util.ArrayList;

public class LinuxPeriodRecord {
    private long memTotal;
    private long memFree;
    private long memAvailable;
    private ArrayList<Long> CPUtotal;
    private ArrayList<Long> CPUused;
    private Long AllCPUtotal;
    private Long AllCPUused;

    public Long getAllCPUtotal() {
        return AllCPUtotal;
    }

    public void setAllCPUtotal(Long allCPUtotal) {
        AllCPUtotal = allCPUtotal;
    }

    public Long getAllCPUused() {
        return AllCPUused;
    }

    public void setAllCPUused(Long allCPUused) {
        AllCPUused = allCPUused;
    }

    private double NetReceive;
    private double NetSend;
    private double CPUTemperature;
    private ArrayList<DiskInfo> disks;


    public LinuxPeriodRecord() {
        CPUtotal=new ArrayList<>();
        CPUused=new ArrayList<>();
        disks=new ArrayList<>();
    }

    public LinuxPeriodRecord(long memTotal, long memFree, long memAvailable, ArrayList<Long> CPUtotal, ArrayList<Long> CPUused, long netReceive, long netSend, double CPUTemperature, ArrayList<DiskInfo> disks) {
        this.memTotal = memTotal;
        this.memFree = memFree;
        this.memAvailable = memAvailable;
        this.CPUtotal = CPUtotal;
        this.CPUused = CPUused;
        NetReceive = netReceive;
        NetSend = netSend;
        this.CPUTemperature = CPUTemperature;
        this.disks = disks;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemFree() {
        return memFree;
    }

    public void setMemFree(long memFree) {
        this.memFree = memFree;
    }

    public long getMemAvailable() {
        return memAvailable;
    }

    public void setMemAvailable(long memAvailable) {
        this.memAvailable = memAvailable;
    }

    public ArrayList<Long> getCPUtotal() {
        return CPUtotal;
    }

    public void setCPUtotal(ArrayList<Long> CPUtotal) {
        this.CPUtotal = CPUtotal;
    }

    public ArrayList<Long> getCPUused() {
        return CPUused;
    }

    public void setCPUused(ArrayList<Long> CPUused) {
        this.CPUused = CPUused;
    }

    public double getNetReceive() {
        return NetReceive;
    }

    public void setNetReceive(double netReceive) {
        NetReceive = netReceive;
    }

    public double getNetSend() {
        return NetSend;
    }

    public void setNetSend(double netSend) {
        NetSend = netSend;
    }

    public double getCPUTemperature() {
        return CPUTemperature;
    }

    public void setCPUTemperature(double CPUTemperature) {
        this.CPUTemperature = CPUTemperature;
    }

    public ArrayList<DiskInfo> getDisks() {
        return disks;
    }

    public void setDisks(ArrayList<DiskInfo> disks) {
        this.disks = disks;
    }
}
