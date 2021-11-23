package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormatConfig {
    private final JSONObject configJson = JSONObject.parseObject(readFile("ConfigData/Server/Config.json"));
    private final String path = System.getProperty("user.dir");
    private static volatile FormatConfig formatConfig=null;
    public static FormatConfig getInstance(){
        if(formatConfig ==null){
            synchronized (FormatConfig.class){
                if(formatConfig ==null){
                    formatConfig =new FormatConfig();
                }
            }
        }
        return formatConfig;
    }

    private FormatConfig(){

    }

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
    private JSONObject getConfigSampleFormat(String key){
        return JSONObject.parseObject(configJson.getJSONObject("SampleFormat").getJSONObject(key).toJSONString());
    }
    public String getCollectorIP(){
        return configJson.getString("DataCollectorServerIP");
    }
    public int getSampleSelect() {
        //0 OSHI,1 commands
        return configJson.getIntValue("clientSampleSelect");
    }
    public int getPort(int choice){
        if(choice==1){
            return configJson.getIntValue("ServerSampleListenPort");
        }
        else if(choice==2){
            return configJson.getIntValue("SpecialUsagePort");
        }
        return 7000;
    }
    //-----获取配置文件JsonObject-对外接口-----
    public JSONObject getHostInfoJson(){
        return getConfigSampleFormat("hostInfo");
    }
    public JSONObject getDiskInfoJson(){
        return getConfigSampleFormat("diskInfo");
    }
    public JSONObject getCpuInfoJson(){
        return getConfigSampleFormat("cpuInfo");
    }
    public JSONObject getGpuInfoJson(){
        return getConfigSampleFormat("gpuInfo");
    }
    public JSONObject getProcessInfoJson(){
        return getConfigSampleFormat("processInfo");
    }
    public JSONObject getNetInterfaceInfoJson(){
        return getConfigSampleFormat("netInterfaceInfo");
    }
//    public JSONObject getOutputInfoJson(){
//        return getConfigJsonObject("outputFormat");
//    }
//
    public Map<String, Float> getProcessFilter(){
        JSONObject processFilter = configJson.getJSONObject("filter");
        Map<String, Float> result = new HashMap<>();
        result.put("cpuUsage",processFilter.getFloat("cpuUsage"));
        result.put("memoryUsage",processFilter.getFloat("memoryUsage"));
        result.put("diskReadSpeed",processFilter.getFloat("diskReadSpeed"));
        result.put("diskWriteSpeed",processFilter.getFloat("diskWriteSpeed"));

        return result;
    }
}
