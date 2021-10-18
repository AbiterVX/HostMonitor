package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.ProcessMapper;
import com.hust.hostmonitor_data_collector.dao.RecordMapper;
import com.hust.hostmonitor_data_collector.utils.CentralizedMonitor.HostMonitorBatchExecution;
import com.hust.hostmonitor_data_collector.utils.CentralizedMonitor.HostProcessSampleData;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

public class CentralizedDataCollectorService implements DataCollectorService{
    @Autowired
    RecordMapper recordMapper;
    @Autowired
    ProcessMapper processMapper;

    //HostMonitor
    private final HostMonitorBatchExecution hostMonitorBE = HostMonitorBatchExecution.getInstance();

    //采样间隔,单位ms
    private final int sampleIntervalMS = 10* 1000;
    private final int sampleProcessIntervalMS = 5* 1000;
    //存储偏移,单位ms
    private final int sampleStoreDelayMS = sampleIntervalMS/2;
    private final int sampleProcessStoreDelayMS = sampleProcessIntervalMS/2;
    //定时器
    private Timer mainTimer = new Timer();
    //定时器任务
    private final TimerTask hostSampleTask = new TimerTask() {
        @Override
        public void run() {
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


    private final TimerTask hostProcessSampleTask = new TimerTask() {
        @Override
        public void run() {
            System.out.println("Host Process Sample");
            hostMonitorBE.sampleProcess();
            try {
                Thread.sleep(sampleProcessStoreDelayMS);
                storeProcessSampleData();
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println("[Thread Sleep Error]: In TimerTask run()");
            }
        }
    };

    //Init
    public CentralizedDataCollectorService(){
        System.out.println("启动:DataServiceImpl");

        //每隔sampleIntervalMS,执行一次mainTimerTask
        mainTimer.schedule(hostSampleTask,sampleProcessIntervalMS/2,sampleIntervalMS);
        mainTimer.schedule(hostProcessSampleTask,0,sampleProcessIntervalMS);
    }

    //存储新采样的数据
    private void storeSampleData(){
        JSONArray dataSource=hostMonitorBE.getHostSampleInfo();
        List<Boolean> hostState=hostMonitorBE.getHostState();
        List<String> ipList=hostMonitorBE.getHostIp();
        //netsend netreceive格式需要修改
        System.out.println(dataSource.size());
        for(int i=0;i<dataSource.size();i++){
            JSONObject sampleData=dataSource.getJSONObject(i);
            System.out.println(hostState.get(i));
            if(hostState.get(i)) {

                Double NetSend,NetReceive;
                try {
                    String toPocesse = sampleData.getJSONObject("NetSend").getString("value");
                    NetSend=Double.parseDouble(toPocesse.substring(0, toPocesse.length() - 1));
                    toPocesse = sampleData.getJSONObject("NetReceive").getString("value");
                    NetReceive=Double.parseDouble(toPocesse.substring(0, toPocesse.length() - 1));
                } catch (NumberFormatException e) {
                    NetSend=Double.parseDouble("0.0");
                    NetReceive=Double.parseDouble("0.0");
                }
                recordMapper.insertNewRecord(ipList.get(i),new Timestamp(System.currentTimeMillis()),NetReceive,
                        NetSend, sampleData.getJSONObject("MemTotal").getInteger("value"),
                        sampleData.getJSONObject("MemFree").getInteger("value"),
                        sampleData.getJSONObject("MemAvailable").getInteger("value"),
                        sampleData.getJSONObject("Buffers").getDouble("value"),
                        sampleData.getJSONObject("Cached").getDouble("value"),
                        sampleData.getJSONObject("TcpEstablished").getInteger("value"),
                        sampleData.getJSONObject("DiskTotalSize").getDouble("value"),
                        sampleData.getJSONObject("DiskOccupancyUsage").getDouble("value"),
                        sampleData.getJSONObject("CpuIdle").getDouble("value"),
                        sampleData.getJSONObject("Power").getDouble("value"),
                        Double.parseDouble("22.0"),
//                        sampleData.getJSONObject("Disk").getJSONObject("value").getJSONObject("value").getInteger("Iops"),
//                        sampleData.getJSONObject("Disk").getJSONObject("value").getJSONObject("value").getString("Type"),
//                        sampleData.getJSONObject("Disk").getJSONObject("value").getJSONObject("value").getDouble("ReadRates"),
//                        sampleData.getJSONObject("Disk").getJSONObject("value").getJSONObject("value").getDouble("WriteRates"),
//                        sampleData.getJSONObject("Disk").getJSONObject("value").getJSONObject("value").getDouble("Utils"));
                        Integer.parseInt("13"),
                        "NoValue",
                        Double.parseDouble("33.0"),
                        Double.parseDouble("44.0"),
                        Double.parseDouble("55.0"));
            }
        }
        System.out.println("["+Thread.currentThread().getName()+"]:Insert into database");

    }

    //存储新采样的数据-线程
    private void storeProcessSampleData(){
        Vector<Vector<HostProcessSampleData>> processVector=hostMonitorBE.getHostProcessSampleDataList();
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        int index=0;
        for(Iterator<Vector<HostProcessSampleData>> outerIt = processVector.iterator(); outerIt.hasNext(); index++){
            Iterator<HostProcessSampleData> it=outerIt.next().iterator();
            String ip=hostMonitorBE.getHostIp(index);
            while(it.hasNext()){
                HostProcessSampleData tempData=it.next();
                processMapper.insertProcessRecord(ip,timestamp,tempData.pid,tempData.uid,tempData.readKbps,tempData.writeKbps,tempData.command);
            }
        }
    }


    //----------对外接口----------

    @Override
    public String getDashboardSummary() {
        return null;
    }

    @Override
    public String getHostInfoDashboardAll() {
        return null;
    }

    @Override
    public String getHostInfoDetail(String hostName) {
        return null;
    }

    @Override
    public String getDiskInfoAll() {
        return null;
    }

    @Override
    public String getDiskInfo(String hostName) {
        return null;
    }

    @Override
    public String getHostInfoDetailTrend(String hostName) {
        return null;
    }

    @Override
    public String getDFPInfoTrend(String hostIp, String diskName) {
        return null;
    }

    @Override
    public String getDFPInfoAll() {
        return null;
    }

    @Override
    public void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion, JSONObject extraParams, String operatorID) {

    }

    @Override
    public boolean userAuthoirtyCheck(String user, String password, int checkLevel) {
        return false;
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }

    @Override
    public String getDFPTrainList() {
        return null;
    }

    @Override
    public List<Float> getTrainProgress() {
        return null;
    }

    @Override
    public String getDFPSummary() {
        return null;
    }
}
