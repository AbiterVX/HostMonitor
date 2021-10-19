package com.hust.hostmonitor_data_collector.utils;


import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.JschSSHManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.SSHManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class DataSampleManager {
    private CmdExecutor cmdExecutor;
    //Config配置信息
    private ConfigDataManager configDataManager= ConfigDataManager.getInstance();
    private OSType localOSType;
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
        cmdExecutor = new CmdExecutor();

        //使用SSH监控的节点列表
        hostList = configDataManager.getSSHConfigHostList();
        localOSType = OSType.NONE;
    }

    private OSType getOSType(HostConfigData hostConfigData){
        if(hostConfigData!=null){
            return hostConfigData.osType;
        }
        else{
            return localOSType;
        }
    }

    public void setLocalOSType( OSType localOSType){
        this.localOSType = localOSType;
    }

    public JSONObject sampleHostHardwareData(HostConfigData hostConfigData){
        JSONObject sampleData = configDataManager.getSampleFormat("hostInfo");
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
        }
        else if(osType.equals(OSType.WINDOWS)){
            //hostName
            {

            }
            //osName
            {
                List<String> cmdResult = cmdExecutor.runCommand("ver",hostConfigData);
                if(cmdResult.size()>=2){
                    sampleData.put("osName",cmdResult.get(1));
                }
            }
            //diskInfo
            {
                //diskName
                //diskCapacitySize
                //diskModel

                /*List<String> cmdResult = cmdExecutor.runCommand("wmic diskdrive get Name,SerialNumber,Size,Model",hostConfigData);
                cmdResult.remove(0);
                for (String rowData:cmdResult){
                    if(!rowData.equals("")){
                        String[] diskData = rowData.split("\\s+");
                        JSONObject newDiskInfo = configDataManager.getSampleFormat("diskInfo");
                        {
                            newDiskInfo.put("diskName",diskData[2]);
                            newDiskInfo.put("diskModel",diskData[0]);
                            newDiskInfo.put("diskCapacitySize",diskData[2]);
                        }
                        sampleData.getJSONArray("diskInfoList").add(newDiskInfo);
                    }
                }*/
            }
            //cpuInfoList
            {
                List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject Win32_Processor\"",hostConfigData);
                for(String rowData:cmdResult){
                    if(rowData.startsWith("Name")){
                        JSONObject newCpuInfo = configDataManager.getSampleFormat("cpuInfo");
                        {
                            String[] tempData = rowData.split(":");
                            newCpuInfo.put("cpuName",tempData[1].trim());
                        }
                        sampleData.getJSONArray("cpuInfoList").add(newCpuInfo);
                    }
                }
            }
            //gpuInfo
            {
                List<String> cmdResult = cmdExecutor.runCommand("wmic PATH Win32_VideoController GET Name,Adapterram",hostConfigData);
                cmdResult.remove(0);
                for(String rowData:cmdResult){
                    if(!rowData.equals("")){
                        JSONObject newGpuInfo = configDataManager.getSampleFormat("gpuInfo");
                        {
                            String[] tempData = rowData.split("\\s+",2);
                            newGpuInfo.put("gpuName",tempData[1].trim());
                            newGpuInfo.put("gpuAvailableRam",Long.valueOf(tempData[0])/1024/1024);
                        }
                        sampleData.getJSONArray("gpuInfoList").add(newGpuInfo);
                    }
                }
            }
        }
        return sampleData;
    }

    public void sampleHostData(HostConfigData hostConfigData,JSONObject sampleData){
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){

        }
        else if(osType.equals(OSType.WINDOWS)){

            //CPU
            {
                //Cpu Usage
                List<String> cmdResult = cmdExecutor.runCommand("wmic cpu get loadpercentage",hostConfigData);
                cmdResult.remove(0);
                int currentIndex = 0;
                for(String rowData:cmdResult){
                    if(!rowData.equals("")){
                        JSONObject cpuInfo = sampleData.getJSONArray("cpuInfoList").getJSONObject(currentIndex);
                        cpuInfo.put("cpuUsage",Float.valueOf(cmdResult.get(1)));
                        currentIndex +=1;
                    }
                }
                //Cpu Temperature
                {

                }
            }
            //Net IO
            {
                long[] sendBytes = {0,0};
                long[] ReceiveBytes = {0,0};
                int sampleDelayMS = 200;
                {
                    List<String> cmdResult = cmdExecutor.runCommand("chcp 437 && netstat -e && chcp 936",hostConfigData);
                    for(String rowData:cmdResult){
                        if(rowData.startsWith("Bytes")){
                            String[] bytes = rowData.split("\\s+");
                            sendBytes[0] =Long.parseLong(bytes[1]);
                            ReceiveBytes[0] =Long.parseLong(bytes[2]);
                            break;
                        }
                    }
                }
                {
                    try {
                        Thread.sleep(sampleDelayMS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                {
                    List<String> cmdResult = cmdExecutor.runCommand("chcp 437 && netstat -e && chcp 936",hostConfigData);
                    for(String rowData:cmdResult){
                        if(rowData.startsWith("Bytes")){
                            String[] bytes = rowData.split("\\s+");
                            sendBytes[1] =Long.parseLong(bytes[1]);
                            ReceiveBytes[1] =Long.parseLong(bytes[2]);
                            break;
                        }
                    }
                }
                float sendSpeed = (sendBytes[1]- sendBytes[0] )* 1.0f / (sampleDelayMS/1000f);
                float receiveSpeed = (ReceiveBytes[1]- ReceiveBytes[0] )* 1.0f /(sampleDelayMS/1000f);
                sampleData.put("netSendSpeed",sendSpeed);
                sampleData.put("netReceiveSpeed",receiveSpeed);
            }

            //Disk
            {
                List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject -query { SELECT * FROM Win32_PerfFormattedData_PerfDisk_LogicalDisk}\"",hostConfigData);
                for(int i=0;i<2;i++){
                    cmdResult.remove(0);
                }

                for(String rowData:cmdResult){
                    if(rowData.equals("")){

                    }
                    else{
                        if(rowData.startsWith("Name")){
                            //IOPS
                        }
                        else if(rowData.startsWith("DiskReadBytesPersec")){
                            //读速度

                        }
                        else if(rowData.startsWith("DiskWriteBytesPersec")){
                            //写速度
                        }
                        else if(rowData.startsWith("PercentIdleTime")){
                            //空闲率
                        }
                        else if(rowData.startsWith("DiskTransfersPersec")){
                            //IOPS
                        }

                    }
                }
            }
            //
            {

            }


        }
    }


    public void sampleHostProcess(HostConfigData hostConfigData,JSONObject sampleData){
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){

        }
        else if(osType.equals(OSType.WINDOWS)){
            List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject -query { SELECT CreatingProcessID,Name,ElapsedTime,workingset,percentProcessorTime,ioReadBytesPersec,ioWriteBytesPersec  FROM Win32_PerfFormattedData_PerfProc_Process  }\""
                    ,hostConfigData);

        }
    }

    public JSONObject ioTest(HostConfigData hostConfigData){
        OSType osType = getOSType(hostConfigData);
        JSONObject sampleData = new JSONObject();
        if(osType.equals(OSType.LINUX)){

        }
        else if(osType.equals(OSType.WINDOWS)){

        }
        return sampleData;
    }


    public static void main(String[] args) {
        DataSampleManager dataSampleManager = DataSampleManager.getInstance();
        dataSampleManager.setLocalOSType(OSType.WINDOWS);
        JSONObject sampleData = dataSampleManager.sampleHostHardwareData(null);
        //System.out.println(sampleData);

        dataSampleManager.sampleHostData(null,sampleData);
        System.out.println(sampleData);
    }
}
