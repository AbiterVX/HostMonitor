package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredictProgress;

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

   void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion,
                                    JSONObject extraParams,String operatorID);

    //1 admin,2 superAdmin
    boolean userAuthoirtyCheck(String user, String password, int checkLevel);

    String getSpeedMeasurementInfoAll();

    String getDFPTrainList(int pageSize,int pageNo);

}
