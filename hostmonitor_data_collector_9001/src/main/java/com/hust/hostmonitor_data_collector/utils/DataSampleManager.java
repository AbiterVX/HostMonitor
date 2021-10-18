package com.hust.hostmonitor_data_collector.utils;


import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.JschSSHManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.SSHManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class DataSampleManager {
    //ssh连接管理
    private SSHManager sshManager;
    //Config配置信息
    private ConfigDataManager configDataManager= ConfigDataManager.getInstance();
    //线程池
    ExecutorService executor;

    //Host 配置信息
    List<HostConfigData> hostList;

    //单例
    private volatile static DataSampleManager dataSampleManager;
    public static DataSampleManager getInstance(){
        if(dataSampleManager==null){
            synchronized (DataSampleManager.class){
                if(dataSampleManager==null){
                    dataSampleManager=new DataSampleManager();
                }
            }
        }
        return dataSampleManager;
    }
    private DataSampleManager(){
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();
        //使用SSH监控的节点列表
        hostList = configDataManager.getSSHConfigHostList();
        //线程池(大小设为Host个数*2)
        executor= Executors.newFixedThreadPool(hostList.size()*2);
    }

    public JSONObject sampleHostHardwareData(int index){
        return new JSONObject();
    }

    public JSONObject sampleHostData(int index){
        return new JSONObject();
    }

    public JSONObject sampleHostProcess(int index){
        return new JSONObject();
    }

    public JSONObject ioTest(int index){
        return new JSONObject();
    }



}
