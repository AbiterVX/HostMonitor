package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_client.utils.KylinEntity.KylinGPU;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KySampler implements Sampler{
    private SystemInfo systemInfo;
    private JSONObject dataObject;
    private FormatConfig formatConfig=FormatConfig.getInstance();
    private JSONArray processInfoList;
    private Map<Integer, OSProcess> processMapLastSample = new HashMap<>();
    private Map<String, Float> processFilter;
    private String diskDataPath;
    private String queryCommand="tasklist";
    //静态硬件信息采样，不会周期性调用
    public KySampler(){
        diskDataPath=System.getProperty("user.dir")+"/DiskPredict/client/sampleData/data.csv";
        systemInfo = new SystemInfo();
        dataObject= new JSONObject();
        dataObject.putAll(formatConfig.getHostInfoJson());
        dataObjectInitialization();
        processFilter = formatConfig.getProcessFilter();
    }
    private void dataObjectInitialization(){
        JSONObject cpuObject=new JSONObject();
        cpuObject.putAll(formatConfig.getCpuInfoJson());
        dataObject.getJSONArray("cpuInfoList").add(cpuObject);
        int diskNumber=systemInfo.getHardware().getDiskStores().size();
        int i=0;
        for(i=0;i<diskNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getDiskInfoJson());
            dataObject.getJSONArray("diskInfoList").add(temp);
        }
        int gpuNumber=systemInfo.getHardware().getGraphicsCards().size();
        for(i=0;i<gpuNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getGpuInfoJson());
            dataObject.getJSONArray("gpuInfoList").add(temp);
        }
        int netInterfaceNumber=systemInfo.getHardware().getNetworkIFs().size();
        for(i=0;i<netInterfaceNumber;i++){
            JSONObject temp=new JSONObject();
            temp.putAll(formatConfig.getNetInterfaceInfoJson());
            dataObject.getJSONArray("netInterfaceList").add(temp);
        }

    }


    @Override
    public void hardWareSample() {
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        OperatingSystem os = systemInfo.getOperatingSystem();
        dataObject.put("hostName",os.getNetworkParams().getHostName());
        dataObject.put("osName",os.toString());
        //CPU部分
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier identifier=centralProcessor.getProcessorIdentifier();
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuName",identifier.getName());
        HashMap<String,SmartInfo> types=readDiskSmartInfo();
        //磁盘
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        int i=0;
        long totalsize=0;
        for(i=0;i<hwDiskStoreList.size();i++){
            HWDiskStore tempDiskStore=hwDiskStoreList.get(i);
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

        //单位GB

        dataObject.put("diskCapacityTotalSizeSum",FormatUtils.doubleTo2bits_double(totalsize*1.0/1024/1024/1024));
        //resultObject.put("Disks",diskArray);


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
        List<NetworkIF> networkIFList=systemInfo.getHardware().getNetworkIFs();
        for(i=0;i<networkIFList.size();i++){
            NetworkIF tempNetworkIF=networkIFList.get(i);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(i).put("netInterfaceName",tempNetworkIF.getDisplayName());
        }
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
}
