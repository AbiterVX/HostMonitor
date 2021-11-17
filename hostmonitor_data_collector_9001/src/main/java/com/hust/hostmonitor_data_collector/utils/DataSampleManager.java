package com.hust.hostmonitor_data_collector.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;

import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.*;
import com.hust.hostmonitor_data_collector.utils.linuxsample.LinuxDataProcess;
import com.hust.hostmonitor_data_collector.utils.linuxsample.LinuxGPU;
import org.apache.commons.io.FileUtils;
import org.python.modules._hashlib;

import com.hust.hostmonitor_data_collector.utils.SSHConnect.JschSSHManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.SSHManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


public class DataSampleManager {
    private CmdExecutor cmdExecutor;
    //Config配置信息
    private ConfigDataManager configDataManager= ConfigDataManager.getInstance();
    private OSType localOSType;
    //Host 配置信息
    public List<HostConfigData> hostList;
    public final SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
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
    //获取OS类型
    private OSType getOSType(HostConfigData hostConfigData){
        if(hostConfigData!=null){
            return hostConfigData.osType;
        }
        else{
            return localOSType;
        }
    }

    //设置本地OS类型
    public void setLocalOSType( OSType localOSType){
        this.localOSType = localOSType;
    }

    //硬件信息采样
    public JSONObject sampleHostHardwareData(HostConfigData hostConfigData){
        JSONObject sampleData = configDataManager.getSampleFormat("hostInfo");
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)) {
            sampleData.put("ip", hostConfigData.ip);
            //hostName
            {
                List<String> cmdResult = cmdExecutor.runCommand("hostname", hostConfigData, false);
                if (cmdResult.size() == 0) {
                    sampleData.put("connected", false);
                    return sampleData;
                }
                sampleData.put("hostName", cmdResult.get(0).trim());
            }
            //osName
            {
                List<String> cmdResult = cmdExecutor.runCommand("cat /proc/version", hostConfigData, false);
                sampleData.put("osName", cmdResult.get(0).trim());
            }
            //diskInfo
            {
                //diskName
                //diskCapacitySize
                //diskModel
                List<String> devs = cmdExecutor.runCommand("lsblk -bnd", hostConfigData, false);
                long totalsize = 0;
                for (String string : devs) {
                    if (string.contains("loop")) {
                        continue;
                    }
                    String[] tokens = string.split("\\s+");
                    String devsName = tokens[0];
                    String Model = "unknown", Serial = "unknown";
                    long size = Long.parseLong(tokens[3]);
                    List<String> diskInfo = cmdExecutor.runCommand("hdparm -i /dev/" + devsName, hostConfigData, true);
                    for (String info : diskInfo) {
                        if (info.contains("Model")) {
                            tokens = info.split(",");
                            for (String token : tokens) {
                                if (token.contains("Model")) {
                                    int index = token.indexOf("=");
                                    Model = token.substring(index + 1);
                                }
                                if (token.contains("Serial")) {
                                    int index = token.indexOf("=");
                                    Serial = token.substring(index + 1);
                                }
                            }
                            break;
                        }
                    }
                    JSONObject newDiskInfo = configDataManager.getSampleFormat("diskInfo");
                    {
                        String completeDiskName = null;
                        if (Serial.trim().equals("unknown")) {
                            completeDiskName = devsName + ":-unknown-" + hostConfigData.userName + "@" + hostConfigData.ip;
                        } else {
                            completeDiskName = devsName + ":" + Serial.trim();
                        }
                        newDiskInfo.put("diskName", completeDiskName);
                        newDiskInfo.put("diskModel", Model.trim());
                        newDiskInfo.put("diskTotalSize", LinuxDataProcess.doubleTo2bits_double(size * 1.0f / 1024 / 1024 / 1024));
                        newDiskInfo.put("type", 0);
                        JSONObject currentDiskData = new JSONObject();
                        List<String> cmdResult = cmdExecutor.runCommand("smartctl -i /dev/" + devsName, hostConfigData, true);
                        if (cmdResult.get(3).contains("Unable to")) {
                            sampleData.getJSONArray("diskInfoList").add(newDiskInfo);
                            totalsize += size;
                            continue;
                        }
                        for (int i = 0; i < 4; i++) {
                            cmdResult.remove(0);
                        }
                        cmdResult.remove(cmdResult.size() - 1);
                        for (String currentOutput : cmdResult) {
                            String[] rawData = currentOutput.split(":\\s+");
                            if (rawData[0].contains("Device Model")) {
                                newDiskInfo.put("diskModel", rawData[1]);
                                continue;
                            }
                            if (rawData[0].contains("Serial Number")) {
                                newDiskInfo.put("diskName", devsName + ":" + rawData[1]);
                                continue;
                            }
                            if (rawData[0].contains("Rotation Rate")) {
                                newDiskInfo.put("type", rawData[1].equals("Solid State Device") ? 1 : 0);
                                break;
                            }
                        }
                    }
                    sampleData.getJSONArray("diskInfoList").add(newDiskInfo);
                    totalsize += size;
                }
                sampleData.put("allDiskTotalSize", LinuxDataProcess.doubleTo2bits_double(totalsize * 1.0 / 1024 / 1024 / 1024));
            }
            //cpuInfoList
            {
                List<String> cmdResult = cmdExecutor.runCommand("cat /proc/cpuinfo", hostConfigData, false);
                for (String rowData : cmdResult) {
                    if (rowData.contains("model name")) {
                        JSONObject newCpuInfo = configDataManager.getSampleFormat("cpuInfo");
                        {
                            newCpuInfo.put("cpuName", rowData.split(":")[1]);
                        }
                        sampleData.getJSONArray("cpuInfoList").add(newCpuInfo);
                    }
                }
            }
            //gpuInfo
            {
                List<LinuxGPU> cardList = new ArrayList();
                List<String> lspci = cmdExecutor.runCommand("lspci -vnnm", hostConfigData, false);
                String name = "unknown";
                String deviceId = "unknown";
                String vendor = "unknown";
                List<String> versionInfoList = new ArrayList();
                boolean found = false;
                String lookupDevice = null;
                Iterator var8 = lspci.iterator();

                while (var8.hasNext()) {
                    String line = (String) var8.next();
                    String[] split = line.trim().split(":", 2);
                    String prefix = split[0];
                    if (prefix.equals("Class") && line.contains("VGA")) {
                        found = true;
                    } else if (prefix.equals("Device") && !found && split.length > 1) {
                        lookupDevice = split[1].trim();
                    }

                    if (found) {
                        if (split.length < 2) {
                            cardList.add(new LinuxGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), LinuxDataProcess.queryLspciMemorySize(lookupDevice, hostConfigData)));
                            versionInfoList.clear();
                            found = false;
                        } else {
                            Pair pair;
                            if (prefix.equals("Device")) {
                                pair = LinuxDataProcess.parseLspciMachineReadable(split[1].trim());
                                if (pair != null) {
                                    name = (String) pair.getA();
                                    deviceId = "0x" + (String) pair.getB();
                                }
                            } else if (prefix.equals("Vendor")) {
                                pair = LinuxDataProcess.parseLspciMachineReadable(split[1].trim());
                                if (pair != null) {
                                    vendor = (String) pair.getA() + " (0x" + (String) pair.getB() + ")";
                                } else {
                                    vendor = split[1].trim();
                                }
                            } else if (prefix.equals("Rev:")) {
                                versionInfoList.add(line.trim());
                            }
                        }
                    }
                }
                if (found) {
                    cardList.add(new LinuxGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), LinuxDataProcess.queryLspciMemorySize(lookupDevice, hostConfigData)));
                }
                for (LinuxGPU linuxGPU : cardList) {
                    JSONObject newGpuInfo = configDataManager.getSampleFormat("gpuInfo");
                    {
                        newGpuInfo.put("gpuName", linuxGPU.getName());
                        newGpuInfo.put("gpuAvailableRam", LinuxDataProcess.doubleTo2bits_double(linuxGPU.getVram() * 1.0f / 1024 / 1024 / 1024));
                    }
                    sampleData.getJSONArray("gpuInfoList").add(newGpuInfo);
                }
            }
            //lvm Info
            {
                List<String> VGInfo = cmdExecutor.runCommand("lvm pvscan", hostConfigData, true);
                JSONArray lvmInfoArray=new JSONArray();
                for(String string:VGInfo){
                    String[] tokens=string.trim().split("\\s+");
                    if(!tokens[0].equals("PV")){
                        continue;
                    }
                    JSONObject lvmObject=configDataManager.getSampleFormat("lvmInfo");
                    lvmObject.put("PVName",tokens[1]);
                    lvmObject.put("VGName",tokens[3]);
                    lvmInfoArray.add(lvmObject);
                }
                List<String> lvmInfo=cmdExecutor.runCommand("lvm lvdisplay",hostConfigData,true);
                int j=1;
                for(int i=0;i<lvmInfo.size();i+=j){
                    if(lvmInfo.get(i).contains("--- Logical volume ---")){
                        j=1;
                        String line;
                        String LVName="",VGName="",dmName="dm-";
                        while((i+j<lvmInfo.size())&&!(line=lvmInfo.get(i+j)).contains("--- Logical volume ---")){
                            if(line.contains("LV Name")){
                                LVName=line.trim().split("\\s+")[2];
                            }
                            if(line.contains("VG Name")){
                                VGName=line.trim().split("\\s+")[2];
                            }
                            if(line.contains("Block device")){
                                int postfix=Integer.parseInt(line.trim().split("\\s+")[2].split(":")[1]);
                                dmName+=postfix;
                            }
                            j++;
                        }
                        for(int m=0;m<lvmInfoArray.size();m++){
                            if(lvmInfoArray.getJSONObject(m).getString("VGName").equals(VGName)){
                                lvmInfoArray.getJSONObject(m).put("dmName",dmName);
                                lvmInfoArray.getJSONObject(m).getJSONArray("lvmInfo").add(LVName);
                            }
                        }
                    }
                }
                System.out.println(lvmInfoArray.toJSONString());
                sampleData.put("lvmInfo",lvmInfoArray);
            }

        }
        else if(osType.equals(OSType.WINDOWS)){
            //hostName
            {
                List<String> cmdResult = cmdExecutor.runCommand("hostname",hostConfigData,false);
                sampleData.put("hostName",cmdResult.get(0));
            }
            //osName
            {
                List<String> cmdResult = cmdExecutor.runCommand("ver",hostConfigData,false);
                if(cmdResult.size()>=2){
                    sampleData.put("osName",cmdResult.get(1));
                }
            }
            //diskInfo
            {
                Map<String,JSONObject> diskInfoMap = new HashMap<>();
                Map<String,String> logicalDiskMap = new HashMap<>();
                //物理盘信息：序列号，Model，获取逻辑分区与物理盘的映射
                {
                    List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-Partition | % {New-Object PSObject -Property @{'PartitionNumber'=$_.PartitionNumber; 'DiskNumber'=$_.DiskNumber; 'SerialNumber'=(Get-Disk $_.DiskNumber).SerialNumber; 'DiskModel'=(Get-Disk $_.DiskNumber).Model;'PartitionSize'=$_.Size; 'DriveLetter'=$_.DriveLetter;}}\"",hostConfigData,false);
                    JSONArray diskInfoList = sampleData.getJSONArray("diskInfoList");
                    String serialNumber = "";
                    long partitionSize =0;
                    int partitionNumber =0;
                    String diskModel = "";
                    String driveLetter ="";
                    for(String rowData:cmdResult){
                        if(!rowData.equals("")){
                            String[] diskData = rowData.split(":");
                            String columnName = diskData[0].trim();
                            String columnValue = diskData[1].trim();
                            if(columnName.equals("SerialNumber")){
                                serialNumber =columnValue;
                            }
                            else if(columnName.equals("PartitionSize")){
                                partitionSize =Long.parseLong(columnValue);
                            }
                            else if(columnName.equals("PartitionNumber")){
                                partitionNumber =Integer.parseInt(columnValue);
                            }
                            else if(columnName.equals("DiskModel")){
                                diskModel =columnValue;
                            }
                            else if(columnName.equals("DriveLetter")){
                                driveLetter =columnValue;
                                if(!diskInfoMap.containsKey(serialNumber)){
                                    JSONObject diskInfo = configDataManager.getSampleFormat("diskInfo");
                                    diskInfo.put("diskName",serialNumber);
                                    diskInfo.put("diskModel",diskModel);
                                    diskInfoMap.put(serialNumber,diskInfo);
                                }
                                //存放逻辑盘-物理盘的映射
                                if(!driveLetter.equals("")){
                                    logicalDiskMap.put(driveLetter+":",serialNumber);
                                }
                            }
                        }

                    }
                }
                //分区信息：大小，空闲大小
                long allDiskTotalSize=0;
                long allDiskTotalFreeSize =0;
                {
                    List<String> cmdResult = cmdExecutor.runCommand("wmic logicaldisk get size,freespace,caption",hostConfigData,false);
                    cmdResult.remove(0);
                    for(String rowData:cmdResult){
                        if(!rowData.equals("")){
                            String[] logicalDiskData = rowData.split("\\s+");
                            String caption = logicalDiskData[0];
                            long freeSpace = Long.parseLong(logicalDiskData[1]);
                            long size = Long.parseLong(logicalDiskData[2]);
                            //分区信息
                            JSONObject diskPartition = configDataManager.getSampleFormat("diskPartition");
                            diskPartition.put("driveLetter",caption);
                            diskPartition.put("size",size);
                            diskPartition.put("freeSize",freeSpace);

                            JSONObject diskInfo = diskInfoMap.get(logicalDiskMap.get(caption));
                            JSONArray diskPartitionList = diskInfo.getJSONArray("diskPartitionList");
                            diskPartitionList.add(diskPartition);
                            //计算当前disk的总容量，总空闲容量
                            long diskTotalSize = diskInfo.getLong("diskTotalSize");
                            long diskTotalFreeSize = diskInfo.getLong("diskTotalFreeSize");
                            diskTotalSize += size;
                            diskTotalFreeSize += freeSpace;
                            diskInfo.put("diskTotalSize",diskTotalSize);
                            diskInfo.put("diskTotalFreeSize",diskTotalFreeSize);

                            allDiskTotalSize += size;
                            allDiskTotalFreeSize +=freeSpace;
                        }
                    }
                }
                //放置
                Set<String> serialNumberList = diskInfoMap.keySet();
                for(String currentSerialNumber: serialNumberList){
                    sampleData.getJSONArray("diskInfoList").add(diskInfoMap.get(currentSerialNumber));
                }
                sampleData.put("allDiskTotalSize",allDiskTotalSize);
                sampleData.put("allDiskTotalFreeSize",allDiskTotalFreeSize);
            }
            //cpuInfoList
            {
                List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject Win32_Processor\"",hostConfigData,false);
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
                List<String> cmdResult = cmdExecutor.runCommand("wmic PATH Win32_VideoController GET Name,Adapterram",hostConfigData,false);
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


        if(hostConfigData!=null){
            sampleData.put("ip",hostConfigData.ip);
        }
        sampleData.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));
        sampleData.put("connected",true);
        sampleData.put("hasPersistent",false);
        System.out.println(sampleData.toJSONString());
        return sampleData;
    }
    private String readFile(String filePath){
        String resultData = "";
        File file = new File(System.getProperty("user.dir"),filePath);
        try {
            resultData = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    //节点性能采样
    public void sampleHostData(HostConfigData hostConfigData,JSONObject sampleData){
        if(!sampleData.getBoolean("connected")){
            return;
        }
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
            LinuxPeriodRecord record=new LinuxPeriodRecord();
            String sampleCommands=readFile("Scripts/SampleCommand.sh");  //test  //SampleCommand
            sampleCommands=sampleCommands.replaceAll("\r\n","\n");
            List<String> sampleInfo=cmdExecutor.runCommand( sampleCommands,hostConfigData,false);  //test  //SampleCommand,hostConfigData);
            List<String> mountUsageInfo = cmdExecutor.runCommand("df",hostConfigData,false); //查询结果使用量为KB
            if(sampleInfo.size()==0||mountUsageInfo.size()==0){
                return;
            }
            HashMap<String, Pair<Long,Long>> mountUsage=new HashMap<>();
            for (int i=0;i<mountUsageInfo.size();i++
            ) {
                if(i==0){
                    continue;
                }
                String[] parts=mountUsageInfo.get(i).trim().split("\\s+");
                Long used=Long.parseLong(parts[2]);
                Long usable=Long.parseLong(parts[3]);
                mountUsage.put(parts[5],new Pair<Long,Long>(used,usable));
            }
            mountUsageInfo=null;//释放
            List<String> CPUInfo = cmdExecutor.runCommand("cat /proc/stat | grep cpu",hostConfigData,false);
            {
                for (String string : CPUInfo) {
                    String[] tokens = string.split("\\s+");

                    if (tokens[0].contains("cpu")) {
                        if(tokens[0].equals("cpu")){
                            long total = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) + Long.parseLong(tokens[4])
                                    + Long.parseLong(tokens[5]) + Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                            long used = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) +
                                    Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                            record.setAllCPUtotal(total);
                            record.setAllCPUused(used);
                        }
                        if(tokens.length>3){
                            long total = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) + Long.parseLong(tokens[4])
                                    + Long.parseLong(tokens[5]) + Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                            long used = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) +
                                    Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                            record.getCPUused().add(used);
                            record.getCPUtotal().add(total);
                        }
                    }
                }

                Iterator<String> itr = sampleInfo.iterator();
                while (itr.hasNext()) {
                    String currentString = itr.next();
                    if (currentString.contains("SampleCommand.sh")) {
                        continue;
                    }
                    String[] tokens = currentString.split(":");
                    if (tokens[0].equals("MemTotal")) {
                        record.setMemTotal(Long.parseLong(tokens[1]));
                    } else if (tokens[0].equals("MemFree")) {
                        record.setMemFree(Long.parseLong(tokens[1]));
                    } else if (tokens[0].equals("MemAvailable")) {
                        record.setMemAvailable(Long.parseLong(tokens[1]));
                    } else if (tokens[0].equals("NetSend")) {
                        record.setNetSend(Long.parseLong(tokens[1]));
                    } else if (tokens[0].equals("NetReceive")) {
                        record.setNetReceive(Long.parseLong(tokens[1]));
                    } else if (tokens[0].equals("Power")) {
                        record.setCPUTemperature(40.0);
                    }
                    //TODO 磁盘使用量计算方法修改 disk_util,修改为通过挂载点计算
                    else if (tokens[0].contains("Disk_Iops")) {
                        DiskInfo tempDiskInfo = new DiskInfo();
                        String diskName = tokens[0].split("_")[2];
                        JSONArray lvmInfo=sampleData.getJSONArray("lvmInfo");
                        if(diskName.contains("dm")){
                            int index=-1;
                            for(int i=0;i<lvmInfo.size();i++){
                                if(lvmInfo.getJSONObject(i).getString("dmName").equals(diskName)){
                                    index=i;
                                    break;
                                }
                            }
                            if(index==-1){
                                continue;
                            }
                            tempDiskInfo.diskName= lvmInfo.getJSONObject(index).getString("PVName");
                            tempDiskInfo.diskName=tempDiskInfo.diskName.split("/")[2].substring(0,3);
                            tempDiskInfo.diskIOPS = Double.parseDouble(tokens[1]);
                            currentString = itr.next();
                            double readSpeed = Double.parseDouble(currentString.split(":")[1]);
                            currentString = itr.next();
                            double writeSpeed = Double.parseDouble(currentString.split(":")[1]);
                            currentString = itr.next();
                            double utilRadio = Double.parseDouble(currentString.split(":")[1]);
                            tempDiskInfo.diskReadSpeed = readSpeed;
                            tempDiskInfo.diskWriteSpeed = writeSpeed;
                            tempDiskInfo.diskUsedRadio = utilRadio;
                            List<String> dmUsageInfo = cmdExecutor.runCommand("df /dev/" + diskName, hostConfigData, true);
                            long diskUsedAmount = 0;
                            for (String UsageInfo : dmUsageInfo) {
                                if (UsageInfo.contains("Filesystem")) {
                                    continue;
                                }
                                diskUsedAmount=Long.parseLong(UsageInfo.trim().split("\\s+")[2]);
                            }
                           //KB为单位
                            tempDiskInfo.diskFSUsageAmount = diskUsedAmount;
                            record.getDisks().add(tempDiskInfo);


                        }
                        else {
                            tempDiskInfo.diskName = diskName;
                            tempDiskInfo.diskIOPS = Double.parseDouble(tokens[1]);
                            currentString = itr.next();
                            double readSpeed = Double.parseDouble(currentString.split(":")[1]);
                            currentString = itr.next();
                            double writeSpeed = Double.parseDouble(currentString.split(":")[1]);
                            currentString = itr.next();
                            double utilRadio = Double.parseDouble(currentString.split(":")[1]);
                            tempDiskInfo.diskReadSpeed = readSpeed;
                            tempDiskInfo.diskWriteSpeed = writeSpeed;
                            tempDiskInfo.diskUsedRadio = utilRadio;


                            ArrayList<String> mountPoints = new ArrayList<>();
                            List<String> devMountInfo = cmdExecutor.runCommand("lsblk /dev/" + diskName, hostConfigData, false);
                            for (String mountString : devMountInfo) {
                                if (mountString.contains("NAME")) {
                                    continue;
                                }
                                String[] parts = mountString.trim().split("\\s+");
                                if (parts.length == 6) {
                                    continue;
                                } else {
                                    mountPoints.add(parts[6]);
                                }
                            }
                            long diskUsedAmount = 0;
                            for (String mountPoint : mountPoints) {
                                if (mountUsage.containsKey(mountPoint)) {
                                    diskUsedAmount += mountUsage.get(mountPoint).getA();
                                }
                            }
                            tempDiskInfo.diskFSUsageAmount = diskUsedAmount;
                            record.getDisks().add(tempDiskInfo);
                        }
                    }
                }
                //DiskStore合并
                Collections.sort(record.getDisks());
                String tempDiskName=null;
                ArrayList<DiskInfo> afterMergeInfo=new ArrayList<>();
                for(int i=0;i<record.getDisks().size();i++){
                    if(tempDiskName==null||!record.getDisks().get(i).diskName.equals(tempDiskName)){
                        afterMergeInfo.add(record.getDisks().get(i));
                        tempDiskName=record.getDisks().get(i).diskName;
                    }
                    else if(record.getDisks().get(i).diskName.equals(tempDiskName)){
                        DiskInfo tempDiskInfo=afterMergeInfo.get(afterMergeInfo.size()-1);
                        tempDiskInfo.diskIOPS+=record.getDisks().get(i).diskIOPS;
                        tempDiskInfo.diskReadSpeed+=record.getDisks().get(i).diskReadSpeed;
                        tempDiskInfo.diskWriteSpeed+=record.getDisks().get(i).diskWriteSpeed;
                        tempDiskInfo.diskFSUsageAmount+=record.getDisks().get(i).diskFSUsageAmount;
                    }
                }


            }
            //CPU
            {
                if(sampleData.getJSONArray("cpuInfoList").getJSONObject(0).containsKey("lastTotalTicks"))
                {
                    int cpuSize=sampleData.getJSONArray("cpuInfoList").size();
                    for(int i=0;i<cpuSize;i++) {
                        long oldTotalTicks = sampleData.getJSONArray("cpuInfoList").getJSONObject(i).getLong("lastTotalTicks");
                        long oldUsedTicks = sampleData.getJSONArray("cpuInfoList").getJSONObject(i).getLong("lastUsedTicks");
                        long newTotalTicks = record.getCPUtotal().get(i);
                        long newUsedTicks = record.getCPUused().get(i);
                        double cpuUsage = 0;
                        if (newTotalTicks - oldTotalTicks == 0) {
                        } else {
                            cpuUsage = (newUsedTicks - oldUsedTicks) * 1.0f / (newTotalTicks - oldTotalTicks);
                        }
                        double cpuUsage2bits = LinuxDataProcess.doubleTo2bits_double(cpuUsage * 100);
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("cpuUsage", cpuUsage2bits);
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("lastTotalTicks",record.getCPUtotal().get(i));
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("lastUsedTicks",record.getCPUused().get(i));
                    }
                    {
                        long oldTotalTicks = sampleData.getLong("lastTotalTicks");
                        long oldUsedTicks = sampleData.getLong("lastUsedTicks");
                        long newTotalTicks=record.getAllCPUtotal();
                        long newUsedTicks=record.getAllCPUused();
                        double cpuUsage = (newUsedTicks - oldUsedTicks)* 1.0f / (newTotalTicks - oldTotalTicks) ;
                        double cpuUsage2bits = LinuxDataProcess.doubleTo2bits_double(cpuUsage * 100);
                        sampleData.put("cpuUsage", cpuUsage2bits);
                        sampleData.put("lastTotalTicks",newTotalTicks);
                        sampleData.put("lastUsedTicks",newUsedTicks);
                    }
                }
                else {
                    int cpuSize=sampleData.getJSONArray("cpuInfoList").size();
                    for(int i=0;i<cpuSize;i++){
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("cpuUsage", LinuxDataProcess.doubleTo2bits_double(0.0 * 100));
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("lastTotalTicks",record.getCPUtotal().get(i));
                        sampleData.getJSONArray("cpuInfoList").getJSONObject(i).put("lastUsedTicks",record.getCPUused().get(i));
                    }
                    {
                        sampleData.put("cpuUsage",  LinuxDataProcess.doubleTo2bits_double(0.0 * 100));
                        sampleData.put("lastTotalTicks",record.getAllCPUtotal());
                        sampleData.put("lastUsedTicks",record.getAllCPUused());
                    }
                }
            }
            //Memory
            {
                JSONArray memoryUsage = new JSONArray();
                memoryUsage.add(LinuxDataProcess.doubleTo2bits_double((record.getMemTotal() - record.getMemAvailable())*1.0/1024) );
                memoryUsage.add(LinuxDataProcess.doubleTo2bits_double(record.getMemTotal() * 1.0 / 1024 ));
                sampleData.put("memoryUsage",memoryUsage);
            }

            //NetIO
            {
                sampleData.put("netSendSpeed",LinuxDataProcess.doubleTo2bits_double(record.getNetSend()*1.0f/1024));
                sampleData.put("netReceiveSpeed",LinuxDataProcess.doubleTo2bits_double(record.getNetReceive()*1.0f/1024));
            }
            //Disk
            {
                List<DiskInfo> DiskStoreList=record.getDisks();
                long totalUsedSize=0;
                for(int j=0;j<DiskStoreList.size();j++){
                    DiskInfo tempDiskInfo=DiskStoreList.get(j);
                    int i=findDiskIndex(tempDiskInfo.diskName,sampleData);
                    if(i==-1){
                        //System.out.println(tempDiskInfo.diskName +" not found.");
                        continue;
                    }
                    double usage2bits=0.0;
                    try {
                        usage2bits = LinuxDataProcess.doubleTo2bits_double(tempDiskInfo.diskUsedRadio);
                    } catch (Exception e) {
                        System.out.println("usage2bitsError");
                    }

                    double singleTotalSize=sampleData.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskTotalSize");
                    double singleUsedSize=tempDiskInfo.diskFSUsageAmount*1.0f/1024/1024;

                    totalUsedSize+=singleUsedSize;
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskUsage",usage2bits);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskTotalFreeSize",LinuxDataProcess.doubleTo2bits_double((singleTotalSize-singleUsedSize)));
                    double ReadRates = tempDiskInfo.diskReadSpeed;
                    double WriteRates = tempDiskInfo.diskWriteSpeed;
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskIOPS", tempDiskInfo.diskIOPS);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskReadSpeed", LinuxDataProcess.doubleTo2bits_double(ReadRates));
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskWriteSpeed", LinuxDataProcess.doubleTo2bits_double(WriteRates));
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskRead",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskReadBytes",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskWrite",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskWriteBytes",0);
                }
                sampleData.put("allDiskTotalFreeSize",LinuxDataProcess.doubleTo2bits_double(sampleData.getDouble("allDiskTotalSize")-totalUsedSize));
            }
        }
        else if(osType.equals(OSType.WINDOWS)){
            //CPU
            {
                {
                    //Cpu Usage
                    List<String> cmdResult = cmdExecutor.runCommand("wmic cpu get loadpercentage", hostConfigData,false);
                    cmdResult.remove(0);
                    int currentIndex = 0;
                    float averageCpuUsage = 0;
                    for (String rowData : cmdResult) {
                        if (!rowData.equals("")) {
                            JSONObject cpuInfo = sampleData.getJSONArray("cpuInfoList").getJSONObject(currentIndex);
                            float currentCpuUsage = Float.parseFloat(rowData.trim());
                            cpuInfo.put("cpuUsage", currentCpuUsage);
                            currentIndex += 1;
                            averageCpuUsage +=currentCpuUsage;
                        }
                    }
                    sampleData.put("cpuUsage",averageCpuUsage/currentIndex);
                }
                //Cpu Temperature
                {
                    List<String> cmdResult = cmdExecutor.runCommand("wmic /namespace:\\\\root\\wmi PATH MSAcpi_ThermalZoneTemperature get CriticalTripPoint, CurrentTemperature",hostConfigData,false);
                    cmdResult.remove(0);
                    int currentIndex = 0;
                    float averageCpuTemperature = 0;
                    for(String rowData:cmdResult){
                        if(!rowData.equals("")){
                            JSONObject cpuInfo = sampleData.getJSONArray("cpuInfoList").getJSONObject(currentIndex);
                            float currentCpuTemperature = Float.parseFloat(rowData.trim());
                            cpuInfo.put("cpuTemperature", currentCpuTemperature);
                            currentIndex += 1;
                        }
                    }
                }
            }
            //Net IO
            {
                long[] sendBytes = {0,0};
                long[] ReceiveBytes = {0,0};
                int sampleDelayMS = 200;
                {
                    List<String> cmdResult = cmdExecutor.runCommand("chcp 437 && netstat -e && chcp 936",hostConfigData,false);
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
                    List<String> cmdResult = cmdExecutor.runCommand("chcp 437 && netstat -e && chcp 936",hostConfigData,false);
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
                //分区的IO详情
                Map<String,JSONObject> partitionInfoMap = new HashMap<>();
                {
                    List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject -query { SELECT * FROM Win32_PerfFormattedData_PerfDisk_LogicalDisk}\"",hostConfigData,false);
                    for(int i=0;i<2;i++){
                        cmdResult.remove(0);
                    }

                    String partitionName = "";
                    float diskReadBytesPersec = 0;
                    float diskWriteBytesPersec = 0;
                    float percentIdleTime = 0;
                    float diskTransfersPersec = 0;
                    for(String rowData:cmdResult){
                        if(rowData.startsWith("Name")){
                            String[] diskData = rowData.split(":");
                            partitionName = diskData[1].trim();
                        }
                        else if(rowData.startsWith("DiskReadBytesPersec")){
                            //读速度
                            String[] diskData = rowData.split(":");
                            diskReadBytesPersec = Float.parseFloat(diskData[1].trim());
                        }
                        else if(rowData.startsWith("DiskWriteBytesPersec")){
                            //写速度
                            String[] diskData = rowData.split(":");
                            diskWriteBytesPersec = Float.parseFloat(diskData[1].trim());
                        }
                        else if(rowData.startsWith("PercentIdleTime")){
                            //空闲率
                            String[] diskData = rowData.split(":");
                            percentIdleTime = Float.parseFloat(diskData[1].trim());
                        }
                        else if(rowData.startsWith("DiskTransfersPersec")){
                            //IOPS
                            String[] diskData = rowData.split(":");
                            diskTransfersPersec = Float.parseFloat(diskData[1].trim());
                        }
                        else if(rowData.startsWith("PSComputerName")){
                            //结束
                            JSONObject newPartitionInfo = new JSONObject();
                            newPartitionInfo.put("readSpeed",diskReadBytesPersec);
                            newPartitionInfo.put("writeSpeed",diskWriteBytesPersec);
                            newPartitionInfo.put("percentIdleTime",percentIdleTime);
                            newPartitionInfo.put("IOPS",diskTransfersPersec);
                            partitionInfoMap.put(partitionName,newPartitionInfo);
                        }
                    }
                }
                //设置Disk IO
                JSONArray diskInfoList = sampleData.getJSONArray("diskInfoList");
                for(int i=0;i<diskInfoList.size();i++){
                    JSONObject diskInfo = diskInfoList.getJSONObject(i);
                    JSONArray diskPartitionList = diskInfo.getJSONArray("diskPartitionList");
                    //set每个分区
                    float diskIOPS = 0;
                    float diskReadSpeed = 0;
                    float diskWriteSpeed = 0;
                    for(int j=0;j<diskPartitionList.size();j++){
                        JSONObject diskPartition = diskPartitionList.getJSONObject(j);
                        String driveLetter = diskPartition.getString("driveLetter");
                        if(partitionInfoMap.containsKey(driveLetter)){
                            JSONObject partitionInfo = partitionInfoMap.get(driveLetter);
                            float IOPS = partitionInfo.getFloat("IOPS");
                            float readSpeed = partitionInfo.getFloat("readSpeed");
                            float writeSpeed = partitionInfo.getFloat("writeSpeed");
                            diskPartition.put("IOPS",IOPS);
                            diskPartition.put("readSpeed",readSpeed);
                            diskPartition.put("writeSpeed",writeSpeed);
                            diskIOPS += IOPS;
                            diskReadSpeed += readSpeed;
                            diskWriteSpeed += writeSpeed;
                        }
                    }
                    //统计每个硬盘
                    diskInfo.put("diskIOPS",diskIOPS);
                    diskInfo.put("diskReadSpeed",diskReadSpeed);
                    diskInfo.put("diskWriteSpeed",diskWriteSpeed);
                }
            }
            //Memory
            {
                long totalMemory = 0;
                long freeMemory = 0;
                List<String> cmdResult = cmdExecutor.runCommand("wmic OS GET FreePhysicalMemory /value && wmic ComputerSystem GET TotalPhysicalMemory  /value",hostConfigData,false);
                for(String rowData:cmdResult){
                    if(!rowData.equals("")){
                        String[] memoryData = rowData.split("=");
                        String key = memoryData[0];
                        long value = Long.parseLong(memoryData[1]);
                        if(key.equals("FreePhysicalMemory")){
                            freeMemory = value;
                        }
                        else if(key.equals("TotalPhysicalMemory")){
                            totalMemory = value;
                        }
                    }
                }
                JSONArray memoryUsage = sampleData.getJSONArray("memoryUsage");
                memoryUsage.set(0,totalMemory-freeMemory);
                memoryUsage.set(0,totalMemory);
            }
        }
        sampleData.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));
        sampleData.put("connected",true);
        sampleData.put("hasPersistent",false);
        System.out.println(sampleData.toJSONString());
    }
    private int findDiskIndex(String diskName,JSONObject dataObject){
        for(int i=0;i<dataObject.getJSONArray("diskInfoList").size();i++){
            if( dataObject.getJSONArray("diskInfoList").getJSONObject(i).getString("diskName").contains(diskName)){
                return i;
            }
        }
        return -1;
    }

    //节点进程采样
    public void sampleHostProcess(HostConfigData hostConfigData,JSONObject sampleData){
        if(!sampleData.getBoolean("connected")){
            return;
        }
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
            JSONArray processInfoList=new JSONArray();
            List<String> ProcessInfo = cmdExecutor.runCommand("top -b -n 1 ",hostConfigData,false);
            boolean reachProcesses=false;
            for(String string:ProcessInfo){
                if(string.contains("PID")){
                    reachProcesses=true;
                    continue;
                }
                if(!reachProcesses){
                    continue;
                }
                //System.out.println("process +1");
                String[] tokens=string.trim().split("\\s+");
                //System.out.println(tokens[0]);
                int PID=Integer.parseInt(tokens[0]);
                String Name=tokens[11];
                if(Name.equals("top")){
                    continue;
                }
                double cpuUsage=Double.parseDouble(tokens[8]);
                cpuUsage=cpuUsage/sampleData.getJSONArray("cpuInfoList").size();
                double memoryUsage=Double.parseDouble(tokens[9]);
                //TODO 时间算法有问题，以后再debug
                String[] times=tokens[10].split(":");
                long time=new Date().getTime();
                long useTime=new Double(Integer.parseInt(times[0])*60*1000+Double.parseDouble(times[1])*1000).longValue();
                JSONObject newProcess = new JSONObject();
                newProcess.put("processId",PID);
                newProcess.put("processName",Name);
                newProcess.put("startTime",new Timestamp(time-useTime));
                newProcess.put("cpuUsage",cpuUsage);
                newProcess.put("memoryUsage",memoryUsage);
                newProcess.put("diskReadSpeed",0.0f);
                newProcess.put("diskWriteSpeed",0.0f);
                if(cpuUsage==0&&memoryUsage==0){
                    continue;
                }
                else {
                    processInfoList.add(newProcess);
                }
            }
            sampleData.put("processInfoList",processInfoList);

        }
        else if(osType.equals(OSType.WINDOWS)){
            JSONArray processInfoList = new JSONArray();
            List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject -query { SELECT CreatingProcessID,Name,ElapsedTime,workingset,percentProcessorTime,ioReadBytesPersec,ioWriteBytesPersec  FROM Win32_PerfFormattedData_PerfProc_Process  }\"",hostConfigData,false);
            int creatingProcessID = 0;
            String name = "";
            long elapsedTime = 0;
            long workingset = 0;
            float percentProcessorTime = 0;
            float ioReadBytesPersec = 0;
            float ioWriteBytesPersec = 0;

            for(String rowData:cmdResult){
                if(!rowData.equals("")){
                    String[] processData = rowData.split(":");
                    String value = processData[1].trim();
                    if(rowData.startsWith("CreatingProcessID")){
                        //进程ID
                        creatingProcessID = Integer.parseInt(value);
                    }
                    else if(rowData.startsWith("ElapsedTime")){
                        //时间
                        elapsedTime = Long.parseLong(value);
                    }
                    else if(rowData.startsWith("IOReadBytesPersec")){
                        //IO读速度
                        ioReadBytesPersec = Float.parseFloat(value);
                    }
                    else if(rowData.startsWith("IOWriteBytesPersec")){
                        //IO写速度
                        ioWriteBytesPersec = Float.parseFloat(value);
                    }
                    else if(rowData.startsWith("Name")){
                        //进程名称
                        name = value;
                    }
                    else if(rowData.startsWith("PercentProcessorTime")){
                        //CPU利用率
                        percentProcessorTime = Float.parseFloat(value);
                    }
                    else if(rowData.startsWith("WorkingSet")){
                        //内存
                        workingset = Long.parseLong(value);
                    }
                    else if(rowData.startsWith("PSComputerName")){
                        //保存
                        JSONObject processInfo = configDataManager.getSampleFormat("processInfo");
                        processInfo.put("processId",creatingProcessID);
                        processInfo.put("processName",name);
                        processInfo.put("startTime",elapsedTime);
                        processInfo.put("cpuUsage",percentProcessorTime);
                        processInfo.put("memoryUsage",workingset);
                        processInfo.put("diskReadSpeed",ioReadBytesPersec);
                        processInfo.put("diskWriteSpeed",ioWriteBytesPersec);
                        processInfoList.add(processInfo);
                    }
                }
            }
            sampleData.put("processInfoList",processInfoList);
        }
        sampleData.put("connected",true);
    }
    public void sampleHostSmart(HostConfigData hostConfigData){
        OSType osType = getOSType(hostConfigData);
        //获取硬盘smart数据
        JSONObject diskData = new JSONObject();
        {
            //获取硬盘名
            List<String> diskList = new ArrayList<>();
            {
                String getDiskListCmd = "";
                if(osType.equals(OSType.WINDOWS)){
                    getDiskListCmd = "wmic logicaldisk get deviceid";
                    List<String> cmdResult = cmdExecutor.runCommand(getDiskListCmd,hostConfigData,false);
                    cmdResult.remove(0);
                    for(String currentStr:cmdResult){
                        if(!currentStr.equals("")){
                            diskList.add(currentStr.trim());
                        }
                    }
                }
                else if(osType.equals(OSType.LINUX)){
                    getDiskListCmd = "lsblk -bnd";
                    List<String> cmdResult = cmdExecutor.runCommand(getDiskListCmd,hostConfigData,false);
                    for(String currentStr:cmdResult) {
                        if(currentStr.contains("loop")){
                            continue;
                        }
                        String[] rawData = currentStr.split("\\s+");
                        //System.out.println(rawData.length);
                        diskList.add("/dev/"+rawData[0]);
                        //System.out.println("/dev/"+rawData[0]);

                    }
                }
            }
            //以Json格式存数据
            String smartDiskInfoCmd = "smartctl -i ";
            String smartDataSampleCmd = "smartctl -A ";
            for(String currentDiskName: diskList){
                JSONObject currentDiskData = new JSONObject();
                {
                    List<String> cmdResult = cmdExecutor.runCommand(smartDiskInfoCmd + currentDiskName,hostConfigData,true);
                    if(cmdResult.get(3).contains("Unable to")){
                        continue;
                    }
                    for (int i = 0; i < 4; i++) {
                        cmdResult.remove(0);
                    }
                    cmdResult.remove(cmdResult.size() - 1);
                    for(String currentOutput: cmdResult){
                        String[] rawData = currentOutput.split(":\\s+");
                        currentDiskData.put(rawData[0],rawData[1]);
                    }
                }

                String serialNumber = currentDiskData.getString("Serial Number");
                if(!diskData.containsKey(serialNumber)){
                    diskData.put(serialNumber,currentDiskData);
                    JSONObject smartData = new JSONObject();
                    {
                        List<String> cmdResult = cmdExecutor.runCommand(smartDataSampleCmd + currentDiskName,hostConfigData,true);
                        for (int i = 0; i < 7; i++) {
                            cmdResult.remove(0);
                        }
                        for(String currentOutput: cmdResult){
                            if(!currentOutput.equals("")){
                                currentOutput = currentOutput.trim();
                                String[] rawData = currentOutput.split("\\s+");
                                JSONObject currentSmart = new JSONObject();
                                currentSmart.put("ATTRIBUTE_NAME",rawData[1]);
                                currentSmart.put("VALUE",rawData[3]);
                                currentSmart.put("RAW_VALUE",rawData[9]);
                                smartData.put(rawData[0],currentSmart);
                            }
                        }
                        currentDiskData.put("SmartData",smartData);
                    }
                }
            }
        }

        //获取当前时间:
        String currentDate = "";
        String pt_d = "";
        {
            Date date = new Date();
            {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy/MM/dd HH:mm");
                currentDate = sdf.format(date);
            }
            {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyyMMdd");
                pt_d = sdf.format(date);
            }
        }

        Calendar calendar= Calendar.getInstance();
        String sampleDataFilePath=System.getProperty("user.dir")+"/DiskPredict/original_data/"+ calendar.get(Calendar.YEAR);
        File file=new File(sampleDataFilePath);
        if(!file.exists()){
            file.mkdir();
        }
        sampleDataFilePath=sampleDataFilePath+"/"+(calendar.get(Calendar.MONTH)+1);
        file=new File(sampleDataFilePath);
        if(!file.exists()){
            file.mkdir();
        }
        sampleDataFilePath=sampleDataFilePath+"/"+sdf.format(calendar.getTime())+".csv";
        file=new File(sampleDataFilePath);
        int smartCount = 256;
        if(!file.exists()){
            CsvWriter csvWriter = new CsvWriter(sampleDataFilePath,',', Charset.forName("GBK"));
            try {
                //Smart属性个数
                //获取表头
                List<String> headers = new ArrayList<>();
                {
                    String[] staticHeaders = {"date", "serial_number", "model", "ip", "failure", "is_ssd", "pt_d"};
                    for(String staticHeader:staticHeaders){
                        headers.add(staticHeader);
                    }

                    String[] smartTagAttributes = {"_normalized","_raw"};
                    for(int i=0;i<smartCount;i++){
                        for(String attribute: smartTagAttributes){
                            headers.add("smart_"+ Integer.toString(i) + attribute);
                        }
                    }
                }

                //写入header头
                csvWriter.writeRecord(headers.toArray(new String[0]));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                csvWriter.close();
            }
        }
        try {
                FileOutputStream innerFileStream=new FileOutputStream(sampleDataFilePath,true);
                CsvWriter csvWriter = new CsvWriter(innerFileStream,',', Charset.forName("GBK"));
                //写入数据
                Set<String> serialNumberList = diskData.keySet();
                for(String serialNumber:serialNumberList){
                    JSONObject currentDiskData = diskData.getJSONObject(serialNumber);
                    List<String> rowData = new ArrayList<>();
                    {
                        rowData.add(currentDate);
                        rowData.add(serialNumber);
                        rowData.add(currentDiskData.getString("Device Model"));
                        rowData.add(hostConfigData.ip);
                        rowData.add("0");
                        //是否为SSD
                        if(currentDiskData.getString("Rotation Rate").equals("Solid State Device")){
                            rowData.add("1");
                        }
                        else{
                            rowData.add("0");
                        }
                        rowData.add(pt_d);
                        JSONObject smartData = currentDiskData.getJSONObject("SmartData");
                        //@Todo 0到255
                        for(int i=0;i<smartCount;i++){
                            JSONObject currentSmart = smartData.getJSONObject(Integer.toString(i));
                            if(currentSmart != null){
                                rowData.add(currentSmart.getString("VALUE"));
                                rowData.add(currentSmart.getString("RAW_VALUE"));
                            }
                            else{
                                rowData.add("");
                                rowData.add("");
                            }
                        }
                    }
                    csvWriter.writeRecord(rowData.toArray(new String[0]));
                }
                csvWriter.close();
                innerFileStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    //IO测试
    public JSONObject ioTest(HostConfigData hostConfigData){
        JSONObject ioTestData = new JSONObject();
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
            String sampleCommands=readFile("Scripts/SpeedTest.sh");  //test  //SampleCommand
            sampleCommands=sampleCommands.replaceAll("\r\n","\n");
            List<String> cmdResult = cmdExecutor.runCommand(sampleCommands,hostConfigData,false);
            ioTestData.put("writeSpeed",cmdResult.get(0));
            ioTestData.put("readSpeed",cmdResult.get(1));
        }
        else if(osType.equals(OSType.WINDOWS)){
            List<String> cmdResult = cmdExecutor.runCommand("winsat disk",hostConfigData,false);
            for(String currentOutput: cmdResult){
                String[] rawData = currentOutput.split("\\s+");
                if(currentOutput.contains("Disk  Sequential 64.0 Read")){
                    ioTestData.put("readSpeed",rawData[5] +" "+ rawData[6]);
                }
                else if(currentOutput.contains("Disk  Sequential 64.0 Write")){
                    ioTestData.put("writeSpeed",rawData[5] +" "+ rawData[6]);
                }
            }
        }
        return ioTestData;
    }
    private void checkCommandResult(List<String> result,JSONObject tempObject){
        if(result.size()==0){
            tempObject.put("connected",false);
        }
    }

    public static void main(String[] args) {
        DataSampleManager dataSampleManager = DataSampleManager.getInstance();
        dataSampleManager.setLocalOSType(OSType.WINDOWS);
        JSONObject sampleData = dataSampleManager.sampleHostHardwareData(null);
        //System.out.println(sampleData);
        dataSampleManager.sampleHostData(null,sampleData);
        //dataSampleManager.sampleHostProcess(null,sampleData);
        System.out.println(sampleData);
        JSONObject ioTestData = dataSampleManager.ioTest(null);
        System.out.println(ioTestData);
    }
}
