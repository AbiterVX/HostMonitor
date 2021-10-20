package com.hust.hostmonitor_data_collector.utils.linuxsample.Entity;

public class KylinDiskStore {
    private String serial;
    private String model;
    private long size;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KylinDiskStore(String name, String serial, String model, long size) {
        this.serial = serial;
        this.model = model;
        this.size = size;
        this.name=name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
