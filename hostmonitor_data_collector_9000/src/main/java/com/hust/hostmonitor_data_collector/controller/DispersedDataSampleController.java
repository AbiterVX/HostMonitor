package com.hust.hostmonitor_data_collector.controller;

import com.hust.hostmonitor_data_collector.service.DispersedDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/Dispersed")
public class DispersedDataSampleController {
    @Resource
    DispersedDataService dispersedDataService;

    /**
     * 获取信息-Dashboard-Summary统计
     * 格式：{"summary":}
     */
    @GetMapping(value="/getSummary/Dashboard")
    @ResponseBody
    public String getSummary_Dashboard(){
        String result= dispersedDataService.getDashboardSummary();
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
        String result= dispersedDataService.getHostInfoDashboardAll();
        return result;
    }

    /**
     * 获取信息-Dashboard-DiskInfo-全部Host
     * 格式：{"hostName1":[{},{}], }
     */
    @GetMapping(value="/getDiskInfo/All/Dashboard")
    @ResponseBody
    public String getDiskInfo_All_Dashboard(){
        String result= dispersedDataService.getDiskInfoAll();
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
        String result= dispersedDataService.getHostInfoDetail(map.get("hostName"));
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
        String result= dispersedDataService.getHostInfoDetailTrend(map.get("hostName"));
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
        String result= dispersedDataService.getDiskInfo(map.get("hostName"));
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
        String result= dispersedDataService.getDFPInfoTrend(map.get("hostName"),map.get("diskName"));
        return result;
    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getDFPInfo/All")
    @ResponseBody
    public String getDFPInfo_List(){
        String result= dispersedDataService.getDFPInfoAll();
        return result;
    }

    /**
     * 获取信息-SpeedMeasurement-All
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getSpeedMeasurementInfo/All")
    @ResponseBody
    public String getSpeedMeasurementInfo_All(){
        String result= dispersedDataService.getSpeedMeasurementInfoAll();
        return result;
    }
}
