package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Record {
    private String ip;
    private Timestamp timestamp;
    private Double NetReceive;
    private Double NetSend;
    private Integer MemTotal;
    private Integer MemFree;
    private Integer MemAvailable;
    private Double Buffers;
    private Double Cached;
    private Integer TcpEstablished;
    private Double DiskTotalSize;
    private Double DiskOccupancyUsage;
    private Double CpuIdle;
    private Double Power;
    private Double temperature;
    private Integer Iops;
    private String Type;
    private Double ReadRates;
    private Double WriteRates;
    private Double Utils;

    public String getIp() {
        return ip;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getNetReceive() {
        return NetReceive;
    }

    public double getNetSend() {
        return NetSend;
    }

    public double getMemTotal() {
        return MemTotal;
    }

    public double getMemFree() {
        return MemFree;
    }

    public double getMemAvailable() {
        return MemAvailable;
    }

    public double getBuffers() {
        return Buffers;
    }

    public double getCached() {
        return Cached;
    }

    public int getTcpEstablished() {
        return TcpEstablished;
    }

    public double getDiskTotalSize() {
        return DiskTotalSize;
    }

    public double getDiskOccupancyUsage() {
        return DiskOccupancyUsage;
    }

    public double getCpuIdle() {
        return CpuIdle;
    }

    public double getPower() {
        return Power;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getIops() {
        return Iops;
    }

    public String getType() {
        return Type;
    }

    public double getReadRates() {
        return ReadRates;
    }

    public double getWriteRates() {
        return WriteRates;
    }

    public double getUtils() {
        return Utils;
    }

    public Record(String ip, Timestamp timestamp, Double netReceive, Double netSend, Integer memTotal, Integer memFree, Integer memAvailable, Double buffers, Double cached, Integer tcpEstablished, Double diskTotalSize, Double diskOccupancyUsage, Double cpuIdle, Double power, Double temperature, Integer iops, String type, Double readRates, Double writeRates, Double utils) {
        this.ip = ip;
        this.timestamp = timestamp;
        NetReceive = netReceive;
        NetSend = netSend;
        MemTotal = memTotal;
        MemFree = memFree;
        MemAvailable = memAvailable;
        Buffers = buffers;
        Cached = cached;
        TcpEstablished = tcpEstablished;
        DiskTotalSize = diskTotalSize;
        DiskOccupancyUsage = diskOccupancyUsage;
        CpuIdle = cpuIdle;
        Power = power;
        this.temperature = temperature;
        Iops = iops;
        Type = type;
        ReadRates = readRates;
        WriteRates = writeRates;
        Utils = utils;
    }

    @Override
    public String toString() {
        return "Record{" +
                "ip='" + ip + '\'' +
                ", timestamp=" + timestamp +
                ", NetReceive=" + NetReceive +
                ", NetSend=" + NetSend +
                ", MemTotal=" + MemTotal +
                ", MemFree=" + MemFree +
                ", MemAvailable=" + MemAvailable +
                ", Buffers=" + Buffers +
                ", Cached=" + Cached +
                ", TcpEstablished=" + TcpEstablished +
                ", DiskTotalSize=" + DiskTotalSize +
                ", DiskOccupancyUsage=" + DiskOccupancyUsage +
                ", CpuIdle=" + CpuIdle +
                ", Power=" + Power +
                ", temperature=" + temperature +
                ", Iops=" + Iops +
                ", Type='" + Type + '\'' +
                ", ReadRates=" + ReadRates +
                ", WriteRates=" + WriteRates +
                ", Utils=" + Utils +
                '}';
    }
}
