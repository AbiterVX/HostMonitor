package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oshi.util.FormatUtil;

import java.sql.Timestamp;
import java.util.*;

public class DispersedHostMonitor {
    //配置类
    private DispersedConfig dispersedConfig = DispersedConfig.getInstance();
    //整体JSON信息
    public JSONObject summaryInfo;
    public float[][] loadPartition;
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
        System.out.println("[Init]DispersedHostMonitor Initialization");
        summaryInfo = dispersedConfig.getSummaryJson();
        loadPartition = dispersedConfig.getLoadPartitionJson();
        dataReceiver.startListening();
    }
    public void UpdateSummaryInfo(){
        double totalSumCapacity=0;
        int windowsCount=0,linuxCount=0,HDDCount=0,SSDCount=0,connectedCount=0;
        float[][] loadCount = new float[][]{{0,0,0},{0,0,0},{0,0,0}};
        JSONArray hostNames=new JSONArray();
        for(Map.Entry<String,JSONObject> hostInfo: hostInfoMap.entrySet()){
            JSONObject hostInfoJson = hostInfo.getValue();
            hostNames.add(hostInfoJson.get("hostName"));
            totalSumCapacity+=hostInfoJson.getJSONArray("diskCapacityTotalUsage").getDouble(1);
            if(hostInfoJson.getString("osName").toLowerCase().contains(("windows").toLowerCase())){
                windowsCount++;
            }
            else{
                linuxCount++;
            }
            if(hostInfoJson.getBoolean("connected")){
                connectedCount++;
            }

            //-----负载统计
            //cpu负载统计
            JSONArray cpuInfoList = hostInfoJson.getJSONArray("cpuInfoList");
            for(int i=0;i<cpuInfoList.size();i++){
                float cpuUsage = cpuInfoList.getJSONObject(i).getFloat("cpuUsage");
                for(int j=0;j<loadPartition[0].length;j++){
                    if(cpuUsage <= loadPartition[0][j]){
                        loadCount[0][j] += 1;
                        break;
                    }
                }
            }
            //内存负载统计
            JSONArray memoryUsageJson =  hostInfoJson.getJSONArray("memoryUsage");
            float memoryUsage = (memoryUsageJson.getFloat(0) / memoryUsageJson.getFloat(1))*100;
            for(int j=0;j<loadPartition[1].length;j++){
                if(memoryUsage <= loadPartition[1][j]){
                    loadCount[1][j] += 1;
                    break;
                }
            }
            //硬盘负载统计
            JSONArray diskInfoList = hostInfoJson.getJSONArray("diskInfoList");
            for(int i=0;i<diskInfoList.size();i++){
                JSONArray diskCapacitySize =  diskInfoList.getJSONObject(i).getJSONArray("diskCapacitySize");
                float diskUsage = (diskCapacitySize.getFloat(0) / diskCapacitySize.getFloat(1))*100;
                for(int j=0;j<loadPartition[2].length;j++){
                    if(diskUsage <= loadPartition[2][j]){
                        loadCount[2][j] += 1;
                        break;
                    }
                }
            }

        }

        summaryInfo.put("hostName",hostNames);
        summaryInfo.put("sumCapacity",totalSumCapacity);
        summaryInfo.put("windowsHostCount",windowsCount);
        summaryInfo.put("linuxHostCount",linuxCount);
        summaryInfo.put("connectedCount",connectedCount);
        summaryInfo.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));


        JSONArray load = summaryInfo.getJSONArray("load");
        for(int i=0;i<load.size();i++){
            JSONArray currentLoad = load.getJSONArray(i);
            for(int j=0;j<currentLoad.size();j++){
                currentLoad.set(j,loadCount[i][j]);
            }
        }


    }
    public String storageFormatUtils(long storageSize){
        return storageSize > 0L ? FormatUtil.formatBytesDecimal(storageSize) : "?";
    }

    public static void main(String[] args){
        DispersedHostMonitor.getInstance();
    }

}
