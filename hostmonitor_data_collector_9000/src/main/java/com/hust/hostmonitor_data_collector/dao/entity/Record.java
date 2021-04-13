package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Record {
    private String ip;
    private Timestamp timestamp;
    private BigDecimal receiveBW;
    private BigDecimal transmitBW;
    private BigDecimal cpuUsage;
    private BigDecimal memoryUsage;
    private BigDecimal diskUsage;
    private BigDecimal temp;
    private BigDecimal energy;

    private Integer iNumber;
    private Integer oNumber;

    private float receiveBWf;
    private float transmitBWf;
    private float cpuUsagef;
    private float memoryUsagef;
    private float diskUsagef;
    private float tempf;
    private float energyf;

    public Record(String ip, Timestamp timestamp, Integer iNumber, Integer oNumber, float receiveBWf, float transmitBWf, float cpuUsagef, float memoryUsagef, float diskUsagef, float tempf, float energyf) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.iNumber = iNumber;
        this.oNumber = oNumber;
        this.receiveBWf = receiveBWf;
        this.transmitBWf = transmitBWf;
        this.cpuUsagef = cpuUsagef;
        this.memoryUsagef = memoryUsagef;
        this.diskUsagef = diskUsagef;
        this.tempf = tempf;
        this.energyf = energyf;
    }
    public Record(String ip, Timestamp timestamp, BigDecimal receiveBW, BigDecimal transmitBW, BigDecimal cpuUsage, BigDecimal memoryUsage, BigDecimal diskUsage,Integer iNumber, Integer oNumber,BigDecimal temp, BigDecimal energy) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.receiveBW = receiveBW;
        this.transmitBW = transmitBW;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.temp = temp;
        this.energy = energy;
        this.iNumber = iNumber;
        this.oNumber = oNumber;
        this.receiveBWf=receiveBW.floatValue();
        this.transmitBWf=transmitBW.floatValue();
        this.cpuUsagef=cpuUsage.floatValue();
        this.memoryUsagef=memoryUsage.floatValue();
        this.diskUsagef=diskUsage.floatValue();
        this.tempf=temp.floatValue();
        this.energyf=energy.floatValue();
    }



    public String getIp() {
        return ip;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getiNumber() {
        return iNumber;
    }

    public Integer getoNumber() {
        return oNumber;
    }

    public float getReceiveBWf() {
        return receiveBWf;
    }

    public float getTransmitBWf() {
        return transmitBWf;
    }

    public float getCpuUsagef() {
        return cpuUsagef;
    }

    public float getMemoryUsagef() {
        return memoryUsagef;
    }

    public float getDiskUsagef() {
        return diskUsagef;
    }

    public float getTempf() {
        return tempf;
    }

    public float getEnergyf() {
        return energyf;
    }

    @Override
    public String toString() {
        return "record{" +
                "ip='" + ip + '\'' +
                ", timestamp=" + timestamp +
                ", iNumber=" + iNumber +
                ", oNumber=" + oNumber +
                ", receiveBWf=" + receiveBWf +
                ", transmitBWf=" + transmitBWf +
                ", cpuUsagef=" + cpuUsagef +
                ", memoryUsagef=" + memoryUsagef +
                ", diskUsagef=" + diskUsagef +
                ", tempf=" + tempf +
                ", energyf=" + energyf +
                '}';
    }
}
