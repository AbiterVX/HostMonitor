package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispersedHostMonitor {
    //配置类
    private DispersedConfig dispersedConfig = DispersedConfig.getInstance();
    //整体JSON信息
    public JSONObject summaryInfo;
    //Host的JSON信息Map
    public Map<String, JSONObject> hostInfoMap = new HashMap<>();
    //磁盘故障预测Info-List
    public Map<String, JSONObject> dfpInfoList = new HashMap<>();
    //测速Info-List
    public Map<String, JSONObject> speedMeasurementInfoList = new HashMap<>();

    //单例
    private volatile static DispersedHostMonitor dispersedHostMonitor;
    public static DispersedHostMonitor getInstance(){
        if(dispersedHostMonitor ==null){
            synchronized (DispersedConfig.class){
                if(dispersedHostMonitor ==null){
                    dispersedHostMonitor =new DispersedHostMonitor();
                }
            }
        }
        return dispersedHostMonitor;
    }
    private DispersedHostMonitor(){
        summaryInfo = dispersedConfig.getSummaryJson();
    }


}
