package com.hust.hostmonitor_data_collector.service;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class HybridDataCollectorService implements DataCollectorService{


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
    public String getDFPInfoTrend(String hostIp, String diskName) {
        return null;
    }

    @Override
    public String getDFPInfoAll() {
        return null;
    }

    @Override
    public void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion, JSONObject extraParams, String operatorID) {

    }

    @Override
    public boolean userAuthoirtyCheck(String user, String password, int checkLevel) {
        return false;
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }

    @Override
    public String getDFPTrainList() {
        return null;
    }

    @Override
    public List<Float> getTrainProgress() {
        return null;
    }

    @Override
    public String getDFPSummary() {
        return null;
    }
}
