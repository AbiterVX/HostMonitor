package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.ProcessMapper;
import com.hust.hostmonitor_data_collector.dao.RecordMapper;
import com.hust.hostmonitor_data_collector.dao.entity.Record;
import com.hust.hostmonitor_data_collector.utils.HostMonitorBatchExecution;
import com.hust.hostmonitor_data_collector.utils.HostProcessSampleData;
import com.hust.hostmonitor_data_collector.utils.HostSampleData;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

public class DataServiceImpl implements DataService{
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
            //采样
            System.out.println("Host Sample");
            hostMonitorBE.sample();
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
    public DataServiceImpl(){
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
        for(Iterator<Vector<HostProcessSampleData>> outerIt=processVector.iterator(); outerIt.hasNext();index++){
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
    public String getHostIp() {
        return JSONArray.parseArray(JSON.toJSONString(hostMonitorBE.getHostIp())).toJSONString();
    }

    @Override
    public String getHostState() {
        return JSONArray.parseArray(JSON.toJSONString(hostMonitorBE.getHostState())).toJSONString();
    }

    @Override
    public String getHostHardwareInfo() {
        JSONArray result=new JSONArray();
        JSONArray dataSource=hostMonitorBE.getHostSampleInfo();
        for(int i=0;i<dataSource.size();i++){
            JSONObject temp=new JSONObject();
            JSONObject sampleData=dataSource.getJSONObject(i);
            temp.put("os",sampleData.getJSONObject("OS").get("value"));
            temp.put("MemorySize",sampleData.getJSONObject("MemTotal").getString("value"));
            temp.put("CpuType",sampleData.getJSONObject("CpuType").get("value"));
            temp.put("DiskTotalSize",sampleData.getJSONObject("DiskTotalSize").getString("value"));
            result.add(temp);
        }
        return result.toJSONString();
    }
    /**
     * 功能：获取Host信息-近期
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *      hour：小时数
     * 格式：[{"TimeStamp":1619319687,
     *        "CpuUsage":10,
     *        "MemoryUsage":11,
     *        "DiskOccupancyUsage":12,
     *
     *        "Disk":{
     *            "vda":{
     *                "Util":44
     *                "Iops":5.1
     *                "Read":89.2
     *                "Write":22.5
     *            },
     *            ...
     *        },
     *        "NetSend":200,
     *        "NetReceive":300,
     *        "TcpEstablished":16,
     *        "Temperature":{
     *            "1": 66,
     *            ...
     *        },
     *        "Power":178,
     *       },
     *       ...]
     */
    @Override
    public String getHostInfoRealTime() {
        Random r=new Random();
        JSONArray result=new JSONArray();
        JSONArray dataSource=hostMonitorBE.getHostSampleInfo();
        Timestamp realTime=new Timestamp(System.currentTimeMillis());
        for(int i=0;i<dataSource.size();i++){
            JSONObject temp=new JSONObject();
            JSONObject sampleData=dataSource.getJSONObject(i);
            temp.put("Timestamp",realTime);
            temp.put("ip",hostMonitorBE.getHostIp(i));

            temp.put("CpuIdle",sampleData.getJSONObject("CpuIdle").get("value"));
            temp.put("MemoryUsage",""+(sampleData.getJSONObject("MemTotal").getInteger("value")-sampleData.getJSONObject("MemAvailable").getInteger("value")));
            temp.put("DiskOccupancyUsage",sampleData.getJSONObject("DiskOccupancyUsage").getString("value"));
            //temp.put("Disk",sampleData.getJSONObject("Disk").getJSONObject("value"));
            try {
                String toPocesse = sampleData.getJSONObject("NetSend").getString("value");

                temp.put("NetSend", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)));
                toPocesse = sampleData.getJSONObject("NetReceive").getString("value");
                temp.put("NetReceive", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)));
            } catch (NumberFormatException e) {
                temp.put("NetSend", 0);
                temp.put("NetReceive",0);
            }

            temp.put("TcpEstablished",sampleData.getJSONObject("TcpEstablished").get("value"));
            //temp.put("Temperature",sampleData.getJSONObject("Temperature").toJSONString());
            int iops=r.nextInt(11);
            temp.put("iops",iops);
            temp.put("Power",sampleData.getJSONObject("Power").get("value"));

