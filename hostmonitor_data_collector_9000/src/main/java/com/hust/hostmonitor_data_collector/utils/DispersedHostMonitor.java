package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONObject;
import oshi.util.FormatUtil;

import java.sql.Timestamp;
import java.util.*;

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

    public DataReceiver getDataReceiver() {
        return dataReceiver;
    }

    //serverSocket
    private DataReceiver dataReceiver=new DataReceiver(this);

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
        System.out.println("???");
        summaryInfo = dispersedConfig.getSummaryJson();
        dataReceiver.startListening();
    }
    public void UpdateSummaryInfo(){
        long totalSumCapacity=0;
        int windowsCount=0,linuxCount=0,HDDCount=0,SSDCount=0;
        for(Map.Entry<String,JSONObject> hostinfo: hostInfoMap.entrySet()){
            summaryInfo.getJSONArray("hostName").add(hostinfo.getValue().get("hostName"));
            totalSumCapacity+=hostinfo.getValue().getLong("diskCapacityTotalSizeSum");
            if(hostinfo.getValue().getString("osName").toLowerCase().contains(("windows").toLowerCase())){
                windowsCount++;
            }
            else{
                linuxCount++;
            }
        }

        summaryInfo.put("sumCapacity",storageFormatUtils(totalSumCapacity));
        summaryInfo.put("windowsHostCount",windowsCount);
        summaryInfo.put("linuxHostCount",linuxCount);
        summaryInfo.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));

    }
    public String storageFormatUtils(long storageSize){
        return storageSize > 0L ? FormatUtil.formatBytesDecimal(storageSize) : "?";
    }

    public static void main(String[] args){
        DispersedHostMonitor.getInstance();
    }

}
