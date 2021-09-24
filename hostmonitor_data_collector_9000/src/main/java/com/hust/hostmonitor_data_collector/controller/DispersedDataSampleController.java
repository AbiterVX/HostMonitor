package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.service.DispersedDataService;
import com.hust.hostmonitor_data_collector.service.DispersedDataServiceImpl;
import com.hust.hostmonitor_data_collector.service.testService;
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
    @Autowired
    testService testService1;
    /**
     * 获取信息-Dashboard-Summary统计页面的JSON数据
     * 以JSON数据格式返回，json数据格式可以参考项目目录下的/ConfigData/DispersedConfig.json下的summary字段内的各字段
     * 格式：{"summary":
     *       "hostIp":[],            节点IP列表（JSON数组）
     *       "connectedCount": 0,     已连接的节点数量
     *       "sumCapacity": 0,        总存储容量
     *       "windowsHostCount": 0,      windows操作系统节点数量
     *       "linuxHostCount": 0,         linux操作系统节点数量
     *       "hddCount": 0,               hdd盘数量
     *       "ssdCount": 0,               ssd盘数量
     *       "lastUpdateTime": 0,         暂时不会用到
     *       "load": [                    负载统计数量
     *         [0,0,0],                   CPU负载分别处于低中高的主机数量
     *         [0,0,0],                   内存负载分别处于低中高的主机数量
     *         [0,0,0]                    磁盘负载分别处于低中高的磁盘数量
     *       ]
     *      }
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
     * 以JSON数据返回，获取全部节点的实时监控信息，在JSON中以节点的IP作为各个节点数据的key
     * 格式：{"Ip1":
     *      {
     *       "hostInfo":{       hostInfo字段为该节点的大部分监控采样信息，为JSON对象格式
     *           "hostName": "",        节点名称
     *           "connected": false,    节点连接状态
     *           "ip": "0.0.0.0",       节点IP
     *           "osName": "OS",        节点操作系统名称
     *           "memoryUsage": [0,0],   节点内存使用量/节点内存总量（json数组）
     *           "netReceiveSpeed": 0,    节点网络流量接收速率
     *           "netSendSpeed": 0,     节点网络速度发送速率
     *           "cpuUsage": 0,         CPU占用率
     *           "diskCapacityTotalUsage": [0,0],   总磁盘使用量/总磁盘容量（json数组）
     *           "lastUpdateTime": 0,       暂时无需使用
     *           "diskInfoList": [],        所有磁盘的详细信息列表（json数组）,单个磁盘的信息组织格式见  /ConfigData/DispersedConfig.json下的diskInfo字段内的各字段
     *           "cpuInfoList": [],         所有CPU的详细信息列表（json数组）,单个CPU的信息组织格式见  /ConfigData/DispersedConfig.json下的cpuInfo字段内的各字段
     *           "gpuInfoList": [],         所有GPU的详细信息列表（json数组）,单个GPU信息组织格式见  /ConfigData/DispersedConfig.json下的gpuInfo字段内的各字段
     *           "processInfoList": [],     主要消耗资源的进程的详细信息列表(json数组),单个进程信息组织格式见  /ConfigData/DispersedConfig.json下的processInfo字段内的各字段
     *           "diskTotalIOPS":0,         该节点所有磁盘的实时总IOPS
     *           "diskTotalWriteSpeed":0,   该节点所有磁盘的实时总写入速度
     *           "diskTotalReadSpeed":0     该节点所有磁盘的实时总读取速度
     *       },
     *       },
     *      }
     */
    @GetMapping(value="/getHostInfo/All/Dashboard")
    @ResponseBody
    public String getHostInfo_All_Dashboard(){
        String result= dispersedDataService.getHostInfoDashboardAll();
        return result;
    }

    /**
     * 获取信息-Dashboard-DiskInfo-全部Host，空接口，暂无实际使用
     * 格式：{"Ip1":[{},{}], }
     */
    @GetMapping(value="/getDiskInfo/All/Dashboard")
    @ResponseBody
    public String getDiskInfo_All_Dashboard(){
        String result= dispersedDataService.getDiskInfoAll();
        return result;
    }

    /**
     * 获取信息-HostDetail-HostInfo-某个节点的具体信息，与getHostInfo_All_Dashboard()类似，但是只获取了指定的一个节点的信息，且字段相对较少
     * 参数：Ip 需要查询的节点Ip
     * 格式：{"hostInfo":{
     *        "hostName": "",        节点名称
     *        "connected": false,    节点连接状态
     *        "ip": "0.0.0.0",       节点IP
     *        "osName": "OS",        节点操作系统名称
     *        "memoryUsage": [0,0],   节点内存使用量/节点内存总量（json数组）
     *        "netReceiveSpeed": 0,    节点网络流量接收速率
     *        "netSendSpeed": 0,     节点网络速度发送速率
     *        "cpuUsage": 0,         CPU占用率
     *        "diskCapacityTotalUsage": [0,0],   总磁盘使用量/总磁盘容量（json数组）
     *        "lastUpdateTime": 0,       暂时无需使用
     *        "diskInfoList": [],        所有磁盘的详细信息列表（json数组）,单个磁盘的信息组织格式见  /ConfigData/DispersedConfig.json下的diskInfo字段内的各字段
     *        "cpuInfoList": [],         所有CPU的详细信息列表（json数组）,单个CPU的信息组织格式见  /ConfigData/DispersedConfig.json下的cpuInfo字段内的各字段
     *        "gpuInfoList": [],         所有GPU的详细信息列表（json数组）,单个GPU信息组织格式见  /ConfigData/DispersedConfig.json下的gpuInfo字段内的各字段
     *        "processInfoList": [],     主要消耗资源的进程的详细信息列表(json数组),单个进程信息组织格式见  /ConfigData/DispersedConfig.json下的processInfo字段内的各字段
     * }}
     */
    @GetMapping(value="/getHostInfo/HostDetail/{Ip}")
    @ResponseBody
    public String getHostInfo_HostDetail(@PathVariable Map<String,String> map){
        String result= dispersedDataService.getHostInfoDetail(map.get("Ip"));
        return result;
    }

    /**
     * 获取信息-HostDetail-HostInfo-Trend-某个Host 获取某个节点的过去24小时的数据，用于绘制折线趋势图
     * 参数：Ip 需要获取的节点Ip
     * 格式：三层嵌套的json数组
     *      [
     *       [[timestamp,value],[],[],...],         cpu使用率变化情况
     *       [[timestamp,value],[],[],...],         内存使用率变化情况
     *       [[timestamp,value],[],[],...],         磁盘读取速度变化情况
     *       [[timestamp,value],[],[],...],         磁盘写入速度变化情况
     *       [[timestamp,value],[],[],...],         网络接受速度变化情况
     *       [[timestamp,value],[],[],...]          网络发送速度变化情况
     *       ]
     */
    @GetMapping(value="/getHostInfo/Trend/HostDetail/{Ip}")
    @ResponseBody
    public String getHostInfo_Trend_HostDetail(@PathVariable Map<String,String> map){
        String result= dispersedDataService.getHostInfoDetailTrend(map.get("Ip"));
        return result;
    }

    /**
     * 获取信息-HostDetail-DiskInfo-某个Host 此接口预留，暂时未使用
     * 参数：Ip
     * 格式：{}
     */
    @GetMapping(value="/getDiskInfo/{Ip}")
    @ResponseBody
    public String getDiskInfo(@PathVariable Map<String,String> map){
        String result= dispersedDataService.getDiskInfo(map.get("Ip"));
        return result;
    }

    /**
     * 获取信息-SpeedMeasurement-All 获取测速结果信息，此接口预留，暂时未完成
     * 格式：[{},{} ]
     */
    @GetMapping(value="/getSpeedMeasurementInfo/All")
    @ResponseBody
    public String getSpeedMeasurementInfo_All(){
        String result= dispersedDataService.getSpeedMeasurementInfoAll();
        return result;
    }




    //-----故障预测-----  以下为磁盘故障预测相关接口

    /**
     * 获取磁盘故障预测综合页面相关的数据
     * 返回JSON对象格式示例
     *{
     *     "SummaryChart": [    对各盘损坏风险分为低，中，高三类，三类分别的数量
     *         1,
     *         0,
     *         0
     *     ],
     *     "hddCount": [3，5],      hddCount ssdCount diskType 三个字段需要一起使用,三个字段同一下标的值属于一组，例如西数品牌得出hdd坏盘有3块，ssd坏盘有4盘
     *     "ssdCount": [4，6],
     *     "diskType": [“西数”，“希捷”]，
     *     "trend": [               统计了过去14天的总坏盘数的变化趋势，两层json数组嵌套，内层第一个值为时间戳
     *         [
     *             1623168000000,
     *             0
     *         ],...
     *         ],
     *     "dfpComparison": [       磁盘故障预测的相关指标信息，第一个json对象是预计值，第二个是实际值（目前全部设定为了0.6，还未加入真实值)
     *         {
     *             "Accuracy": 0.9285714030265808,
     *             "Specificity": 0.9285714030265808,
     *             "field": "predict",
     *             "FDR": 0.7450980544090271,
     *             "Precision": 0.9285714030265808,
     *             "FAR": 0.006896551698446274,
     *             "FNR": 0.2549019753932953,
     *             "ErrorRate": 0.9285714030265808,
     *             "AUC": 0.8691007494926453
     *         },
     *         {
     *             "Accuracy": 0.6,
     *             "Specificity": 0.6,
     *             "field": "reality",
     *             "FDR": 0.6,
     *             "Precision": 0.6,
     *             "FAR": 0.6,
     *             "FNR": 0.6,
     *             "ErrorRate": 0.6,
     *             "AUC": 0.6
     *         }
     *     ],
     *
     * }
     */
    @GetMapping(value="/getDFPSummaryInfo")
    @ResponseBody
    public String getDFPSummaryInfo(){
        String result=dispersedDataService.getDFPSummary();
        return result;

    }

    /**
     * 获取信息-DFP-Trend-某个Host，获取某一个节点的某一块盘的磁盘预测结果（损坏可能性）的趋势变化（所有变化记录）
     * 参数：Ip节点的Ip,diskName磁盘序列号
     * 格式：[[0,0],... ] 两层嵌套json数组，内层第一个值为时间戳，第二个值为对应的预测损坏可能性
     */
    @GetMapping(value="/getDFPInfo/Trend/{Ip}/{diskName}")
    @ResponseBody
    public String getDFPInfoTrend(@PathVariable Map<String,String> map){
        String result= dispersedDataService.getDFPInfoTrend(map.get("Ip"),map.get("diskName"));
        return result;
    }

    /**
     * 获取信息-DFP-All 获取所有磁盘的最新一条磁盘故障预测信息，结果是一个json数组，内部成员为Json对象
     * 格式：[
     *     {
     *         "hostName": "LAPTOP-A3C9JUCK",   磁盘对应主机的名字
     *         "predictResult": 0,          磁盘被预测是否为坏盘
     *         "predictProbability": 90.0,      预测是坏盘的可能性（注意0。0为坏盘 100.0为好盘）
     *         "ip": "127.0.0.1",           磁盘对应主机的ip
     *         "model": "ST4000DM000",      用于预测的模型使用的训练数据对应的磁盘的型号（会尽量选择相近的）
     *         "diskSerial": "WD-WXB1A15FV7P2",     磁盘序列号
     *         "diskType": 0,       磁盘类型（0是hdd,1是ssd）
     *         "diskCapacity": 931.48,      磁盘的容量（GB）
     *         "manufacturer": "WD Elements 10B8 USB Device (标准磁盘驱动器)",  磁盘的型号（字段名不准确）
     *         "timestamp": 1624336440000       此条预测信息使用的smart信息对应的时间戳
     *     },
     *     ...
     * ]
     */
    @GetMapping(value="/getDFPInfo/List")
    @ResponseBody
    public String getDFPInfoList(){
        String result= dispersedDataService.getDFPInfoAll();
        return result;
    }

    /**
     * 请求进行一次磁盘模型的训练
     * @param jsonParam 训练对应的参数
     * @return “train” 无实际展示意义
     */
    @PostMapping(value="/dfpTrain",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String dfpTrain(@RequestBody JSONObject jsonParam){
        //System.out.println(jsonParam);

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
        JSONObject extraParams= new JSONObject();
        if(modelType == 1){
            JSONArray maxDepth = jsonParam.getJSONArray("maxDepth");
            JSONArray maxFeatures = jsonParam.getJSONArray("maxFeatures");
            JSONArray nEstimators = jsonParam.getJSONArray("nEstimators");
            extraParams.put("max_depth",maxDepth);
            extraParams.put("max_features",maxFeatures);
            extraParams.put("n_estimators",nEstimators);


            System.out.println("[模型训练]开始训练");
            dispersedDataService.train(modelType,positiveDataProportion,negativeDataProportion,verifyProportion,extraParams,userID);


        }
        return "Train";
    }

    /**
     * 与上述训练的请求配套使用，上述方法使用后，可通过此接口获取训练的实时进度信息
     * 返回json数组，包含三个数字，分别是训练中的三个阶段：数据预处理，获取训练数据，模型训练的阶段进度的百分比
     * [
     *     -1,
     *     -1,
     *     -1
     * ]
     */
    @GetMapping(value="/getDFPTrainProgress")
    @ResponseBody
    public String getDFPTrainProgress(){
        return dispersedDataService.getTrainProgress().toString();
    }

    /**
     * 获取所有的模型训练的记录，以json数组形式返回
     * 格式：
     * [
     *     {
     *         "buildTime": 1624336666000,          训练时间的时间戳
     *         "diskModel": "ST4000DM000",          训练使用的磁盘数据对应的模型
     *         "FNR": 0.254902,                     训练指标FNR
     *         "OperatorID": "hust",                进行此次训练的用户ID
     *         "params": "{\"max_features\":[4,7,10],\"max_depth\":[10,20,30],\"n_estimators\":[10,20,30,40]}",     训练使用的参数
     *         "AUC": 0.869101,                     训练指标AUC
     *         "Accuracy": 0.928571,                训练指标Accuracy
     *         "Specificity": 0.993103,             训练指标Specificity
     *         "FDR": 0.745098,                     训练指标FDR
     *         "Precision": 0.0,                    训练指标Precision
     *         "FAR": 0.00689655,                   训练指标FAR
     *         "model": 1,                          模型对应磁盘是否为ssd
     *         "ErrorRate": 0.0714286               训练指标ErrorRate
     *     },
     *     {
     *         "buildTime": 1624332959000,
     *         "diskModel": "ST4000DM000",
     *         "FNR": 0.215686,
     *         "OperatorID": "hust",
     *         "params": "{\"max_features\":[4,7,10],\"max_depth\":[10,20,30],\"n_estimators\":[10,20,30,40]}",
     *         "AUC": 0.888709,
     *         "Accuracy": 0.938776,
     *         "Specificity": 0.993103,
     *         "FDR": 0.784314,
     *         "Precision": 0.0,
     *         "FAR": 0.00689655,
     *         "model": 1,
     *         "ErrorRate": 0.0612245
     *     },....
     * ]
     */
    @GetMapping(value="/getDFPTrainRecord/List")
    @ResponseBody
    public String getDFPTrainRecordList(){
        String string=dispersedDataService.getDFPTrainList();
        return string;
    }


    /**
     * 测试用接口，无展示意义
     * @return
     */
    @GetMapping(value = "/insertTestData")
    @ResponseBody
    public String signUp() {
        testService1.insertTestData();
        return "hhhh";

    }


}
