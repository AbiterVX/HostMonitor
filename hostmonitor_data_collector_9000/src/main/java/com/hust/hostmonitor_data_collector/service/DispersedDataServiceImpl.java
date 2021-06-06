package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import com.hust.hostmonitor_data_collector.dao.DispersedMapper;
import com.hust.hostmonitor_data_collector.dao.entity.DFPRecord;
import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;
import com.hust.hostmonitor_data_collector.utils.DispersedHostMonitor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


public class DispersedDataServiceImpl implements DispersedDataService{
    @Autowired
    DispersedMapper dispersedMapper;
    @Autowired
    DiskFailureMapper diskFailureMapper;
    private final long sampleInterval=10000;
    private DispersedHostMonitor dispersedHostMonitor;
    public final int sampleStoreDelayMS=500;
    private String dataPath;
    //定时器
    private long DiskPredictTime;
    private Timer mainTimer = new Timer();
    private final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    //定时器任务
    private final TimerTask dataPersistanceTask = new TimerTask() {
        @Override
        public void run() {
            //采样
            System.out.println("Host Sample");

            try {
                Thread.sleep(sampleStoreDelayMS);
                //存储新采样的数据
                storeSampleData();
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println("[Thread Sleep Error]: In TimerTask run()");
            }
        }
    };
    private void storeSampleData(){
        System.out.println("[hostInfoMap size]"+dispersedHostMonitor.hostInfoMap.size());
        for(Map.Entry<String, JSONObject> entry: dispersedHostMonitor.hostInfoMap.entrySet()){
            JSONObject tempObject=entry.getValue();

            if(!tempObject.getBoolean("hasPersistent")){
                double memUsage=tempObject.getJSONArray("memoryUsage").getDouble(0)/tempObject.getJSONArray("memoryUsage").getDouble(1);
                double DiskReadRates=0;
                double DiskWriteRates=0;
                for(int i=0;i<tempObject.getJSONArray("diskInfoList").size();i++){
                    DiskWriteRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskWriteSpeed");
                    DiskReadRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskReadSpeed");
                }
                if( tempObject.getDouble("cpuUsage")==0){
                    continue;
                }
                dispersedMapper.insertNewRecord(tempObject.getString("hostName"),tempObject.getString("ip"),
                        tempObject.getTimestamp("lastUpdateTime")/*这里可能要改时间*/,memUsage,
                        tempObject.getDouble("cpuUsage"),
                        tempObject.getDouble("netReceiveSpeed"),
                        tempObject.getDouble("netSendSpeed"),DiskReadRates,DiskWriteRates);
                entry.getValue().put("hasPersistent",true);
                System.out.println("[Database]Insert a record.");
            }

            JSONArray diskArray=tempObject.getJSONArray("diskInfoList");
            for(int i=0;i<diskArray.size();i++){
                JSONObject tempDiskObject=diskArray.getJSONObject(i);
                String diskSerial=diskFailureMapper.queryDiskHardwareExists(tempDiskObject.getString("diskName"));
                if(diskSerial==null){
                    diskSerial=tempDiskObject.getString("diskName");

                    System.out.println(tempDiskObject.getJSONArray("diskCapacitySize").getDoubleValue(1));
                    diskFailureMapper.insertDiskHardwareInfo(diskSerial,tempObject.getString("hostName"),
                            tempDiskObject.getJSONArray("diskCapacitySize").getDoubleValue(1),
                            tempDiskObject.getIntValue("type")>0? true:false,
                            tempDiskObject.getString("diskModel"));
                }
                diskFailureMapper.insertDiskSampleInfo(diskSerial, tempObject.getTimestamp("lastUpdateTime"),tempDiskObject.getDoubleValue("diskIOPS"),
                        tempDiskObject.getDoubleValue("diskReadSpeed"),tempDiskObject.getDoubleValue("diskWriteSpeed"));
            }
        }
    }
    public DispersedDataServiceImpl(){
        dataPath=System.getProperty("user.dir")+"/DiskPredict/";
        dispersedHostMonitor=DispersedHostMonitor.getInstance();
        mainTimer.schedule(dataPersistanceTask,sampleInterval/2,sampleInterval);
    }
    /**
     * 获取信息-Dashboard-Summary统计
     * 格式：{"summary":}
     */
    @Override
    public String getDashboardSummary() {
        dispersedHostMonitor.UpdateSummaryInfo();
        return dispersedHostMonitor.summaryInfo.toJSONString();
    }

    //1
    /**
     * 获取信息-Dashboard-HostInfo-全部Host
     * 格式：{"hostName1":{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}, }
     */
    @Override
    public String getHostInfoDashboardAll() {
        JSONObject resultObject=new JSONObject();
        for(Map.Entry<String, JSONObject> entry: dispersedHostMonitor.hostInfoMap.entrySet()){
            resultObject.put(entry.getKey(),entry.getValue());
        }
        return resultObject.toJSONString();
    }


