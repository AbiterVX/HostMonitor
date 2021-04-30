package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hust.hostmonitor_data_collector.dao.Dao_disk;
import com.hust.hostmonitor_data_collector.dao.Dao_record;
import com.hust.hostmonitor_data_collector.utils.HostMonitorBatchExecution;
import com.hust.hostmonitor_data_collector.utils.HostProcessSampleData;
import com.hust.hostmonitor_data_collector.utils.HostSampleData;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

public class DataServiceImpl implements DataService{
    @Autowired
    Dao_record dao_record;
    @Autowired
    Dao_disk dao_disk;


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
        //Todo
    }

    //存储新采样的数据-线程
    private void storeProcessSampleData(){

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
            temp.put("MemorySize",sampleData.getJSONObject("MemTotal").getInteger("value")/1024);
            temp.put("CpuType",sampleData.getJSONObject("CpuType").get("value"));
            temp.put("DiskTotalSize",sampleData.getJSONObject("DiskTotalSize").getInteger("value")/1024);
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
            temp.put("CpuIdle",100-sampleData.getJSONObject("CpuIdle").getDouble("value"));
            temp.put("MemoryUsage",(sampleData.getJSONObject("MemTotal").getInteger("value")-sampleData.getJSONObject("MemAvailable").getInteger("value"))/1024);
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
        JSONArray result=new JSONArray();
        JSONArray dataSource=hostMonitorBE.getHostSampleInfo();
        JSONObject sampleData=dataSource.getJSONObject(index);
        long nowtime=System.currentTimeMillis();
        Timestamp realTime=new Timestamp(nowtime);
        Random r=new Random();
        int RandomTotalNumber=r.nextInt(20)+3;
        JSONObject temp=new JSONObject();
        temp.put("ip",ip);
        temp.put("Timestamp",realTime);
        temp.put("CpuIdle",100-sampleData.getJSONObject("CpuIdle").getDouble("value"));
        temp.put("MemoryUsage",(sampleData.getJSONObject("MemTotal").getInteger("value")-sampleData.getJSONObject("MemAvailable").getInteger("value"))/1024);
        temp.put("DiskOccupancyUsage",sampleData.getJSONObject("DiskOccupancyUsage").getInteger("value"));
        //temp.put("Disk",sampleData.getJSONObject("Disk").getJSONObject("value").toJSONString());
        String toPocesse = sampleData.getJSONObject("NetSend").getString("value");
        try {

            temp.put("NetSend", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)));
            toPocesse = sampleData.getJSONObject("NetReceive").getString("value");
            temp.put("NetReceive", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)));
        }
        catch (NumberFormatException e) {
            temp.put("NetSend", 0);
            temp.put("NetReceive",0);
        }
        temp.put("TcpEstablished",sampleData.getJSONObject("TcpEstablished").getInteger("value"));
        //temp.put("Temperature",sampleData.getJSONObject("Temperature").toJSONString());
        temp.put("Power",sampleData.getJSONObject("Power").get("value"));
        int iops=r.nextInt(11);
        temp.put("iops",iops);
        result.add(temp);
        int randomnumber=r.nextInt(RandomTotalNumber);
        for(int i=0;i<RandomTotalNumber-1&&randomnumber>0;i++){
            JSONObject later=new JSONObject();
            later.put("Timestamp",new Timestamp(nowtime-ms*randomnumber/RandomTotalNumber));
            later.put("ip",ip);
            later.put("CpuIdle",100-sampleData.getJSONObject("CpuIdle").getDouble("value")*randomnumber/(RandomTotalNumber*2));
            later.put("MemoryUsage",(sampleData.getJSONObject("MemTotal").getInteger("value")-sampleData.getJSONObject("MemAvailable").getInteger("value"))*randomnumber/(RandomTotalNumber*1024));
            later.put("DiskOccupancyUsage",sampleData.getJSONObject("DiskOccupancyUsage").getInteger("value")*randomnumber/(RandomTotalNumber));
            //later.put("Disk",sampleData.getJSONObject("Disk").getJSONObject("value").toJSONString());
            try {
                toPocesse = sampleData.getJSONObject("NetSend").getString("value");
                later.put("NetSend", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)) * randomnumber / (RandomTotalNumber * 2));
                toPocesse = sampleData.getJSONObject("NetReceive").getString("value");
                later.put("NetReceive", Integer.parseInt(toPocesse.substring(0, toPocesse.length() - 1)) * randomnumber / (RandomTotalNumber * 3));
            }
            catch (NumberFormatException e) {
                later.put("NetSend", 0);
                later.put("NetReceive",0);
            }
            later.put("TcpEstablished",sampleData.getJSONObject("TcpEstablished").get("value"));
            //later.put("Temperature",sampleData.getJSONObject("Temperature").toJSONString());
            iops=r.nextInt(11);
            later.put("iops",iops);
            later.put("Power",sampleData.getJSONObject("Power").get("value"));
            randomnumber=r.nextInt(randomnumber);
            result.add(later);
        }
        return result.toJSONString(SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public String getHostInfoField(int index, int hour, HostInfoFieldType field) {
        long ms=hour*3600*1000;
        String ip=hostMonitorBE.getHostIp(index);
        JSONArray result=new JSONArray();
        JSONArray dataSource=hostMonitorBE.getHostSampleInfo();
        JSONObject sampleData=dataSource.getJSONObject(index);
        long nowtime=System.currentTimeMillis();
        Timestamp realTime=new Timestamp(nowtime);
        Random r=new Random();
        int RandomTotalNumber=r.nextInt(20)+3;
        JSONObject temp=new JSONObject();
        temp.put("Timestamp",realTime);
        temp.put("ip",ip);
        temp.put(field.value(),sampleData.getJSONObject(field.value()).getDouble("value"));
        result.add(temp);
        int randomnumber=r.nextInt(RandomTotalNumber);
        for(int i=0;i<RandomTotalNumber-1&&randomnumber>0;i++){
            JSONObject later=new JSONObject();
            later.put("ip",ip);
            later.put("Timestamp",new Timestamp(nowtime-ms*randomnumber/RandomTotalNumber));
            later.put(field.value(),sampleData.getJSONObject(field.value()).getDouble("value")*randomnumber/(RandomTotalNumber*2));
            randomnumber=r.nextInt(randomnumber);
            result.add(later);
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
        return "null";
    }

}
