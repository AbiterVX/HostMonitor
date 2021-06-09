package com.hust.hostmonitor_data_collector.service;

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

}
