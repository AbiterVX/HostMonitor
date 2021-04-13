package com.hust.hostmonitor_data_collector.utils;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Host各个数值相关信息
 */
public class HostInfo {
    public HostInfo(String _ip){
        ip = _ip;
        receiveBytes = new int[2];
        transmitBytes = new int[2];
        cpuTotalTime = new int[2];
        cpuIdleTime = new int[2];
        ioTimeSpent = new int[2];
        initValue();

        sessionConnected = false;
    }

    public void initValue(){
        Arrays.fill(receiveBytes,0);
        Arrays.fill(transmitBytes,0);
        Arrays.fill(cpuTotalTime,0);
        Arrays.fill(cpuIdleTime,0);
        memTotal = 0;
        memAvaliable = 0;
        Arrays.fill(ioTimeSpent,0);
    }
    //会话建立
    public boolean sessionConnected;


    //ip
    public String ip;

    //[流量统计]接受字节数量
    public int[] receiveBytes;
    //[流量统计]发送字节数量
    public int[] transmitBytes;

    //[CPU使用率]总时间
    public int[] cpuTotalTime;

    //[CPU使用率]空闲时间
    public int[] cpuIdleTime;

    //[内存]内存总量
    public int memTotal;
    //[内存]内存可用
    public int memAvaliable;

    //[磁盘]输入/输出操作花费的时间
    public int[] ioTimeSpent;

    //CPU温度
    public float cpuTemperature;
    //能耗
    public float energyConsumption;
    //IO数量
    public int[] ioNum;


    //----------硬件型号
    //CPU型号
    public String cpuType;
    //内存型号
    public String memoryType;
    //内存大小
    public String memorySize;
    //磁盘型号
    public String diskType;
    //操作系统型号
    public String osType;
    //网络带宽
    public String netBindwidth;

    //



    //[网络]带宽（格式KB/s）
    public float[] getNetBindWidth(){
        float receiveBW = 0;
        float transmitBW = 0;
        if(receiveBytes[0] != 0 && receiveBytes[0] != transmitBytes[1]){
            //两次采样差值，并由原bytes转为KB/s格式
            receiveBW = (receiveBytes[1] - receiveBytes[0])*8f/60f/1024f ;
            transmitBW = (transmitBytes[1] - transmitBytes[0])*8f/60f/1024f;
            //保留2位小数
            receiveBW=( float )(Math.round(receiveBW* 100f ) / 100f );
            transmitBW=( float )(Math.round(transmitBW* 100f )/ 100f );
        }
        float[] result = new float[2];
        result[0] = receiveBW;
        result[1] = transmitBW;
        //System.out.println("IP: "+ip+" ,receiveBW: "+receiveBW + "KB/s ,transmitBW: "+transmitBW +" KB/s");
        return result;
    }
    //[CPU]利用率
    public float getCpuUsage(){
        float cpuUsage = 0;
        if(cpuTotalTime[0] != 0 && cpuTotalTime[0] != cpuTotalTime[1]){
            cpuUsage = (1f-  (float) (cpuIdleTime[1] -cpuIdleTime[0]) / (float)(cpuTotalTime[1] - cpuTotalTime[0]) ) * 100f ;
            System.out.println("IP: "+ip+" ,cpuUsage: "+cpuUsage + " %");
            cpuUsage=( float )(Math.round(cpuUsage* 100f ) / 100f );
        }
        return cpuUsage;
    }
    //[内存]利用率
    public float getMemoryUsage(){
        float memoryUsage = 0;
        memoryUsage = (float) (memTotal - memAvaliable ) / (float)memTotal * 100f ;
        memoryUsage = ( float )(Math.round(memoryUsage* 100f ) / 100f );
        //System.out.println("IP: "+ip+" ,memoryUsage: "+memoryUsage + " %");
        return memoryUsage;
    }
    //[磁盘]利用率
    public float getDiskUsage(int interval_ms){
        float diskUsage = 0;
        if(ioTimeSpent[0] != 0 && ioTimeSpent[0] != ioTimeSpent[1]){
            diskUsage = (float) (ioTimeSpent[1] - ioTimeSpent[0]) / (float)interval_ms * 100f;
            diskUsage=( float )(Math.round(diskUsage* 100f ) / 100f );
        }
        //System.out.println("IP: "+ip+" ,diskUsage: "+diskUsage + " %");
        return diskUsage;
    }

    //得出用于前端显示的数据
    public Map<String, Object> getOutputData(int interval_ms){
        Map<String, Object> resultMap=new HashMap<>();
        if(sessionConnected){
            float[] netBindWidth = getNetBindWidth();
            float cpuUsage = getCpuUsage();
            float memoryUsage = getMemoryUsage();
            float diskUsage = getDiskUsage(interval_ms);
//            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
//            sdf.applyPattern("yyyy/MM/dd HH:mm:ss");// a为am/pm的标记

            resultMap.put("timestamp",new Timestamp(System.currentTimeMillis()));
            resultMap.put("ip",ip);
            resultMap.put("receiveBW",netBindWidth[0]);
            resultMap.put("transmitBW",netBindWidth[1]);
            resultMap.put("cpuUsage",cpuUsage);
            resultMap.put("memoryUsage",memoryUsage);
            resultMap.put("diskUsage",diskUsage);
        }
        else{
            String emptyLabel = "--";
            resultMap.put("timestamp",new Timestamp(System.currentTimeMillis()));
            resultMap.put("ip",ip);
            resultMap.put("receiveBW",0.0f);
            resultMap.put("transmitBW",0.0f);
            resultMap.put("cpuUsage",0.0f);
            resultMap.put("memoryUsage",0.0f);
            resultMap.put("diskUsage",0.0f);
        }


        return resultMap;
    }
}
