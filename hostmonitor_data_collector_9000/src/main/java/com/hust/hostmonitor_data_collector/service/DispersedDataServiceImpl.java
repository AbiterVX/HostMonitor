package com.hust.hostmonitor_data_collector.service;

import com.hust.hostmonitor_data_collector.utils.DispersedHostMonitor;

public class DispersedDataServiceImpl implements DispersedDataService{

    private DispersedHostMonitor dispersedHostMonitor=DispersedHostMonitor.getInstance();

    @Override
    public String getDashboardSummary() {
        return null;
    }

    @Override
    public String getHostInfoDashboardAll() {
        return null;
    }

    @Override
    public String getHostInfoDetail(String hostName) {
        return null;
    }

    @Override
    public String getDiskInfoAll() {
        return null;
    }

    @Override
    public String getDiskInfo(String hostName) {
        return null;
    }

    @Override
    public String getHostInfoDetailTrend(String hostName) {
        return null;
    }

    @Override
    public String getDFPInfoTrend(String hostName, String diskName) {
        return null;
    }

    @Override
    public String getDFPInfoAll() {
        return null;
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }
}
