package com.hust.hostmonitor_client.utils.Entity.KylinEntity;

public class KylinProcess {
    private int processID;
    private String name;
    private long startTime;
    private double cpuUsage;
    private double memoryUsage;
    private double diskReadSpeed;
    private double diskWriteSpeed;

    public KylinProcess(int processID, String name, long startTime, double cpuUsage, double memoryUsage, double diskReadSpeed, double diskWriteSpeed) {
        this.processID = processID;
        this.name = name;
        this.startTime = startTime;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskReadSpeed = diskReadSpeed;
        this.diskWriteSpeed = diskWriteSpeed;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getDiskReadSpeed() {
        return diskReadSpeed;
    }

    public void setDiskReadSpeed(double diskReadSpeed) {
        this.diskReadSpeed = diskReadSpeed;
    }

    public double getDiskWriteSpeed() {
        return diskWriteSpeed;
    }

    public void setDiskWriteSpeed(double diskWriteSpeed) {
        this.diskWriteSpeed = diskWriteSpeed;
    }
}
