package com.hust.hostmonitor_data_collector.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EnergyRecord {
    private Timestamp timestamp;
    private BigDecimal energy;
    private float energyf;

    public EnergyRecord(BigDecimal energy,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.energy = energy;
        this.energyf=energy.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getEnergyf() {
        return energyf;
    }

    @Override
    public String toString() {
        return "EnergyRecord{" +
                "timestamp=" + timestamp +
                ", energyf=" + energyf +
                '}';
    }
}
