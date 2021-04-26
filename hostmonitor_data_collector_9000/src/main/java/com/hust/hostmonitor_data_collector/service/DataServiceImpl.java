package com.hust.hostmonitor_data_collector.service;

import com.hust.hostmonitor_data_collector.utils.HostMonitorBatchExecution;

import java.util.Timer;
import java.util.TimerTask;

public class DataServiceImpl implements DataService{
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
        //Todo
    }


    //----------对外接口----------

    @Override
    public String getHostIp() {
        return "[\"ip1\",\"ip2\",\"ip3\"]";
    }

    @Override
    public String getHostState() {
        return "[X,X,X]";
    }

    @Override
    public String getHostHardwareInfo() {
        return "null";
    }

    @Override
    public String getHostInfoRealTime() {
        return "null";
    }

    @Override
    public String getHostInfoRecent(int index, int hour) {
        return "null";
    }

    @Override
    public String getHostInfoField(int index, int hour, HostInfoFieldType field) {
        return "null";
    }

    @Override
    public String getHostProcessInfoRealTime(int index) {
        return "null";
    }

}
