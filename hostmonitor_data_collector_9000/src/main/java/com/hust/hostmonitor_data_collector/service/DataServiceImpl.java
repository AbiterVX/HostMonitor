package com.hust.hostmonitor_data_collector.service;

public class DataServiceImpl implements DataService{
    @Override
    public String getHostIp() {
        return "[\"ip1\",\"ip2\",\"ip3\"]";
    }

    @Override
    public String getHostState() {
        return "[X,X,X]";
    }

    @Override
    public String getHostHardwareInfo() {
        return "null";
    }

    @Override
    public String getHostInfoRealTime() {
        return "null";
    }

    @Override
    public String getHostInfoRecent(int index, int hour) {
        return "null";
    }

    @Override
    public String getHostInfoField(int index, int hour, HostInfoFieldType field) {
        return "null";
    }
}
