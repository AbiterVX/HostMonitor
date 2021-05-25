package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DispersedConfig {
    //配置文件
    private final JSONObject configJson = JSONObject.parseObject(readFile("ConfigData/DispersedConfig.json"));
    //配置文件父路径-最终为打包jar的同级目录
    private final String path = System.getProperty("user.dir");

    //单例-init
    private volatile static DispersedConfig dispersedConfig;
    public static DispersedConfig getInstance(){
        if(dispersedConfig ==null){
            synchronized (DispersedConfig.class){
                if(dispersedConfig ==null){
                    dispersedConfig =new DispersedConfig();
                }
            }
        }
        return dispersedConfig;
    }
    private DispersedConfig(){
    }


    //读文件
    public String readFile(String filePath){
        String resultData = "";
        File file = new File(path,filePath);
        try {
            resultData = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    //获取配置文件JsonObject
    private JSONObject getConfigJsonObject(String key){
        return JSONObject.parseObject(configJson.getJSONObject(key).toJSONString());
    }

    //-----获取配置文件JsonObject-对外接口-----
    public JSONObject getSummaryJson(){
        return getConfigJsonObject("summary");
    }
    public JSONObject getHostInfoJson(){
        return getConfigJsonObject("hostInfo");
    }
    public JSONObject getDiskInfoJson(){
        return getConfigJsonObject("diskInfo");
    }
    public JSONObject getCpuInfoJson(){
        return getConfigJsonObject("cpuInfo");
    }
    public JSONObject getGpuInfoJson(){
        return getConfigJsonObject("gpuInfo");
    }
    public JSONObject getProcessInfoJson(){
        return getConfigJsonObject("processInfo");
    }
    public JSONObject getDFPJson(){
        return getConfigJsonObject("diskFailurePredictInfo");
    }
    public JSONObject getSpeedMeasurementInfoJson(){
        return getConfigJsonObject("speedMeasurementInfo");
    }
    public float[][] getLoadPartitionJson(){
        float[][] loadCount = new float[][]{{0,0,0},{0,0,0},{0,0,0}};
        List<List<Float>> partition = new ArrayList<>();
        List<JSONArray> loadPartition = new ArrayList<>();

        JSONObject loadPartitionJson = getConfigJsonObject("loadPartition");
        loadPartition.add(loadPartitionJson.getJSONArray("cpuLoad"));
        loadPartition.add(loadPartitionJson.getJSONArray("memoryLoad"));
        loadPartition.add(loadPartitionJson.getJSONArray("diskLoad"));

        for(int i=0;i<loadPartition.size();i++){
            JSONArray currentLoadPartition = loadPartition.get(i);
            for(int j=0;j<currentLoadPartition.size();j++){
                loadCount[i][j] = currentLoadPartition.getFloat(j);
            }
        }

        return loadCount;
    }
    //-----当前Data_Collector的IP
    public String getServerIp(){
        return configJson.getString("DataCollectorServerIP");
    }
    //-----当前Data_Collector采样监听端口
    public int getServerSampleListenPort(){
        return configJson.getInteger("ServerSampleListenPort");
    }

    public static void main(String[] args) {
        DispersedConfig dispersedConfig = DispersedConfig.getInstance();
        System.out.println(dispersedConfig.getServerIp());
    }
}
