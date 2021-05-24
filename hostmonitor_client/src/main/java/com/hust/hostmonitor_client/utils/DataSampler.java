package com.hust.hostmonitor_client.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;
import sun.nio.ch.Net;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    参考文档：http://oshi.github.io/oshi/oshi-core/apidocs/oshi/hardware/package-summary.html

    cpu利用率：两次间隔采样，并计算idle比例
    memory利用率：globalMemory.getAvailable() / getTotal
    磁盘占用率：计算每个盘的单独容量以及当前使用量。
    磁盘iops：两次采样，io个数/时间段=iops
    磁盘io速率：两次采样，磁盘读写数据量/时间段
    网络：两次采样，计算收发速率

硬件：
    CPU：类型，核数，温度
    磁盘：类型，总量
    GPU：类型
    操作系统：类型，版本

*/
public class DataSampler {
    private SystemInfo systemInfo;
    private JSONObject dataObject;
    private FormatConfig formatConfig=new FormatConfig();
    private HashMap<Integer,JSONObject> processMap;

    //静态硬件信息采样，不会周期性调用
    public DataSampler(){
        systemInfo = new SystemInfo();
        dataObject= new JSONObject();
        dataObject.putAll(formatConfig.getHostInfoJson());
        dataObjectInitialization();
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
        processMap=new HashMap<>();
    }
    public void hardWareSample(){
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        OperatingSystem os = systemInfo.getOperatingSystem();
        dataObject.put("hostName",os.getNetworkParams().getHostName());
        dataObject.put("osName",os.toString());
        //CPU部分
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier identifier=centralProcessor.getProcessorIdentifier();
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuName",identifier.getName());

        //磁盘
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        int i=0;
        long totalsize=0;
        for(i=0;i<hwDiskStoreList.size();i++){
            HWDiskStore tempDiskStore=hwDiskStoreList.get(i);
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskName",tempDiskStore.getModel());
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskCapacityTotalSize",FormatUtils.doubleTo2bits_double((tempDiskStore.getSize()*1.0/1024/1024/1024)));
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
            totalsize+=tempDiskStore.getSize();
            //tempDiskObject.put("size",hwDiskStore.getSize() > 0L ? FormatUtil.formatBytesDecimal(hwDiskStore.getSize()) : "?");
            //diskArray.add(tempDiskObject);
        }
        //单位GB
        System.out.println(FormatUtils.doubleTo2bits_double(totalsize*1.0/1024/1024/1024));
        System.out.println(totalsize);
        dataObject.put("diskCapacityTotalSizeSum",FormatUtils.doubleTo2bits_double(totalsize*1.0/1024/1024/1024));
        //resultObject.put("Disks",diskArray);
        //GPU
        List<GraphicsCard> graphicsCards =  systemInfo.getHardware().getGraphicsCards();
        for (i=0;i<graphicsCards.size();i++){
            GraphicsCard tempGraphicsCard=graphicsCards.get(i);
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuName",tempGraphicsCard.getName());
            //单位MB
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuAvailableRam",FormatUtils.doubleTo2bits_double(tempGraphicsCard.getVRam()*1.0/1024/1024/1024));
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
        }
        //net interface
        List<NetworkIF> networkIFList=systemInfo.getHardware().getNetworkIFs();
        for(i=0;i<networkIFList.size();i++){
            NetworkIF tempNetworkIF=networkIFList.get(i);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(i).put("netInterfaceName",tempNetworkIF.getDisplayName());
        }
        System.out.println(dataObject.get("diskCapacityTotalSizeSum"));
    }
    public void periodSample(int period,boolean isTheFirstTimeToSample){
        if(isTheFirstTimeToSample){
            firstSample();
            return;
        }
        //Memory利用率
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        JSONArray memoryUsage=new JSONArray();
        memoryUsage.add(FormatUtils.doubleTo2bits_double((globalMemory.getTotal()-globalMemory.getAvailable())*1.0/1024/1024));
        memoryUsage.add(FormatUtils.doubleTo2bits_double(globalMemory.getTotal()*1.0/1024/1024));
        dataObject.put("memoryUsage",memoryUsage);
        //Cpu利用率,但是是单个CPU的实现
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();
        long oldTotalTicks=dataObject.getJSONArray("cpuInfoList").getJSONObject(0).getLong("TotalTicks");
        long oldIdleTick=dataObject.getJSONArray("cpuInfoList").getJSONObject(0).getLong("idleTick");
        long newTotalTicks=0;
        long totalCpu = 0;
        for(int i=0;i<ticks.length;i++){
            newTotalTicks += ticks[i];
        }
        totalCpu=newTotalTicks-oldTotalTicks;
        double cpuUsage = 1.0 - ((ticks[CentralProcessor.TickType.IDLE.getIndex()] - oldIdleTick) * 1.0 / totalCpu);
        double cpuUsage2bits=FormatUtils.doubleTo2bits_double(cpuUsage*100);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", cpuUsage2bits);
        dataObject.put("cpuUsage",cpuUsage2bits);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("TotalTicks",newTotalTicks);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("idleTick",ticks[CentralProcessor.TickType.IDLE.getIndex()]);

        //Cpu温度
        Sensors sensors = systemInfo.getHardware().getSensors();
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuTemperature",FormatUtils.doubleTo2bits_double(sensors.getCpuTemperature()));

        //磁盘占用率/iops/速率
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        List<partionInfo> pList=processing(systemInfo.getOperatingSystem().getFileSystem().getFileStores());
        long totalUsedSize=0;
        for(int j=0;j<hwDiskStoreList.size();j++){
            HWDiskStore hwInThisLoop=hwDiskStoreList.get(j);
            List<HWPartition> hwpList= hwInThisLoop.getPartitions();
            long usable=0;
            long total=0;
            for(HWPartition hwPartition:hwpList){
                for(partionInfo pInfo:pList){
                    if(hwPartition.getUuid().equals(pInfo.volumn)){
                        usable+=pInfo.usable;
                        total+=pInfo.total;
                        break;
                    }
                }
            }
            double usage=usable*1.0/total;
            double usage2bits=FormatUtils.doubleTo2bits_double(usage);
            JSONArray singleArray=new JSONArray();
            double singleTotalSize=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getDouble("diskCapacityTotalSize");
            singleArray.add(FormatUtils.doubleTo2bits_double((total-usable)*1.0/1024/1024/1024));
            singleArray.add(singleTotalSize);
            totalUsedSize+=total-usable;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskUsage",usage2bits);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskCapacitySize",singleArray);
            long previousReadNumber=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getLong("diskRead");
            long previousReadBytes=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getLong("diskReadBytes");
            long previousWriteNumber=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getLong("diskWrite");
            long previousWriteBytes=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getLong("diskWriteBytes");
            long ReadNumber=hwInThisLoop.getReads();
            long ReadBytes=hwInThisLoop.getReadBytes();
            long WriteNumber=hwInThisLoop.getWrites();
            long WriteBytes=hwInThisLoop.getWriteBytes();
            double iops = (ReadNumber + WriteNumber - previousReadNumber - previousWriteNumber) * 1.0 / period;
            double ReadRates = (ReadBytes - previousReadBytes) * 1.0 / period;
            double WriteRates = (WriteBytes - previousWriteBytes) * 1.0 / period;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskIOPS", iops);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadSpeed", FormatUtils.doubleTo2bits_double(ReadRates/1024));
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteSpeed", FormatUtils.doubleTo2bits_double(WriteRates/1024));
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",ReadNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",ReadBytes);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",WriteNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",WriteBytes);
        }
        JSONArray diskUsage=new JSONArray();
        diskUsage.add(FormatUtils.doubleTo2bits_double(totalUsedSize*1.0/1024/1024/1024));
        diskUsage.add(dataObject.getDouble("diskCapacityTotalSizeSum"));
        dataObject.put("diskCapacityTotalUsage",diskUsage);
        //网络速率计算
        List<NetworkIF> networkIFS=systemInfo.getHardware().getNetworkIFs();
        double totalNetRecv=0;
        double totalNetSent=0;
        for(int k=0;k<networkIFS.size();k++){
            NetworkIF networkIF=networkIFS.get(k);
            long previousRecvBytes=dataObject.getJSONArray("netInterfaceList").getJSONObject(k).getLong("recvBytes");
            long previousSentBytes=dataObject.getJSONArray("netInterfaceList").getJSONObject(k).getLong("sentBytes");
            long RecvBytes=networkIF.getBytesRecv();
            long SentBytes=networkIF.getBytesSent();
            double NetRecv = (RecvBytes - previousRecvBytes) * 1.0 / period;
            double NetSent = (SentBytes - previousSentBytes) * 1.0 / period;
            totalNetRecv+=NetRecv;
            totalNetSent+=NetSent;
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("recvSpeed", FormatUtils.doubleTo2bits_double(NetRecv/1024));
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("sentSpeed", FormatUtils.doubleTo2bits_double(NetSent/1024));
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("recvBytes",RecvBytes);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("sentBytes",SentBytes);

        }
        //单位MB/s
        dataObject.put("netReceiveSpeed",FormatUtils.doubleTo2bits_double(totalNetRecv/1024/1024));
        dataObject.put("netSendSpeed",FormatUtils.doubleTo2bits_double(totalNetSent/1024/1024));
        System.out.println(dataObject.get("diskCapacityTotalSizeSum"));
        System.out.println("Sample Finish");
    }
    private List<partionInfo> processing(List<OSFileStore> fsList){
        ArrayList<partionInfo> result=new ArrayList<>();
        for(OSFileStore OSfs: fsList){
            String volumn=OSfs.getVolume();
            int left=volumn.indexOf("{");
            int right=volumn.indexOf("}");
            volumn=volumn.substring(left+1,right);
            partionInfo pInfo=new partionInfo(volumn,OSfs.getTotalSpace(),OSfs.getUsableSpace());
            result.add(pInfo);
        }
        return  result;
    }
    public void processInfoSample(int period,int processFrequency){
        List<OSProcess> processesList=systemInfo.getOperatingSystem().getProcesses();
        Long memory=systemInfo.getHardware().getMemory().getTotal();
        for(OSProcess osProcess:processesList){
            JSONObject tempObject=processMap.get(osProcess.getProcessID());
            if(tempObject==null){
                tempObject=new JSONObject();
                tempObject.putAll(formatConfig.getProcessInfoJson());
                tempObject.put("processId",osProcess.getProcessID());
                tempObject.put("processName",osProcess.getName());
                tempObject.put("startTime",osProcess.getStartTime());
                tempObject.put("cpuUsage",osProcess.getProcessCpuLoadCumulative());
                double memUsage=osProcess.getResidentSetSize()*1.0/memory;
                tempObject.put("memoryUsage",memUsage);
                tempObject.put("ReadBytes",osProcess.getBytesRead());
                tempObject.put("WriteBytes",osProcess.getBytesWritten());
                processMap.put(osProcess.getProcessID(),tempObject);
            }
            else {
                if(osProcess.getStartTime()==tempObject.getLong("startTime")){
                    tempObject.put("startTime",osProcess.getStartTime());
                    tempObject.put("cpuUsage",osProcess.getProcessCpuLoadCumulative());
                    double memUsage=osProcess.getResidentSetSize()*1.0/memory;
                    tempObject.put("memoryUsage",memUsage);
                    long oldReadBytes=tempObject.getLong("ReadBytes");
                    long oldWriteBytes=tempObject.getLong("WriteBytes");
                    long ReadBytes=osProcess.getBytesRead();
                    long WriteBytes=osProcess.getBytesWritten();
                    double readSpeed=(ReadBytes-oldReadBytes)*1.0/(processFrequency*period);
                    double writeSpeed=(WriteBytes-oldWriteBytes)*1.0/(processFrequency*period);
                    tempObject.put("diskReadSpeed",readSpeed);
                    tempObject.put("diskWriteSpeed",writeSpeed);
                    tempObject.put("ReadBytes",ReadBytes);
                    tempObject.put("WriteBytes",WriteBytes);
                }
                else{
                    tempObject=new JSONObject();
                    tempObject.putAll(formatConfig.getProcessInfoJson());
                    tempObject.put("processId",osProcess.getProcessID());
                    tempObject.put("processName",osProcess.getName());
                    tempObject.put("startTime",osProcess.getStartTime());
                    tempObject.put("cpuUsage",osProcess.getProcessCpuLoadCumulative());
                    double memUsage=osProcess.getResidentSetSize()*1.0/memory;
                    tempObject.put("memoryUsage",memUsage);
                    tempObject.put("ReadBytes",osProcess.getBytesRead());
                    tempObject.put("WriteBytes",osProcess.getBytesWritten());
                    processMap.put(osProcess.getProcessID(),tempObject);
                }
            }
        }
    }
    private void firstSample(){
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        JSONArray memoryUsage=new JSONArray();
        memoryUsage.add(FormatUtils.doubleTo2bits_double((globalMemory.getTotal()-globalMemory.getAvailable())*1.0/1024/1024));
        memoryUsage.add(FormatUtils.doubleTo2bits_double(globalMemory.getTotal()*1.0/1024/1024));
        dataObject.put("memoryUsage",memoryUsage);
        //Cpu利用率
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();
        long newTotalTicks=0;
        long totalCpu = 0;
        for(int i=0;i<ticks.length;i++){
            newTotalTicks += ticks[i];
        }
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("TotalTicks",newTotalTicks);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("idleTick",ticks[CentralProcessor.TickType.IDLE.getIndex()]);
        //Cpu温度
        Sensors sensors = systemInfo.getHardware().getSensors();
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuTemperature",FormatUtils.doubleTo2bits_double(sensors.getCpuTemperature()));
        //磁盘占用率/iops/速率
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        List<partionInfo> pList=processing(systemInfo.getOperatingSystem().getFileSystem().getFileStores());
        long totalUsedSize=0;
        for(int j=0;j<hwDiskStoreList.size();j++){
            HWDiskStore hwInThisLoop=hwDiskStoreList.get(j);
            List<HWPartition> hwpList= hwInThisLoop.getPartitions();
            long usable=0;
            long total=0;
            for(HWPartition hwPartition:hwpList){
                for(partionInfo pInfo:pList){
                    if(hwPartition.getUuid().equals(pInfo.volumn)){
                        usable+=pInfo.usable;
                        total+=pInfo.total;
                        break;
                    }
                }
            }
            double usage=usable*1.0/total;
            double usage2bits=FormatUtils.doubleTo2bits_double(usage);
            JSONArray singleArray=new JSONArray();
            double singleTotalSize=dataObject.getJSONArray("diskInfoList").getJSONObject(j).getDouble("diskCapacityTotalSize");
            singleArray.add(FormatUtils.doubleTo2bits_double((total-usable)*1.0/1024/1024/1024));
            singleArray.add(singleTotalSize);
            totalUsedSize+=total-usable;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskUsage",usage2bits);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskCapacitySize",singleArray);
            long ReadNumber=hwInThisLoop.getReads();
            long ReadBytes=hwInThisLoop.getReadBytes();
            long WriteNumber=hwInThisLoop.getWrites();
            long WriteBytes=hwInThisLoop.getWriteBytes();
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",ReadNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",ReadBytes);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",WriteNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",WriteBytes);
        }
        JSONArray diskUsage=new JSONArray();
        diskUsage.add(FormatUtils.doubleTo2bits_double(totalUsedSize*1.0/1024/1024/1024));
        diskUsage.add(dataObject.getDoubleValue("diskCapacityTotalSizeSum"));
        dataObject.put("diskCapacityTotalUsage",diskUsage);
        //网络速率计算
        List<NetworkIF> networkIFS=systemInfo.getHardware().getNetworkIFs();
        for(int k=0;k<networkIFS.size();k++){
            NetworkIF networkIF=networkIFS.get(k);
            long RecvBytes=networkIF.getBytesRecv();
            long SentBytes=networkIF.getBytesSent();
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("recvBytes",RecvBytes);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("sentBytes",SentBytes);

        }
        System.out.println("Sample Finish");

    }
    public String outputSampleData(boolean insertProcessOrNot){
        JSONObject outputObject=new JSONObject();
        outputObject.putAll(formatConfig.getOutputInfoJson());
        for(String a:outputObject.keySet()){
            outputObject.put(a,dataObject.get(a));
        }
        if(insertProcessOrNot){
            JSONArray processInfoList=new JSONArray();
            for(Map.Entry<Integer,JSONObject> entry:processMap.entrySet()){
                processInfoList.add(entry.getValue());
            }
            outputObject.put("processInfoList",processInfoList);
        }

        return outputObject.toJSONString();
    }
    public String getHostName(){
        return dataObject.getString("hostName");
    }
    public static void main(String[] args) {
        DataSampler dataSampler = new DataSampler();
        dataSampler.processInfoSample(10,1);
    }
}
