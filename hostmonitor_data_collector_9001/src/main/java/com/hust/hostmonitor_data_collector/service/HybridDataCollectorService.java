package com.hust.hostmonitor_data_collector.service;


import com.alibaba.fastjson.JSONObject;

import com.hust.hostmonitor_data_collector.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import com.hust.hostmonitor_data_collector.utils.DataSampleManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HybridDataCollectorService implements DataCollectorService{
    //数据采样
    private DataSampleManager dataSampleManager = DataSampleManager.getInstance();
    //配置数据
    private ConfigDataManager configDataManager= ConfigDataManager.getInstance();


    @Autowired
    UserDao userDao;

    //----- 监控节点
    //SSH 连接的节点配置数据List
    private List<HostConfigData> sshHostList;
    //Socket 连接的节点List
    //List<> socketHostList;
    //线程池(用于SSH节点的定时采样)
    private ExecutorService executorService;

    //----- 定时作业
    //定时器
    private Timer mainTimer = new Timer();
    //定时任务(Host性能采样)
    private TimerTask performanceSampleTask;
    //定时任务(Host进程采样)
    private TimerTask procesSampleTask;


    //----- 数据 -----
    //总体统计数据
    private JSONObject summaryInfo;
    public float[][] loadPartition;

    //IO测试数据
    public Map<String, JSONObject> ioTestInfoList = new HashMap<>();

    //采样数据
    private Map<String,JSONObject> sshSampleData = new HashMap<>();
    private Map<String,JSONObject> socketSampleData = new HashMap<>();

    //----- 内部函数 -----
    //构造函数
    public HybridDataCollectorService(){
        sshHostList = configDataManager.getSSHConfigHostList();
        //线程池大小设为Host个数*2
        executorService= Executors.newFixedThreadPool(sshHostList.size()*2);


        //SSH 采样
        {
            for(int i=0;i<sshHostList.size();i++){
                sshHostList.get(i);

            }

            //定时作业
            mainTimer.schedule(performanceSampleTask,7*1000,20* 1000);
            mainTimer.schedule(procesSampleTask,13*1000,20* 1000);
        }



    }


    //存储采样数据
    private void storeSampleData(){

    }




    //-----外部服务接口-----

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


    public String providerTest(String userName, String password, Timestamp timestamp){
        return userDao.signUp(userName,password,timestamp);
    }
}
