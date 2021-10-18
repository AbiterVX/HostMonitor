package com.hust.hostmonitor_web.controller;


import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_web.entity.RequestData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DataSampleController_Remote {


    //RestTemplate
    @Resource
    private RestTemplate restTemplate;

    //读取配置文件字段:data_collector_9000 URL
    @Value("${service-url.data_collector_service}")
    private String dataCollectorUrl;

    //缓存
    private Map<String, RequestData> cacheData = new HashMap<String, RequestData>();

    //获取请求数据
    public String getRequestData(String url, int coolDownTime) {
        /*long currentTime = System.currentTimeMillis();
        if (cacheData.containsKey(url)) {
            RequestData requestData = cacheData.get(url);
            if (currentTime - requestData.timeStamp < coolDownTime) {
                return requestData.data;
            }
        }*/
        //缓存有数据，在冷却时间内，重新发送请求
        //缓存无数据，直接发送请求
        String resultData = restTemplate.getForObject(url, String.class);
        //cacheData.put(url, new RequestData(resultData));
        return resultData;
    }

    public String postRequestData(String url, Map<String, String> map,int coolDownTime){
        long currentTime = System.currentTimeMillis();
        if (cacheData.containsKey(url)) {
            RequestData requestData = cacheData.get(url);
            if (currentTime - requestData.timeStamp < coolDownTime) {
                //冷却时间内，拒绝请求
                return "";
            }
        }
        //缓存无数据，直接发送请求
        String resultData = restTemplate.postForObject(url, map,String.class);
        cacheData.put(url, new RequestData(resultData));
        return resultData;
    }
    /**
     * 获取信息-Dashboard-Summary统计
     * 格式：{"summary":}
     */
    @GetMapping(value = "/getSummary/Dashboard")
    @ResponseBody
    public String getSummaryDashboard() {
        return getRequestData(dataCollectorUrl + "/getSummary/Dashboard", 60000);
    }

    /**
     * 获取信息-Dashboard-HostInfo-全部Host
     * 格式：{"hostName1":{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}, }
     */
    @GetMapping(value = "/getHostInfo/All/Dashboard")
    @ResponseBody
    public String getHostInfoAll() {
        return getRequestData(dataCollectorUrl + "/getHostInfo/All/Dashboard", 60000);
    }

    /**
     * 获取信息-HostDetail-HostInfo-某个Host
     * 参数：hostName
     * 格式：{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}
     */
    @GetMapping(value = "/getHostInfo/HostDetail/{hostName}")
    @ResponseBody
    public String getHostInfo_HostDetail(@PathVariable Map<String, String> map) {
        return getRequestData(dataCollectorUrl + "/getHostInfo/HostDetail/" + map.get("hostName"), 60000);
    }

    /**
     * 获取信息-HostDetail-HostInfo-Trend-某个Host
     * 参数：hostName
     * 格式：{"cpuUsage":{},"memoryUsage":{},"diskIO":{},"netIO":{}}
     */
    @GetMapping(value = "/getHostInfo/Trend/HostDetail/{hostName}")
    @ResponseBody
    public String getHostInfo_Trend_HostDetail(@PathVariable Map<String, String> map) {
        return getRequestData(dataCollectorUrl + "/getHostInfo/Trend/HostDetail/" + map.get("hostName"), 60000);
    }



    @GetMapping(value="/getDFPSummaryInfo")
    @ResponseBody
    public String getDFPSummaryInfo(){
        return getRequestData(dataCollectorUrl + "/getDFPSummaryInfo", 30000);
    }



    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @GetMapping(value = "/getDFPInfo/Trend/{hostName}/{diskName}")
    @ResponseBody
    public String getDFPInfo_Trend(@PathVariable Map<String, String> map) {
        return getRequestData(dataCollectorUrl + "/getDFPInfo/Trend/" + map.get("hostName") + "/" + map.get("diskName"), 60000);
    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @GetMapping(value = "/getDFPInfo/List")
    @ResponseBody
    public String getDFPInfo_All() {
        return getRequestData(dataCollectorUrl + "/getDFPInfo/List", 60000);
    }



    @PostMapping(value="/dfpTrain",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String dfpTrain(@RequestBody JSONObject jsonParam){
        System.out.println("[模型训练]");
        return restTemplate.postForObject(dataCollectorUrl + "/dfpTrain", jsonParam,String.class);
    }


    @GetMapping(value="/getDFPTrainProgress")
    @ResponseBody
    public String getDFPTrainProgress(){
        return getRequestData(dataCollectorUrl + "/getDFPTrainProgress", 1000);
    }


    @GetMapping(value="/getDFPTrainRecord/List")
    @ResponseBody
    public String getDFPTrainRecordList(){
        return getRequestData(dataCollectorUrl + "/getDFPTrainRecord/List", 1000);
    }


    @PostMapping(value="/diskSpeedTest",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String remoteTest(@RequestBody JSONObject jsonParam){
        System.out.println("[测速]");
        return restTemplate.postForObject(dataCollectorUrl + "/diskSpeedTest", jsonParam,String.class);
    }
}