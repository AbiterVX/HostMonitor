package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostMonitorBatchExecution{
    //---------- 配置信息
    //Config配置信息
    private Config configInfo = Config.getInstance();
    //Host 配置信息
    List<HostConfigInfo> hostConfigInfoList;

    //---------- 采样
    //ssh连接管理
    private SSHManager sshManager;
    //Host 信息
    private List<HostSampleData> hostSampleDataList;

    //Host Process 信息
    private Vector<Vector<HostProcessSampleData>> hostProcessSampleDataList;

    //线程池
    ExecutorService executor;

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
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();

        //Host 配置信息
        hostConfigInfoList = configInfo.getHostConfigInfoList();

        //Host 信息 init
        hostSampleDataList = new ArrayList<>();
        hostProcessSampleDataList = new Vector<>();
        JSONObject sampleDataFormat = configInfo.getSampleDataFormat();


        for(int i=0;i<hostConfigInfoList.size();i++){
            HostSampleData newHostSampleData = new HostSampleData(hostConfigInfoList.get(i).ip,sampleDataFormat);
            newHostSampleData.setAllValueInvalid();
            hostSampleDataList.add(newHostSampleData);

            Vector<HostProcessSampleData> tempList = new Vector<>();
            hostProcessSampleDataList.add(tempList);
        }

        //线程池大小设为Host个数*2
        executor= Executors.newFixedThreadPool(hostConfigInfoList.size()*2);
    }

    //----------采样----------
    //Host采样
    public void sample(){
        //对所有Host异步采样
        for(int i=0;i<hostConfigInfoList.size();i++){
            int index = i;
            executor.submit(() -> {
                sampleHost(index);
            });
        }
    }
    //Host采样-单个
    public void sampleHost(int index){
        //采样返回结果
        List<String> commandResult = sshManager.runCommand(configInfo.getSampleCommands(), hostConfigInfoList.get(index));
        //System.out.println("Index:"+index);
        //System.out.println(commandResult);

        HostSampleData currentHostSampleData = hostSampleDataList.get(index);
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

    //Host采样-进程
    public void sampleProcess(){
        //对所有Host异步采样
        for(int i=0;i<hostConfigInfoList.size();i++){
            int index = i;
            executor.submit(() -> {
                //sampleHost(index);
                sampleHostProcess(index);
            });
        }
    }
    //Host采样-进程-单个
    private void sampleHostProcess(int index){
        //采样返回结果
        List<String> commandResult = sshManager.runCommand(configInfo.getProcessSampleCommand(), hostConfigInfoList.get(index));
        if(commandResult.size() == 0){
            //System.out.println("NULL,Index:"+index);
        }
        else{
            //System.out.println("Index:"+index);
            //System.out.println(commandResult);
            Vector<HostProcessSampleData> processSampleDataList = new Vector<>();
            for(int i=0;i<commandResult.size();i++){
                String[] segments = commandResult.get(i).split("\\s+");
                String uid = segments[1];
                String pid = segments[2];
                String readKbps = segments[3];
                String writeKbps = segments[4];
                String command = segments[7];

                HostProcessSampleData hostProcessSampleData = new HostProcessSampleData(uid,pid,readKbps,writeKbps,command);
                processSampleDataList.add(hostProcessSampleData);
                /*System.out.println("uid:"+uid);
                System.out.println("pid:"+pid);
                System.out.println("readKbps:"+readKbps);
                System.out.println("writeKbps:"+writeKbps);
                System.out.println("command:"+command);*/
            }
            hostProcessSampleDataList.set(index,processSampleDataList);
        }
    }


    //Host采样-测试
    public void sampleTest(int index){
        List<String> commandResult = sshManager.runCommand(configInfo.getTestCommand(), hostConfigInfoList.get(index));
        System.out.println("Index:"+index);
        System.out.println(commandResult);
    }


    //----------数据获取----------

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

    public Vector<Vector<HostProcessSampleData>> getHostProcessSampleDataList() {
        return hostProcessSampleDataList;
    }

    //获取Host 状态
    public List<Boolean> getHostState(){
        List<Boolean> arrayList=new ArrayList<>();
        for(HostSampleData hostSampleData: hostSampleDataList){
            arrayList.add(hostSampleData.sessionConnected);
        }
        return arrayList;
    }

    //----------初始化----------

    //初始化Host环境-仅安装一次
    public void initEnvironment(){
        //对所有Host异步采样
        for(int i=0;i<hostConfigInfoList.size();i++){
            int index = i;
            executor.submit(() -> {
                initHostEnvironment(index);
            });
        }
    }
    //初始化Host环境-仅安装一次-某一Host
    public void initHostEnvironment(int index) {
        //采样返回结果
        List<String> commandResult = sshManager.runCommand(configInfo.getInitEnvironmentCommand(), hostConfigInfoList.get(index));
    }


    public static void main(String[] args) {
        HostMonitorBatchExecution hostMonitorBatchExecution = HostMonitorBatchExecution.getInstance();
        hostMonitorBatchExecution.sampleTest(3);
        //hostMonitorBatchExecution.sample();
        //hostMonitorBatchExecution.sampleProcess();
        //System.out.println(hostMonitorBatchExecution.getHostSampleInfo());
    }
}
