package com.hust.hostmonitor_data_collector.utils.linuxsample.Entity;

public class KylinGPU {
    private String name;
    private String deviceId;
    private String vendor;
    private String versionInfo;
    private long vram;

    public KylinGPU(String name, String deviceId, String vendor, String versionInfo, long vram) {
        this.name = name;
        this.deviceId = deviceId;
        this.vendor = vendor;
        this.versionInfo = versionInfo;
        this.vram = vram;
    }

    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getVendor() {
        return vendor;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public long getVram() {
        return vram;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public void setVram(long vram) {
        this.vram = vram;
    }
}
