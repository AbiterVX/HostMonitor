package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class DispersedRecord {
    private String hostname;
    private String ip;
    private Timestamp timestamp;
    private Double MemUsage;
    private Double CpuUsage;
    private Double NetRecv;
    private Double NetSent;
    private Double DiskReadRates;
    private Double DiskWriteRates;

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Double getMemUsage() {
        return MemUsage;
    }

    public Double getCpuUsage() {
        return CpuUsage;
    }

    public Double getNetRecv() {
        return NetRecv;
    }

    public Double getNetSent() {
        return NetSent;
    }

    public Double getDiskReadRates() {
        return DiskReadRates;
    }

    public Double getDiskWriteRates() {
        return DiskWriteRates;
    }

    public DispersedRecord(String hostname, String ip, Timestamp timestamp, Double memUsage, Double cpuUsage, Double netRecv, Double netSent, Double diskReadRates, Double diskWriteRates) {
        this.hostname = hostname;
        this.ip = ip;
        this.timestamp = timestamp;
        MemUsage = memUsage;
        CpuUsage = cpuUsage;
        NetRecv = netRecv;
        NetSent = netSent;
        DiskReadRates = diskReadRates;
        DiskWriteRates = diskWriteRates;
    }

    @Override
    public String toString() {
        return "Record{" +
                "hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                ", timestamp=" + timestamp +
                ", MemUsage=" + MemUsage +
                ", CpuUsage=" + CpuUsage +
                ", NetRecv=" + NetRecv +
                ", NetSent=" + NetSent +
                ", DiskReadRates=" + DiskReadRates +
                ", DiskWriteRates=" + DiskWriteRates +
                '}';
    }
}
