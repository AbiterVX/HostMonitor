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
    private FormatLastSampleData formatLastSampleData;
    //静态硬件信息采样，不会周期性调用
    public DataSampler(){
        systemInfo = new SystemInfo();
        formatLastSampleData=new FormatLastSampleData();
    }
    public String hardWareSample(){
        JSONObject resultObject=new JSONObject();
        //操作系统部分
        OperatingSystem os = systemInfo.getOperatingSystem();
        JSONObject osObject=new JSONObject();
        osObject.put("type",os.getManufacturer()+" "+os.getFamily());
        osObject.put("version",os.getVersionInfo().toString());
        resultObject.put("OS",osObject);
        //CPU部分
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier identifier=centralProcessor.getProcessorIdentifier();
        JSONObject CPUObject=new JSONObject();
        CPUObject.put("type",identifier.getName());
        CPUObject.put("CoreNumber",centralProcessor.getPhysicalProcessorCount());
        resultObject.put("CPU",CPUObject);
        //磁盘
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        JSONArray diskArray=new JSONArray();
        for(HWDiskStore hwDiskStore:hwDiskStoreList){
            JSONObject tempDiskObject=new JSONObject();
            tempDiskObject.put("type",hwDiskStore.getModel());
            tempDiskObject.put("size",hwDiskStore.getSize() > 0L ? FormatUtil.formatBytesDecimal(hwDiskStore.getSize()) : "?");
            diskArray.add(tempDiskObject);
        }
        resultObject.put("Disks",diskArray);
        //GPU
        List<GraphicsCard> graphicsCards =  systemInfo.getHardware().getGraphicsCards();
        JSONArray GPUArray=new JSONArray();
        for (GraphicsCard graphicsCard:graphicsCards){
            JSONObject tempGPUObject=new JSONObject();
            tempGPUObject.put("type",graphicsCard.getName());
            tempGPUObject.put("RAMSize",graphicsCard.getVRam() > 0L ? FormatUtil.formatBytesDecimal(graphicsCard.getVRam()) : "?");
            GPUArray.add(tempGPUObject);
        }
        resultObject.put("GPU",GPUArray);
        return resultObject.toJSONString();
    }
    public String periodSample(int period){
        JSONObject resultObject=new JSONObject();
        //Memory利用率
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        long totalByte = globalMemory.getTotal();
        long availableByte = globalMemory.getAvailable();
        //formatUnits(,(1024*1024*1024),"GB");
        double memoryUsage = (totalByte-availableByte)*1.0/totalByte;
        resultObject.put("MemUsage",memoryUsage);

        //Cpu利用率
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();
        long[] value = new long[ticks.length];
        long totalCpu = 0;
        for(int i=0;i<value.length;i++){
            value[i] = ticks[i] - formatLastSampleData.preTicks[i];
            totalCpu += value[i];
        }
        double cpuUsage = 1.0-(value[CentralProcessor.TickType.IDLE.getIndex()] * 1.0 / totalCpu);
        resultObject.put("CpuUsage",new DecimalFormat("#.##%").format(cpuUsage));
        formatLastSampleData.preTicks=ticks;
        //Cpu温度
        Sensors sensors = systemInfo.getHardware().getSensors();
        resultObject.put("CpuTemperature",sensors.getCpuTemperature());

        //磁盘占用率/iops/速率
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        JSONArray diskArray=new JSONArray();
        List<partionInfo> pList=processing(systemInfo.getOperatingSystem().getFileSystem().getFileStores());
        for(HWDiskStore hwDiskStore:hwDiskStoreList){
            JSONObject tempDiskObject=new JSONObject();
            List<HWPartition> hwpList= hwDiskStore.getPartitions();
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
            String name=hwDiskStore.getName()+":"+hwDiskStore.getModel();
            tempDiskObject.put("name",name);
            tempDiskObject.put("usage",usage);
            long previousReadNumber=formatLastSampleData.DiskInfo.get(name+"-read");
            long previousReadBytes=formatLastSampleData.DiskInfo.get(name+"-readbytes");
            long previousWriteNumber=formatLastSampleData.DiskInfo.get(name+"-write");
            long previousWriteBytes=formatLastSampleData.DiskInfo.get(name+"-writebytes");
            long ReadNumber=hwDiskStore.getReads();
            long ReadBytes=hwDiskStore.getReadBytes();
            long WriteNumber=hwDiskStore.getWrites();
            long WriteBytes=hwDiskStore.getWriteBytes();
            double iops=(ReadNumber+WriteNumber-previousReadNumber-previousWriteNumber)*1.0/period;
            double ReadRates=(ReadBytes-previousReadBytes)*1.0/period;
            double WriteRates=(WriteBytes-previousWriteBytes)*1.0/period;
            tempDiskObject.put("iops",iops);
            tempDiskObject.put("readrates",ReadRates);
            tempDiskObject.put("writerates",WriteRates);
            formatLastSampleData.DiskInfo.put(name+"-read",ReadNumber);
            formatLastSampleData.DiskInfo.put(name+"-readbytes",ReadBytes);
            formatLastSampleData.DiskInfo.put(name+"-write",WriteNumber);
            formatLastSampleData.DiskInfo.put(name+"-writebytes",WriteBytes);
            diskArray.add(tempDiskObject);
        }
        resultObject.put("Disks",diskArray);
        System.out.println("Sample Finish");
        return  resultObject.toJSONString();
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
    private static String formatUnits(long value, long prefix, String unit) {
        return value % prefix == 0L ? String.format("%d %s", value / prefix, unit) : String.format("%.2f %s", (double)value / (double)prefix, unit);
    }
    public String firstPeriodSample(){
        JSONObject resultObject=new JSONObject();
        //Memory利用率
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();
        long totalByte = globalMemory.getTotal();
        long availableByte = globalMemory.getAvailable();
        //formatUnits(,(1024*1024*1024),"GB");
        double memoryUsage = (totalByte-availableByte)*1.0/totalByte;
        resultObject.put("MemUsage",memoryUsage);
        //Cpu利用率
        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();
        formatLastSampleData.preTicks=ticks;
        //Cpu温度
        Sensors sensors = systemInfo.getHardware().getSensors();
        resultObject.put("CpuTemperature",sensors.getCpuTemperature());
        //磁盘占用率/iops/速率
        List<HWDiskStore> hwDiskStoreList=systemInfo.getHardware().getDiskStores();
        JSONArray diskArray=new JSONArray();
        List<partionInfo> pList=processing(systemInfo.getOperatingSystem().getFileSystem().getFileStores());
        for(HWDiskStore hwDiskStore:hwDiskStoreList){
            JSONObject tempDiskObject=new JSONObject();
            List<HWPartition> hwpList= hwDiskStore.getPartitions();
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
            String name=hwDiskStore.getName()+":"+hwDiskStore.getModel();
            tempDiskObject.put("name",name);
            tempDiskObject.put("usage",usage);
            long ReadNumber=hwDiskStore.getReads();
            long ReadBytes=hwDiskStore.getReadBytes();
            long WriteNumber=hwDiskStore.getWrites();
            long WriteBytes=hwDiskStore.getWriteBytes();
            formatLastSampleData.DiskInfo.put(name+"-read",ReadNumber);
            formatLastSampleData.DiskInfo.put(name+"-readbytes",ReadBytes);
            formatLastSampleData.DiskInfo.put(name+"-write",WriteNumber);
            formatLastSampleData.DiskInfo.put(name+"-writebytes",WriteBytes);
            diskArray.add(tempDiskObject);
        }
        resultObject.put("Disks",diskArray);
        System.out.println("Sample Finish");
        return  resultObject.toJSONString();
    }
    public static void main(String[] args) {
        DataSampler dataSampler = new DataSampler();
    }
}
