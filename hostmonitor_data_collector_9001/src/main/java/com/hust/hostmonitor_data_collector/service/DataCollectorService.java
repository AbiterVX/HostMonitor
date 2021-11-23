package com.hust.hostmonitor_data_collector.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface DataCollectorService {
    String getDashboardSummary();
    String getHostInfoDashboardAll();
    String getHostInfoDetail(String hostName);
    String getDiskInfoAll();
    String getDiskInfo(String hostName);
    String getHostInfoDetailTrend(String hostName);
    String getDFPInfoTrend(String hostIp,String diskName);
    String getDFPInfoAll();
    void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion,
               JSONObject extraParams, String operatorID);
    //1 admin,2 superAdmin
    boolean userAuthoirtyCheck(String user, String password, int checkLevel);
    String getSpeedMeasurementInfoAll();
    String getDFPTrainList();
    //获取模型训练进度
    List<Float> getTrainProgress();
    String getDFPSummary();
    HashMap<String,JSONObject> getSocketMap();
    void setAllDiskDFPState(String hostIp, boolean b);

    String getAllHostsInfoDetail();

    String getHostsRouterInfo();

    String remoteTest(String nodeIp);

    void updateSystemSetting(JSONObject newSystemSetting);
    String test();

    String setDiskState(String diskSerial,boolean state);
}
