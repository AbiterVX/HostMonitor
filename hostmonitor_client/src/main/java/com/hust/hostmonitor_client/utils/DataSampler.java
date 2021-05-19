package com.hust.hostmonitor_client.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;
import sun.nio.ch.Net;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    }
    public void hardWareSample(){
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        OperatingSystem os = systemInfo.getOperatingSystem();
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
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskName",tempDiskStore.getName()+":"+tempDiskStore.getModel());
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("diskCapacityTotalSize",tempDiskStore.getSize());
            dataObject.getJSONArray("diskInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
            totalsize+=tempDiskStore.getSize();
            //tempDiskObject.put("size",hwDiskStore.getSize() > 0L ? FormatUtil.formatBytesDecimal(hwDiskStore.getSize()) : "?");
            //diskArray.add(tempDiskObject);
        }
        dataObject.put("diskCapacityTotalSizeSum",totalsize);
        //resultObject.put("Disks",diskArray);
        //GPU
        List<GraphicsCard> graphicsCards =  systemInfo.getHardware().getGraphicsCards();
        for (i=0;i<graphicsCards.size();i++){
            GraphicsCard tempGraphicsCard=graphicsCards.get(i);
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuName",tempGraphicsCard.getName());
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("gpuAvailableRam",tempGraphicsCard.getVRam());
            dataObject.getJSONArray("gpuInfoList").getJSONObject(i).put("lastUpdateTime",timestamp);
        }
        //net interface
        List<NetworkIF> networkIFList=systemInfo.getHardware().getNetworkIFs();
        for(i=0;i<networkIFList.size();i++){
            NetworkIF tempNetworkIF=networkIFList.get(i);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(i).put("netInterfaceName",tempNetworkIF.getDisplayName());
        }
    }
    public void periodSample(int period,boolean isTheFirstTimeToSample){
        if(isTheFirstTimeToSample){
            firstSample();
            return;
        }
        //Memory利用率
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();

        //double memoryUsage = (totalByte-availableByte)*1.0/totalByte;
        dataObject.put("memoryTotalSize",globalMemory.getTotal());
        dataObject.put("memoryUsedSize",globalMemory.getTotal()-globalMemory.getAvailable());

        //Cpu利用率
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
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", cpuUsage);
        dataObject.put("cpuAverageUsage",cpuUsage);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("TotalTicks",newTotalTicks);
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("idleTick",ticks[CentralProcessor.TickType.IDLE.getIndex()]);

        //Cpu温度
        Sensors sensors = systemInfo.getHardware().getSensors();
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuTemperature",sensors.getCpuTemperature());

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
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskCapacityUsedSize",total-usable);
            totalUsedSize+=total-usable;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskUsage",usage);
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
                dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadSpeed", ReadRates);
                dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteSpeed", WriteRates);



            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",ReadNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",ReadBytes);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",WriteNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",WriteBytes);
        }

        dataObject.put("diskCapacityUsedSizeSum",totalUsedSize);
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
                dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("recvSpeed", NetRecv);
                dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("sentSpeed", NetSent);

            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("recvBytes",RecvBytes);
            dataObject.getJSONArray("netInterfaceList").getJSONObject(k).put("sentBytes",SentBytes);

        }
        dataObject.put("netReceiveSpeed",totalNetRecv);
        dataObject.put("netSendSpeed",totalNetSent);
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
    private void firstSample(){
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        //double memoryUsage = (totalByte-availableByte)*1.0/totalByte;
        dataObject.put("memoryTotalSize",globalMemory.getTotal());
        dataObject.put("memoryUsedSize",globalMemory.getTotal()-globalMemory.getAvailable());
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
        dataObject.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuTemperature",sensors.getCpuTemperature());
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
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskCapacityUsedSize",total-usable);
            totalUsedSize+=total-usable;
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskUsage",usage);
            long ReadNumber=hwInThisLoop.getReads();
            long ReadBytes=hwInThisLoop.getReadBytes();
            long WriteNumber=hwInThisLoop.getWrites();
            long WriteBytes=hwInThisLoop.getWriteBytes();
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",ReadNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",ReadBytes);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",WriteNumber);
            dataObject.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",WriteBytes);
        }
        dataObject.put("diskCapacityUsedSizeSum",totalUsedSize);
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
    private static String formatUnits(long value, long prefix, String unit) {
        return value % prefix == 0L ? String.format("%d %s", value / prefix, unit) : String.format("%.2f %s", (double)value / (double)prefix, unit);
    }
    public String outputSampleData(){
        return dataObject.toJSONString();
    }
    public static void main(String[] args) {
        DataSampler dataSampler = new DataSampler();
    }
}
