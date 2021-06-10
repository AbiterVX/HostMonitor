package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.service.DispersedDataService;
import com.hust.hostmonitor_data_collector.service.DispersedDataServiceImpl;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredictProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
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
        return null;
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
        return null;
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




    //-----故障预测-----

    @GetMapping(value="/getDFPSummaryInfo")
    @ResponseBody
    public String getDFPSummaryInfo(){
        return null;
    }

    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @GetMapping(value="/getDFPInfo/Trend/{hostName}/{diskName}")
    @ResponseBody
    public String getDFPInfoTrend(@PathVariable Map<String,String> map){
        String result= dispersedDataService.getDFPInfoTrend(map.get("hostName"),map.get("diskName"));
        return result;
    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getDFPInfo/List")
    @ResponseBody
    public String getDFPInfoList(){
        String result= dispersedDataService.getDFPInfoAll();
        return result;
    }


    @PostMapping(value="/dfpTrain",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String dfpTrain(@RequestBody JSONObject jsonParam){
        //服务端身份验证-管理员
        String userID = jsonParam.getString("userID");
        String password = jsonParam.getString("password");
        if(!dispersedDataService.userAuthoirtyCheck(userID,password,1)){
            return "Permission Denied";
        }
        //数据预处理
        //TODO 对传回的数据进行检验
        float positiveDataProportion = jsonParam.getFloat("positiveDataProportion");
        float negativeDataProportion = jsonParam.getFloat("negativeDataProportion");
        float verifyProportion = jsonParam.getFloat("verifyProportion");
        //模型具体参数
        int modelType = jsonParam.getInteger("modelType");
        JSONObject extraParams=null;
        if(modelType == 1){
            JSONArray maxDepth = jsonParam.getJSONArray("maxDepth");
            JSONArray maxFeatures = jsonParam.getJSONArray("maxFeatures");
            JSONArray nEstimators = jsonParam.getJSONArray("nEstimators");
            extraParams.put("max_depth",maxDepth);
            extraParams.put("max_features",maxFeatures);
            extraParams.put("n_estimators",nEstimators);
        }
        dispersedDataService.train(modelType,positiveDataProportion,negativeDataProportion,verifyProportion,extraParams,userID);
        return null;
    }

    @GetMapping(value="/getDFPTrainProgress")
    @ResponseBody
    public String getDFPTrainProgress(){
        return dispersedDataService.getTrainProgress().toString();
    }

    @GetMapping(value="/getDFPTrainRecord/List")
    @ResponseBody
    public String getDFPTrainRecordList(){
        String string=dispersedDataService.getDFPTrainList(10,1);
        return string;
    }



}
