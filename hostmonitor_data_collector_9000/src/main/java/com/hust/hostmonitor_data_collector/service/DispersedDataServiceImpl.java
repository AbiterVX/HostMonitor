package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.DispersedMapper;
import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import com.hust.hostmonitor_data_collector.utils.DispersedHostMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class DispersedDataServiceImpl implements DispersedDataService{
    @Autowired
    DispersedMapper dispersedMapper;

    private final long sampleInterval=10000;
    private DispersedHostMonitor dispersedHostMonitor;
    public final int sampleStoreDelayMS=500;

    //定时器
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
                double memUsage=tempObject.getLong("memoryUsedSize")*1.0/tempObject.getLong("memoryTotalSize");
                double DiskReadRates=0;
                double DiskWriteRates=0;
                for(int i=0;i<tempObject.getJSONArray("diskInfoList").size();i++){
                    DiskWriteRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskWriteSpeed");
                    DiskReadRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskReadSpeed");
                }
                if( tempObject.getDouble("cpuUsageAverage")==0){
                    continue;
                }
                dispersedMapper.insertNewRecord(tempObject.getString("hostName"),tempObject.getString("ip"),
                        tempObject.getTimestamp("lastUpdateTime"),memUsage,
                        tempObject.getDouble("cpuUsageAverage"),
                        tempObject.getDouble("netReceiveSpeed"),
                        tempObject.getDouble("netSendSpeed"),DiskReadRates,DiskWriteRates);
                entry.getValue().put("hasPersistent",true);
                System.out.println("[Database]Insert a record.");
            }
        }
    }
    public DispersedDataServiceImpl(){
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

//    @Override
//    public String getDiskInfoAll() {
//        return null;
//    }
//
//    @Override
//    public String getDiskInfo(String hostName) {
//        return null;
//    }

    //时间段 cpuusage,memory,
    @Override
    public String getHostInfoDetailTrend(String hostName) {
        int hours=2;
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

    @Override
    public String getDFPInfoTrend(String hostName, String diskName) {
        return null;
    }

    @Override
    public String getDFPInfoAll() {
        return null;
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }
}
