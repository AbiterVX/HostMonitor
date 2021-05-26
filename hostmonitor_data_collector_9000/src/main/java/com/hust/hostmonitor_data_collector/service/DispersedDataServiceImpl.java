package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.DiskPredict.Disk_Predict;
import com.hust.hostmonitor_data_collector.dao.DispersedMapper;
import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import com.hust.hostmonitor_data_collector.utils.DispersedHostMonitor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


public class DispersedDataServiceImpl implements DispersedDataService{
    @Autowired
    DispersedMapper dispersedMapper;

    private final long sampleInterval=10000;
    private final double cpuThreshold=0.1;
    private DispersedHostMonitor dispersedHostMonitor;
    public final int sampleStoreDelayMS=500;
    private String inputPath;
    private String outputPath;
    //定时器
    private long DiskPredictTime;
    private Timer mainTimer = new Timer();
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
                        tempObject.getTimestamp("lastUpdateTime"),memUsage,
                        tempObject.getDouble("cpuUsage"),
                        tempObject.getDouble("netReceiveSpeed"),
                        tempObject.getDouble("netSendSpeed"),DiskReadRates,DiskWriteRates);
                entry.getValue().put("hasPersistent",true);
                System.out.println("[Database]Insert a record.");
            }
        }
    }
    public DispersedDataServiceImpl(){
        inputPath=System.getProperty("user.dir")+"/DiskPredictData/input/";
        outputPath=System.getProperty("user.dir")+"/DiskPredictData/output/";
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
        JSONArray[] component=new JSONArray[6];
        for(int i=0;i<6;i++){
            component[i]=new JSONArray();
        }
        for(DispersedRecord dispersedRecord:dispersedRecordList){
            JSONArray[] jsonArrays=new JSONArray[6];
            jsonArrays[0]=new JSONArray();
            jsonArrays[0].add(dispersedRecord.getTimestamp());
            jsonArrays[0].add(dispersedRecord.getCpuUsage());
            component[0].add(jsonArrays);

            jsonArrays[1]=new JSONArray();
            jsonArrays[1].add(dispersedRecord.getTimestamp());
            jsonArrays[1].add(dispersedRecord.getMemUsage());
            component[1].add(jsonArrays[1]);

            jsonArrays[2]=new JSONArray();
            jsonArrays[2].add(dispersedRecord.getTimestamp());
            jsonArrays[2].add(dispersedRecord.getDiskReadRates());
            component[2].add(jsonArrays[2]);

            jsonArrays[3]=new JSONArray();
            jsonArrays[3].add(dispersedRecord.getTimestamp());
            jsonArrays[3].add(dispersedRecord.getDiskWriteRates());
            component[3].add(jsonArrays[3]);

            jsonArrays[4]=new JSONArray();
            jsonArrays[4].add(dispersedRecord.getTimestamp());
            jsonArrays[4].add(dispersedRecord.getNetRecv());
            component[4].add(jsonArrays[4]);

            jsonArrays[5]=new JSONArray();
            jsonArrays[5].add(dispersedRecord.getTimestamp());
            jsonArrays[5].add(dispersedRecord.getNetSent());
            component[5].add(jsonArrays[5]);
        }
        for(int i=0;i<6;i++){
            result.add(component[i]);
        }
        return  result.toJSONString();
    }



    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @Override
    public String getDFPInfoTrend(String hostName, String diskName) {
        if(dispersedHostMonitor.dfpInfoList.containsKey(hostName+":"+diskName)){
            JSONObject resultObject=dispersedHostMonitor.dfpInfoList.get(hostName+":"+diskName);
            //目前diskpredict函数还未取子集，取子集后需要修改
            long modifiedTime=new File(hostName+"-data.csv").lastModified();
            if(resultObject.getLong("predictTime")>modifiedTime)
                return resultObject.toJSONString();
            else {
                dispersedHostMonitor.setDiskDFPState(hostName,diskName,false);
                //应该包含在内
                diskPredict();
            }
            resultObject=dispersedHostMonitor.dfpInfoList.get(hostName+":"+diskName);
            return resultObject.toJSONString();
        }
        else {
            diskPredict();
            JSONObject resultObject=dispersedHostMonitor.dfpInfoList.get(hostName+":"+diskName);
            return resultObject==null? null:resultObject.toJSONString();

            }

    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     */
    @Override
    public String getDFPInfoAll() {
        diskPredict();//全范围的
        JSONArray resultArray=new JSONArray();
        JSONArray[] diskListArray=new JSONArray[dispersedHostMonitor.hostInfoMap.size()];

        int i=0;
        for(JSONObject jsonObject:dispersedHostMonitor.hostInfoMap.values()){
            diskListArray[i]=jsonObject.getJSONArray("diskInfoList");
            Iterator iterator=diskListArray[i].iterator();
            while(iterator.hasNext()){
                JSONObject tempObject=(JSONObject) iterator.next();
                if(tempObject.getBoolean("hasLatestDFPRecord")){
                    String queryResult=getDFPInfoTrend(jsonObject.getString("hostName"),tempObject.getString("diskName"));
                    if(queryResult!=null)
                        resultArray.add(JSONObject.parse(queryResult));
                }
            }
        }

        return resultArray.toJSONString();
    }
    private void diskPredict(){
        long time=System.currentTimeMillis();
        ArrayList<String> contentData=new ArrayList<>();

        for(String string: dispersedHostMonitor.hostInfoMap.keySet()){
            File file=new File(inputPath+string+"-data.csv");
            if(file.exists()){

                try {
                    BufferedReader br=new BufferedReader(new FileReader(file));
                    String str;
                    while((str= br.readLine())!=null){
                        contentData.add(str);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Disk_Predict.diskSampleDataIntegration("/DiskPredictData/input/IntegratedData.csv",contentData);
        Disk_Predict disk_predict=new Disk_Predict("IntegratedData.csv","PredictResult.csv");
        //Disk_Predict disk_predict=new Disk_Predict("testInput.csv","testOutput.csv");
        DiskPredictTime=System.currentTimeMillis();
        List<JSONObject> result=Disk_Predict.getDiskPredictResult("/DiskPredictData/output/PredictResult.csv",time);
        for(JSONObject jsonObject:result) {
            if(dispersedHostMonitor.dfpInfoList.containsKey(jsonObject.getString("hostName")+":"+jsonObject.getString("diskName"))){
                JSONObject tempObject=dispersedHostMonitor.dfpInfoList.get(jsonObject.getString("hostName")+":"+jsonObject.getString("diskName"));
                long modifiedTime=new File(tempObject.getString("hostName")+"-data.csv").lastModified();
                if(tempObject.getLong("predictTime")>modifiedTime)
                    continue;
                else{
                    dispersedHostMonitor.dfpInfoList.put(jsonObject.getString("hostName")+":"+jsonObject.getString("diskName"),jsonObject);
                    dispersedHostMonitor.setDiskDFPState(jsonObject.getString("hostName"),jsonObject.getString("diskName"),true);
                }

            }
            else{
                dispersedHostMonitor.dfpInfoList.put(jsonObject.getString("hostName")+":"+jsonObject.getString("diskName"),jsonObject);
                dispersedHostMonitor.setDiskDFPState(jsonObject.getString("hostName"),jsonObject.getString("diskName"),true);
            }
        }
    }
    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }
}
