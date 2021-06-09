package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface DispersedDataService {

    String getDashboardSummary();

    String getHostInfoDashboardAll();

    String getHostInfoDetail(String hostName);

    String getDiskInfoAll();

    String getDiskInfo(String hostName);

    String getHostInfoDetailTrend(String hostName);

    String getDFPInfoTrend(String hostName,String diskName);

    String getDFPInfoAll();

    String getSpeedMeasurementInfoAll();

    String getDFPTrainList(int pageSize,int pageNo);

    //获取模型训练进度
    List<Float> getTrainProgress();

}
