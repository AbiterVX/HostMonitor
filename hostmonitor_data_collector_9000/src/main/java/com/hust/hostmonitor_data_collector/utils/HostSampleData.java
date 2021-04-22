package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostSampleData {
    //ip
    public String ip;
    //会话建立
    public boolean sessionConnected;
    //字段
    JSONObject sampleData;


    public HostSampleData(String _ip, JSONObject sampleDataFormat){
        ip = _ip;
        sessionConnected = false;

        sampleData = JSONObject.parseObject(sampleDataFormat.toJSONString());
    }

    //设置所有字段无效
    public void setAllValueInvalid(){
        for(String key:sampleData.keySet()){
            sampleData.getJSONObject(key).put("valid",false);
        }

        JSONObject diskValue = sampleData.getJSONObject("Disk").getJSONObject("value");
        for(String key:diskValue.keySet()) {
            JSONObject diskSampleData = diskValue.getJSONObject(key);
            for(String subKey:diskSampleData.keySet()) {
                diskSampleData.getJSONObject(subKey).put("valid",false);
            }
        }

        JSONObject temperatureValue = sampleData.getJSONObject("Temperature").getJSONObject("value");
        for(String key:temperatureValue.keySet()) {
            temperatureValue.getJSONObject(key).put("valid",false);
        }
    }

}
