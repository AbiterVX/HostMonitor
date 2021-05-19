package com.hust.hostmonitor_web.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/Dispersed")
public class DispersedDataSampleController_Remote {

    //RestTemplate
    @Resource
    private RestTemplate restTemplate;

    //读取配置文件字段:data_collector_9000 URL
    @Value("${service-url.data_collector_service}")
    private String dataCollectorUrl;


    /**
     * 获取信息-Dashboard-Summary统计
     * 格式：{"summary":}
     */
    @GetMapping(value="/getSummary/Dashboard")
    @ResponseBody
    public String getSummary_Dashboard(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getSummary/Dashboard",String.class);
        //System.out.println("[请求][getHostState]:"+result);
        return result;
    }

    /**
     * 获取信息-Dashboard-HostInfo-全部Host
     * 格式：{"hostName1":{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}, }
     */
    @GetMapping(value="/getHostInfo/All/Dashboard")
    @ResponseBody
    public String getHostInfo_All_Dashboard(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getHostInfo/All/Dashboard",String.class);
        return result;
    }

    /**
     * 获取信息-Dashboard-DiskInfo-全部Host
     * 格式：{"hostName1":[{},{}], }
     */
    @GetMapping(value="/getDiskInfo/All/Dashboard")
    @ResponseBody
    public String getDiskInfo_All_Dashboard(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getDiskInfo/All/Dashboard",String.class);
        return result;
    }

    /**
     * 获取信息-HostDetail-HostInfo-某个Host
     * 参数：hostName
     * 格式：{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}
     */
    @GetMapping(value="/getHostInfo/HostDetail/{hostName}")
    @ResponseBody
    public String getHostInfo_HostDetail(@PathVariable Map<String,String> map){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getHostInfo/HostDetail/"+map.get("hostName"),String.class);
        return result;
    }

    /**
     * 获取信息-HostDetail-HostInfo-Trend-某个Host
     * 参数：hostName
     * 格式：{"cpuUsage":{},"memoryUsage":{},"diskIO":{},"netIO":{}}
     */
    @GetMapping(value="/getHostInfo/Trend/HostDetail/{hostName}")
    @ResponseBody
    public String getHostInfo_Trend_HostDetail(@PathVariable Map<String,String> map){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getHostInfo/Trend/HostDetail/"+map.get("hostName"),String.class);
        return result;
    }

    /**
     * 获取信息-HostDetail-DiskInfo-某个Host
     * 参数：hostName
     * 格式：{}
     */
    @GetMapping(value="/getDiskInfo/{hostName}")
    @ResponseBody
    public String getDiskInfo(@PathVariable Map<String,String> map){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getDiskInfo/"+map.get("hostName"),String.class);
        return result;
    }


    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @GetMapping(value="/getDFPInfo/Trend/{hostName}/{diskName}")
    @ResponseBody
    public String getDFPInfo_Trend(@PathVariable Map<String,String> map){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getDFPInfo/Trend/"+map.get("hostName")+"/"+map.get("diskName"),String.class);
        return result;
    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getDFPInfo/All")
    @ResponseBody
    public String getDFPInfo_All(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getDFPInfo/All",String.class);
        return result;
    }

    /**
     * 获取信息-SpeedMeasurement-All
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getSpeedMeasurementInfo/All")
    @ResponseBody
    public String getSpeedMeasurementInfo_All(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/Dispersed/getSpeedMeasurementInfo/All",String.class);
        return result;
    }




}
