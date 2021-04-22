package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HostMonitorBatchExecution implements Runnable{
    //---------- 配置信息
    //Config配置信息
    private Config configInfo;
    //Host 配置信息
    List<HostConfigInfo> hostConfigInfoList;

    //---------- 采样
    //ssh连接管理
    private SSHManager sshManager;
    //Host 信息
    private List<HostSampleData> hostSampleDataList;
    //采样间隔
    private int interval_ms;
    //线程
    private Thread newThread;


    //Init 单例
    private volatile static HostMonitorBatchExecution hostMonitor;
    public static HostMonitorBatchExecution getInstance(){
        if(hostMonitor==null){
            synchronized (HostMonitorBatchExecution.class){
                if(hostMonitor==null){
                    hostMonitor=new HostMonitorBatchExecution();
                }
            }
        }
        return hostMonitor;
    }
    //Init
    private HostMonitorBatchExecution(){
        //延迟时间默认为10 * 1000，单位：ms
        this(10 * 1000);
    }
    //Init
    public HostMonitorBatchExecution(int _interval_ms){
        //配置信息
        configInfo = new Config();
        //延迟时间 单位：ms
        interval_ms = _interval_ms;
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();

        //Host 配置信息
        hostConfigInfoList = configInfo.getHostConfigInfoList();

        //Host 信息 init
        hostSampleDataList = new ArrayList<>();
        JSONObject sampleDataFormat = configInfo.getSampleDataFormat();


        for(int i=0;i<hostConfigInfoList.size();i++){
            HostSampleData newHostSampleData = new HostSampleData(hostConfigInfoList.get(i).ip,sampleDataFormat);
            newHostSampleData.setAllValueInvalid();
            hostSampleDataList.add(newHostSampleData);
        }
    }

    //对所有Host采样
    public void sample(){
        //指令
        String command = configInfo.getSampleCommands();
        //遍历所有Host
        for(int i=0;i<hostConfigInfoList.size();i++){
            //采样返回结果
            List<String> commandResult = sshManager.runCommand(command, hostConfigInfoList.get(i));
            //
            HostSampleData currentHostSampleData = hostSampleDataList.get(i);
            //更新连接状态
            currentHostSampleData.sessionConnected = (commandResult.size()!=0);
            //设置所有字段无效
            currentHostSampleData.setAllValueInvalid();
            //遍历返回结果的字段并更新
            for(int j=0;j<commandResult.size();j++){
                //按格式拆分
                String currentResult = commandResult.get(j);
                String[] pair = currentResult.split(":");
                if(pair.length ==2){
                    String key = pair[0];
                    String value = pair[1];
                    //System.out.println("Key:"+key+",Value:"+value);
                    if(key.contains("Disk_")){
                        String[] segments = key.split("_");
                        //子key / 二级key
                        String subKey = segments[1];
                        //磁盘名称
                        String diskName = segments[2];
                        currentHostSampleData.sampleData.getJSONObject("Disk").put("valid",true);
                        JSONObject diskValueObject = currentHostSampleData.sampleData.getJSONObject("Disk").getJSONObject("value");
                        //若不存在则创建
                        if(!diskValueObject.containsKey(diskName)){
                            diskValueObject.put(diskName,configInfo.getDiskSampleDataFormat());
                        }
                        //更新值并设为有效
                        diskValueObject.getJSONObject(diskName).getJSONObject(subKey).put("value",value);
                        diskValueObject.getJSONObject(diskName).getJSONObject(subKey).put("valid",true);
                    }
                    else if(key.contains("Temperature_")){
                        String[] segments = key.split("_");
                        //子key / 二级key
                        String subKey = segments[1];
                        currentHostSampleData.sampleData.getJSONObject("Temperature").put("valid",true);
                        JSONObject temperatureObject = currentHostSampleData.sampleData.getJSONObject("Temperature").getJSONObject("value");
                        //若不存在则创建
                        if(!temperatureObject.containsKey(subKey)){
                            temperatureObject.put(subKey,configInfo.getTemperatureSampleDataFormat());
                        }
                        //更新值并设为有效
                        temperatureObject.getJSONObject(subKey).put("value",value);
                        temperatureObject.getJSONObject(subKey).put("valid",true);
                    }
                    else{
                        JSONObject currentJSONObject = currentHostSampleData.sampleData.getJSONObject(key);
                        currentJSONObject.put("value",value);
                        currentJSONObject.put("valid",true);
                    }
                }
            }
        }
    }

    //获取Host IP (Index)
    public String getHostIp(int index){
        return hostConfigInfoList.get(index).ip;
    }

    //获取Host IP
    public List<String> getHostIp(){
        List<String> arrayList=new ArrayList<>();
        for(HostConfigInfo hostConfigInfo: hostConfigInfoList){
            arrayList.add(hostConfigInfo.ip);
        }
        return arrayList;
    }

    //获取Host 采样 信息
    public JSONArray getHostSampleInfo(){
        JSONArray jsonArray=new JSONArray();
        for(int i = 0; i< hostSampleDataList.size(); i++){
            jsonArray.add(i,hostSampleDataList.get(i).sampleData);
        }
        return jsonArray;
    }

    //获取Host 状态
    public List<Boolean> getHostState(){
        List<Boolean> arrayList=new ArrayList<>();
        for(HostSampleData hostSampleData: hostSampleDataList){
            arrayList.add(hostSampleData.sessionConnected);
        }
        return arrayList;
    }

    //---------多线程执行
    //多线程运行
    @Override
    public void run() {
        //硬件设备信息
        while(true){
            //采样
            sample();
            try {
                Thread.sleep(interval_ms);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                if(newThread != null){
                    newThread.interrupt();
                    newThread = null;
                }
                break;
            }
        }

    }
    //开始线程
    public void startThread(){
        if(newThread == null){
            newThread = new Thread(this,"HostMonitor—thread");
            newThread.start();
        }
    }
    //终止线程
    public void stopThread(){
        try {
            Thread.sleep(interval_ms*2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            if(newThread != null){
                newThread.interrupt();
                newThread = null;
            }
        }
    }

    public static void main(String[] args) {
        HostMonitorBatchExecution hostMonitorBatchExecution = HostMonitorBatchExecution.getInstance();
        hostMonitorBatchExecution.sample();
        System.out.println(hostMonitorBatchExecution.getHostSampleInfo());
    }
}
