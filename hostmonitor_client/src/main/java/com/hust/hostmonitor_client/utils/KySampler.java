package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_client.utils.KylinEntity.*;



import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KySampler implements Sampler{

    private JSONObject dataObject;
    private FormatConfig formatConfig=FormatConfig.getInstance();
    private JSONArray processInfoList;
    private Map<String, Float> processFilter;
    private String diskDataPath;
    private KylinPeriodRecord lastSample=null;
    //静态硬件信息采样，不会周期性调用
    public KySampler(){
        diskDataPath=System.getProperty("user.dir")+"/DiskPredict/client/sampleData/data.csv";
        dataObject= new JSONObject();
        dataObject.putAll(formatConfig.getHostInfoJson());
        dataObjectInitialization();
        processFilter = formatConfig.getProcessFilter();
    }
    private void dataObjectInitialization(){
        JSONObject cpuObject=new JSONObject();
        cpuObject.putAll(formatConfig.getCpuInfoJson());
        dataObject.getJSONArray("cpuInfoList").add(cpuObject);
        int diskNumber=DeeperInOSHI.getDiskStoreSize();
        int i=0;
        for(i=0;i<diskNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getDiskInfoJson());
            dataObject.getJSONArray("diskInfoList").add(temp);
        }
        int gpuNumber=DeeperInOSHI.getGraphicsCardSize();
        for(i=0;i<gpuNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getGpuInfoJson());
            dataObject.getJSONArray("gpuInfoList").add(temp);
        }
        int netInterfaceNumber=DeeperInOSHI.getNetworkIFSize();
        for(i=0;i<netInterfaceNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getNetInterfaceInfoJson());
            dataObject.getJSONArray("netInterfaceList").add(temp);
        }

    }


    @Override
    public void hardWareSample() {
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        //主机名
        dataObject.put("hostName",DeeperInOSHI.getHostName());
        //操作系统部分 cat /proc/version
        dataObject.put("osName",DeeperInOSHI.getOSName());
        //CPU部分 cat /proc/cpuinfo |grep cpu
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuName",DeeperInOSHI.getCPUName());
        //磁盘
        List<KylinDiskStore> hwDiskStoreList=DeeperInOSHI.getDiskStores();
        HashMap<String,SmartInfo> types=readDiskSmartInfo();
        int i=0;
        long totalsize=0;
        for(i=0;i<hwDiskStoreList.size();i++){
            KylinDiskStore tempDiskStore=hwDiskStoreList.get(i);
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskName",tempDiskStore.getSerial().trim());
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskModel",tempDiskStore.getModel());
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskCapacityTotalSize",FormatUtils.doubleTo2bits_double((tempDiskStore.getSize()*1.0/1024/1024/1024)));
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
            String serial=tempDiskStore.getSerial().trim();
            for(String string:types.keySet()){
                if(string.toLowerCase().contains(serial.toLowerCase())||stringReorder(string.toLowerCase()).contains(serial.toLowerCase())){
                    dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("type",types.get(string).isSsd);
                    dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskName",string);
                    break;
                }else{
                    for(String backsn:types.get(string).backup){
                        if(backsn.toLowerCase().contains(serial.toLowerCase())){
                            String[] tokens=backsn.split(":");
                            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskName",tokens[1]);
                            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("type",types.get(string).isSsd);
                            break;
                        }
                    }
                }
            }
            totalsize+=tempDiskStore.getSize();

        }
        dataObject.put("diskCapacityTotalSizeSum",FormatUtils.doubleTo2bits_double(totalsize*1.0/1024/1024/1024));
        //GPU
        List<KylinGPU> graphicsCards = DeeperInOSHI.getGraphicsCardsFromLspci();
        for (i=0;i<graphicsCards.size();i++){
            KylinGPU tempGraphicsCard=graphicsCards.get(i);
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuName",tempGraphicsCard.getName());
            //单位MB
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuAvailableRam",FormatUtils.doubleTo2bits_double(tempGraphicsCard.getVram()*1.0/1024/1024/1024));
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
        }
        //net interface   IfConfig
        List<KylinNetworkIF> networkIFList=DeeperInOSHI.getNetworkIFs();
        for(i=0;i<networkIFList.size();i++){
            KylinNetworkIF tempNetworkIF=networkIFList.get(i);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(i).put("netInterfaceName",tempNetworkIF.getDisplayName());
        }
        System.out.println(outputSampleData(false));
    }

    @Override
    public void periodSample(int period, boolean isTheFirstTimeToSample) {
        KylinPeriodRecord record=DeeperInOSHI.getPeriodRecord();
        //Memory利用率
        JSONArray memoryUsage=new JSONArray();
        memoryUsage.add(FormatUtils.doubleTo2bits_double((record.getMemTotal()- record.getMemAvailable())*1.0/1024/1024));
        memoryUsage.add(FormatUtils.doubleTo2bits_double(record.getMemTotal()*1.0/1024/1024));
        dataObject.put("memoryUsage",memoryUsage);
        //Cpu利用率,但是是单个CPU的实现 第一次采样无法计算
        if(isTheFirstTimeToSample){
            dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", FormatUtils.doubleTo2bits_double(0.0 * 100));
            dataObject.put("cpuUsage",  FormatUtils.doubleTo2bits_double(0.0 * 100));
        }
        else {
            long oldTotalTicks = lastSample.getCPUtotal();
            long oldUsedTicks = lastSample.getCPUused();
            long newTotalTicks = record.getCPUtotal();
            long newUsedTicks = record.getCPUused();
            double cpuUsage = (newUsedTicks - oldUsedTicks)* 1.0f / (newTotalTicks - oldTotalTicks) ;
            System.out.println(""+(newUsedTicks - oldUsedTicks)+"/"+(newTotalTicks - oldTotalTicks));
            double cpuUsage2bits = FormatUtils.doubleTo2bits_double(cpuUsage * 100);
            dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", cpuUsage2bits);
            dataObject.put("cpuUsage", cpuUsage2bits);
        }
        //Cpu温度 无法获取
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuTemperature",FormatUtils.doubleTo2bits_double(record.getCPUTemperature()));

        //磁盘占用率/iops/速率
        List<DiskInfo> DiskStoreList=record.getDisks();
        long totalUsedSize=0;
        for(int j=0;j<DiskStoreList.size();j++){
            DiskInfo tempDiskInfo=DiskStoreList.get(j);
            int i=findDiskIndex(tempDiskInfo.diskName);
            double usage2bits=0.0;
            try {
                usage2bits = FormatUtils.doubleTo2bits_double(tempDiskInfo.diskUsedRadio);
            } catch (Exception e) {
                System.out.println("usage2bitsError");
            }
            JSONArray singleArray=new JSONArray();
            double singleTotalSize=dataObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskCapacityTotalSize");
            double singleUsedSize=tempDiskInfo.diskFSUsageAmount*1.0f/1024/1024;
            singleArray.add(FormatUtils.doubleTo2bits_double(singleUsedSize));
            singleArray.add(singleTotalSize);
            totalUsedSize+=singleUsedSize;
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskUsage",usage2bits);
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskCapacitySize",singleArray);
            double ReadRates = tempDiskInfo.diskReadSpeed;
            double WriteRates = tempDiskInfo.diskWriteSpeed;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskIOPS", tempDiskInfo.diskIOPS);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadSpeed", FormatUtils.doubleTo2bits_double(ReadRates));
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteSpeed", FormatUtils.doubleTo2bits_double(WriteRates));
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",0);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",0);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",0);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",0);
        }
        JSONArray diskUsage=new JSONArray();
        diskUsage.add(FormatUtils.doubleTo2bits_double(totalUsedSize));
        diskUsage.add(dataObject.getDouble("diskCapacityTotalSizeSum"));
        dataObject.put("diskCapacityTotalUsage",diskUsage);
        //网络速率计算

        dataObject.put("netReceiveSpeed",FormatUtils.doubleTo2bits_double(record.getNetReceive()*1.0f/1024/1024));
        System.out.println(record.getNetReceive());
        dataObject.put("netSendSpeed",FormatUtils.doubleTo2bits_double(record.getNetSend())*1.0f/1024/1024);
        System.out.println(record.getNetSend());
        lastSample=record;
    }
    private int findDiskIndex(String diskName){
        for(int i=0;i<dataObject.getJSONArray("diskInfoList").size();i++){
            if( dataObject.getJSONArray("diskInfoList").getJSONObject(i).getString("diskName").contains(diskName)){
                return i;
            }
        }
        return 0;
    }
    private HashMap<String,SmartInfo> readDiskSmartInfo(){
        HashMap<String,SmartInfo> types=new HashMap<>();
        File file=new File(diskDataPath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            String str=null;
            boolean flag=true;
            while((str=br.readLine())!=null){
                if(flag){
                    flag=false;
                    continue;
                }
                String[] tokens=str.split(",");
                SmartInfo tempSmartInfo=new SmartInfo();
                tempSmartInfo.isSsd=Integer.parseInt(tokens[5]);
                ArrayList<String> snList=new ArrayList<>();
                String[] sns=tokens[3].split("/");
                for(int i=0;i<sns.length;i++){
                    snList.add(sns[i]);
                }
                tempSmartInfo.backup=snList;
                types.put(tokens[1],tempSmartInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return types;
    }
    @Override
    public void processInfoSample(int period, int processFrequency) {
        //top -b -n 1 > processInfo.txt
        processInfoList = new JSONArray();
        List<KylinProcess> processesList=null;
        processesList=DeeperInOSHI.getProcesses();
        System.out.println("进程个数"+processesList.size());
        for(KylinProcess osProcess:processesList){

            //进程过滤
            if(processFilter.get("cpuUsage") <= osProcess.getCpuUsage() || processFilter.get("memoryUsage")<= osProcess.getCpuUsage() ||
                    processFilter.get("diskReadSpeed")<= osProcess.getDiskReadSpeed() || processFilter.get("diskWriteSpeed")<= osProcess.getDiskWriteSpeed()){
                JSONObject newProcess = new JSONObject();
                newProcess.put("processId",osProcess.getProcessID());
                newProcess.put("processName",osProcess.getName());
                newProcess.put("startTime",osProcess.getStartTime());
                newProcess.put("cpuUsage",osProcess.getCpuUsage());
                newProcess.put("memoryUsage",osProcess.getMemoryUsage());
                newProcess.put("diskReadSpeed",osProcess.getDiskReadSpeed());
                newProcess.put("diskWriteSpeed",osProcess.getDiskWriteSpeed());
                processInfoList.add(newProcess);
            }
        }
    }

    @Override
    public String outputSampleData(boolean insertProcessOrNot) {
        JSONObject outputObject=new JSONObject();
        outputObject.putAll(formatConfig.getOutputInfoJson());
        for(String a:outputObject.keySet()){
            outputObject.put(a,dataObject.get(a));
        }
        if(insertProcessOrNot){
            outputObject.put("processInfoList", processInfoList);
        }

        return outputObject.toJSONString();
    }

    @Override
    public String hostName() {
        return dataObject.getString("hostName");
    }

    @Override
    public String OSName() {
        return dataObject.getString("osName");
    }
    public String stringReorder(String original){
        StringBuffer stringBuffer=new StringBuffer();
        int groupNumber=original.length()/2;
        int i=0;
        for(i=0;i<groupNumber;i++){
            stringBuffer.append(original.charAt(i*2+1));
            stringBuffer.append(original.charAt(i*2));
        }
        if(i*2==original.length()-1)
            stringBuffer.append(original.charAt(i*2));
        return stringBuffer.toString();
    }
}