    /**
     * 获取信息-HostDetail-HostInfo-某个Host
     * 参数：hostName
     * 格式：{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}
     */
    //2
    @Override
    public String getHostInfoDetail(String hostName) {
        String result=dispersedHostMonitor.hostInfoMap.get(hostName).toJSONString();
        return result;
    }
    private JSONObject selectMainProcess(double cpuThreshold,JSONObject jsonObject){
        JSONObject resultObject=new JSONObject();
        resultObject.putAll(jsonObject);
        Iterator iterator=resultObject.getJSONArray("processInfoList").iterator();
        while(iterator.hasNext()){
            JSONObject tempObject=(JSONObject) iterator.next();
            if(tempObject.getDouble("cpuUsage")<cpuThreshold){
                iterator.remove();
            }
            else{
                tempObject.put("cpuUsage",doubleTo2bits_double(tempObject.getDouble("cpuUsage")*100));
                tempObject.put("memUsage",doubleTo2bits_double(tempObject.getDouble("memoryUsage")*100));
            }
        }
        return resultObject;
    }
    private double doubleTo2bits_double(double original){
        BigDecimal b=new BigDecimal(original);
        return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**
     * 获取信息-Dashboard-DiskInfo-全部Host
     * 格式：{"hostName1":[{},{}], }
     */
    public String getDiskInfoAll() {
        return null;
    }

    /**
     * 获取信息-HostDetail-DiskInfo-某个Host
     * 参数：hostName
     * 格式：{}
     */
    public String getDiskInfo(String hostName) {
        return null;
    }

    //时间段 cpuusage,memory,
    @Override
    public String getHostInfoDetailTrend(String hostName) {
        int hours=24;
        Timestamp highbound=new Timestamp(System.currentTimeMillis());
        Timestamp lowbound=new Timestamp(System.currentTimeMillis()-hours*3600*1000);
        List<DispersedRecord> dispersedRecordList= dispersedMapper.queryRecordsWithTimeLimit(lowbound,highbound,hostName);

        JSONArray result=new JSONArray();
        for(int i=0;i<6;i++){
            result.add(new JSONArray());
        }

        for(DispersedRecord dispersedRecord:dispersedRecordList){
            Timestamp timestamp = dispersedRecord.getTimestamp();

            result.getJSONArray(0).add(createNewValue(timestamp,dispersedRecord.getCpuUsage()));
            result.getJSONArray(1).add(createNewValue(timestamp,dispersedRecord.getMemUsage()));
            result.getJSONArray(2).add(createNewValue(timestamp,dispersedRecord.getDiskReadRates()));
            result.getJSONArray(3).add(createNewValue(timestamp,dispersedRecord.getDiskWriteRates()));
            result.getJSONArray(4).add(createNewValue(timestamp,dispersedRecord.getNetRecv()));
            result.getJSONArray(5).add(createNewValue(timestamp,dispersedRecord.getNetSent()));
        }
        return  result.toJSONString();
    }

    private JSONArray createNewValue(Object object1,Object object2){
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(object1);
        jsonArray.add(object2);
        return  jsonArray;
    }

    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @Override
    public String getDFPInfoTrend(String hostName, String diskSerial) {
        DFPRecord dfpRecord=diskFailureMapper.selectLatestDFPRecord(diskSerial);
        long recordTime=DiskPredict.getRecordTime("/DiskPredict/predict_data/"+hostName+"/"+hostName+".csv",hostName,diskSerial);
        if(dispersedHostMonitor.getDiskDFPState(hostName,diskSerial)||(dfpRecord!=null&&recordTime!=-1&&dfpRecord.timestamp.getTime()>recordTime)){
            // 从数据库中取出对应的记录

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("diskSerial",diskSerial);
            jsonObject.put("hostName",hostName);
            jsonObject.put("timestamp",dfpRecord.timestamp);
            jsonObject.put("predictProbability",dfpRecord.predictProbability);
            jsonObject.put("modelName",dfpRecord.modelName);
            return jsonObject.toJSONString();
        }
        else {
            JSONObject jsonObject=diskPredict(hostName,diskSerial);
            return jsonObject==null?null:jsonObject.toJSONString();
        }
    }



    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @Override
    public String getDFPInfoAll(boolean ifReturnCurrentRecords) {
        if(ifReturnCurrentRecords){
            //数据库中直接取出最新的预测结果
            JSONArray resultArray=new JSONArray();
            List<DFPRecord> result=diskFailureMapper.selectLatestDFPRecordList();
            for(DFPRecord dfpRecord:result){
                JSONObject tempObject=new JSONObject();
                tempObject.put("diskSerial",dfpRecord.diskSerial);
                tempObject.put("hostName",dfpRecord.hostName);
                tempObject.put("predictProbability",dfpRecord.predictProbability);
                tempObject.put("timestamp",dfpRecord.timestamp);
                tempObject.put("modelName",dfpRecord.modelName);
                resultArray.add(tempObject);
            }
            return resultArray.toJSONString();
        }
        else{
            JSONArray resultArray=new JSONArray();
            JSONArray[] diskListArray=new JSONArray[dispersedHostMonitor.hostInfoMap.size()];
            int i=0;
            for(JSONObject jsonObject:dispersedHostMonitor.hostInfoMap.values()){
                //这个循环到时候直接改成从数据库硬件信息表里面遍历，然后启动的时候根据hostInfoMap里面存活的节点，从数据库里面查出serial列表再循环
                diskListArray[i]=jsonObject.getJSONArray("diskInfoList");
                Iterator iterator=diskListArray[i].iterator();
                while(iterator.hasNext()){
                    JSONObject tempObject=(JSONObject) iterator.next();
                    long recordTime=DiskPredict.getRecordTime("/DiskPredict/predict_data/"+jsonObject.getString("hostName")+"/"+jsonObject.getString("hostName")+".csv",jsonObject.getString("hostName"),tempObject.getString("diskName"));
                    DFPRecord dfpRecord=diskFailureMapper.selectLatestDFPRecord(tempObject.getString("diskName"));
                    if(DiskPredict.checkLatestRecordExists("/DiskPredict/predict_data/"+jsonObject.getString("hostName")+"/"+jsonObject.getString("hostName")+".csv",jsonObject.getString("hostName"),tempObject.getString("diskName"))||dispersedHostMonitor.getDiskDFPState(jsonObject.getString("hostName"), tempObject.getString("diskName"))||(dfpRecord!=null&&recordTime!=-1&&dfpRecord.timestamp.getTime()>recordTime)){
                        if(dfpRecord==null){
                            System.out.println(tempObject.getString("diskName")+"doesn't has neither latest record in database and predict_data");
                            continue;
                        }
                        JSONObject dfpObject=new JSONObject();
                        dfpObject.put("diskSerial",dfpRecord.diskSerial);
                        dfpObject.put("hostName",dfpRecord.hostName);
                        dfpObject.put("predictProbability",dfpRecord.predictProbability);
                        dfpObject.put("modelName",dfpRecord.modelName);
                        dfpObject.put("timestamp",dfpRecord.timestamp);
                        resultArray.add(dfpObject);
                    }
                    else{
                        JSONObject predictResult=diskPredict(jsonObject.getString("hostName"),tempObject.getString("diskName"));//调用函数进行预测，同时需要检查是否存在记录
                        if(predictResult!=null)
                            resultArray.add(predictResult);
                    }
                }
            }
            return resultArray.toJSONString();
        }
    }
    //有待实现对预测范围的选择
    private JSONObject diskPredict(String hostName,String diskSerial){
        //TODO根据hostName的最新文件，检查diskName在不在条目中
        DiskPredict.Predict("\""+ System.getProperty("user.dir") + "/DiskPredict/predict_data/"+hostName +"\"",null);
        //在路径下读出所有的预测结果
        List<JSONObject> result=DiskPredict.getDiskPredictResult("/DiskPredict/result/"+sdf.format(new Date())+"/"+hostName+".csv",hostName);

        // 插入数据库，注意修改接受文件时同时修改下列状态
        JSONObject record=null;
        for(JSONObject jsonObject:result) {
            System.out.println(diskSerial);
            System.out.println(jsonObject);
            if(diskSerial.equals(jsonObject.getString("diskSerial"))){
                record=jsonObject;
            }
            diskFailureMapper.insertDiskDFPInfo(jsonObject.getString("diskSerial"), hostName, jsonObject.getTimestamp("timestamp"), jsonObject.getDoubleValue("predictProbability"), jsonObject.getString("modelName"));
            dispersedHostMonitor.setDiskDFPState(hostName, jsonObject.getString("diskSerial"), true);
        }
        return record;
    }
    private void train(){
        DiskPredict.DataPreProcess("\"2016\"",0,null);
        DiskPredict.GetTrainData("\"2016\"", 1.0f/3, 0.1f,null);
        JSONObject params = new JSONObject();
        params.put("max_depth", new int[]{10, 20, 30});
        params.put("max_features", new int[]{4, 7, 10});
        params.put("n_estimators", new int[]{10, 20, 30, 40});
        DiskPredict.Train("\"2016\"", "\"ST4000DM000\"", params,null);
        //TODO 模型输入数据库
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }

}