            System.out.println(temp.toJSONString());
            result.add(temp);
        }


        return result.toJSONString();
    }

    @Override
    public String getHostInfoRecent(int index, int hour) {
        String ip=hostMonitorBE.getHostIp(index);
        long ms=hour*3600*1000;
        long nowtime=System.currentTimeMillis();

        JSONArray result=new JSONArray();
        List<Record> queryResult=recordMapper.queryRecordsWithTimeLimit(new Timestamp(nowtime-ms),new Timestamp(nowtime),ip);
        for(Record record:queryResult){
            JSONObject temp=new JSONObject();
            temp.put("ip",ip);
            temp.put("Timestamp",record.getTimestamp());
            temp.put("CpuIdle",record.getCpuIdle());
            temp.put("MemoryUsage",record.getMemTotal()-record.getMemAvailable());
            temp.put("DiskOccupencyUsage",record.getDiskOccupancyUsage());
            temp.put("NetSend", record.getNetSend());
            temp.put("NetReceive", record.getNetReceive());
            temp.put("TcpEstablished",record.getTcpEstablished());
            temp.put("Power",record.getPower());
            temp.put("iops",record.getIops());
            result.add(temp);
        }
        return result.toJSONString();
    }

    @Override
    public String getHostInfoField(int index, int hour, HostInfoFieldType field) {
        String ip=hostMonitorBE.getHostIp(index);
        long ms=hour*3600*1000;
        long nowtime=System.currentTimeMillis();
        JSONArray result=new JSONArray();
        List<Record> queryResult=recordMapper.queryRecordsWithTimeLimit(new Timestamp(nowtime-ms),new Timestamp(nowtime),ip);
        switch(field){
            case CpuIdle:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("CpuIdle",record.getCpuIdle());
                    result.add(temp);
                }
                break;
            case MemoryUsage:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("MemoryUsage",record.getMemTotal()-record.getMemAvailable());
                    result.add(temp);
                }
                break;
            case DiskOccupancyUsage:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("DiskOccupencyUsage",record.getDiskOccupancyUsage());
                    result.add(temp);
                }
                break;
            case NetSend:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("NetSend", record.getNetSend());
                    result.add(temp);
                }
                break;
            case NetReceive:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("NetReceive", record.getNetReceive());
                    result.add(temp);
                }
                break;
            case TcpEstablished:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("TcpEstablished",record.getTcpEstablished());
                    result.add(temp);
                }
                break;
            case Power:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("Power",record.getPower());
                    result.add(temp);
                }
                break;
            case Disk_Iops:
                for(Record record:queryResult){
                    JSONObject temp=new JSONObject();
                    temp.put("ip",ip);
                    temp.put("Timestamp",record.getTimestamp());
                    temp.put("iops",record.getIops());
                    result.add(temp);
                }
                break;
            default:
                break;
        }
        return result.toJSONString();
    }
    /**
     * 功能：获取Host 进程信息-最近
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *
     * 格式：[{"uid":1,
     *        "pid":12,
     *        "readKbps": 452.7,
     *        "writeKbps": 142.3,
     *        "command": “java”,
     *       },
     *       ...]
     */
    @Override
    public String getHostProcessInfoRealTime(int index) {
        Vector<Vector<HostProcessSampleData>> processVector=hostMonitorBE.getHostProcessSampleDataList();

        JSONArray result=new JSONArray();
        Iterator<HostProcessSampleData> it=processVector.get(index).iterator();
        while(it.hasNext()){
            JSONObject temp=new JSONObject();
            HostProcessSampleData tempData=it.next();
            temp.put("uid",tempData.uid);
            temp.put("pid",tempData.pid);
            temp.put("readKbps",tempData.readKbps);
            temp.put("writeKbps",tempData.writeKbps);
            temp.put("command",tempData.command);
            result.add(temp);
        }
        return result.toJSONString();
    }

    @Override
    public String getHostIOTestInfoRealTime() {
        return "";
    }

}
