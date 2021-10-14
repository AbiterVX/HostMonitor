package com.hust.hostmonitor_client.utils.KylinEntity;

import java.util.ArrayList;

public class KylinPeriodRecord {
    private long memTotal;
    private long memFree;
    private long memAvailable;
    private long CPUtotal;
    private long CPUused;
    private long NetReceive;
    private long NetSend;
    private double CPUTemperature;
    private ArrayList<DiskInfo> disks;


    public KylinPeriodRecord() {
        disks=new ArrayList<>();
    }

    public KylinPeriodRecord(long memTotal, long memFree, long memAvailable, long CPUtotal, long CPUused, long netReceive, long netSend, double CPUTemperature, ArrayList<DiskInfo> disks) {
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

    public long getCPUtotal() {
        return CPUtotal;
    }

    public void setCPUtotal(long CPUtotal) {
        this.CPUtotal = CPUtotal;
    }

    public long getCPUused() {
        return CPUused;
    }

    public void setCPUused(long CPUused) {
        this.CPUused = CPUused;
    }

    public long getNetReceive() {
        return NetReceive;
    }

    public void setNetReceive(long netReceive) {
        NetReceive = netReceive;
    }

    public long getNetSend() {
        return NetSend;
    }

    public void setNetSend(long netSend) {
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
