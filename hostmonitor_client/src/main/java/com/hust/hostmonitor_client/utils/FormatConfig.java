package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FormatConfig {
    private final JSONObject configJson = JSONObject.parseObject(readFile("ConfigData/OriginalSampleDataFormat.json"));
    private final String path = System.getProperty("user.dir");
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
    private JSONObject getConfigJsonObject(String key){
        return JSONObject.parseObject(configJson.getJSONObject(key).toJSONString());
    }

    //-----获取配置文件JsonObject-对外接口-----
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
    public JSONObject getNetInterfaceInfoJson(){
        return getConfigJsonObject("netInterfaceInfo");
    }
    public JSONObject getOutputInfoJson(){
        return getConfigJsonObject("outputFormat");
    }



}
