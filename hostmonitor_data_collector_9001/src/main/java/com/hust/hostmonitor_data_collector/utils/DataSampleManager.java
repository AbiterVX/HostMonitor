package com.hust.hostmonitor_data_collector.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;
import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.DiskInfo;
import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.LinuxProcess;
import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.LinuxPeriodRecord;
import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.Pair;
import com.hust.hostmonitor_data_collector.utils.linuxsample.LinuxDataProcess;
import com.hust.hostmonitor_data_collector.utils.linuxsample.LinuxGPU;

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
            //hostName
            {
                List<String> cmdResult=cmdExecutor.runCommand("hostname",hostConfigData);
                sampleData.put("hostName",cmdResult.get(0).trim());
            }
            //osName
            {
                List<String> cmdResult = cmdExecutor.runCommand("cat /proc/version",hostConfigData);
                sampleData.put("osName",cmdResult.get(0).trim());
            }
            //diskInfo
            {
                //diskName
                //diskCapacitySize
                //diskModel
                List<String> devs=cmdExecutor.runCommand("lsblk -bnd",hostConfigData);
                long totalsize=0;
                for(String string:devs) {
                    String[] tokens = string.split("\\s+");
                    String devsName = tokens[0];
                    System.out.println(devsName);
                    String Model = "unknown", Serial = "unknown";
                    long size = Long.parseLong(tokens[3]);
                    List<String> diskInfo = cmdExecutor.runCommand("hdparm -i /dev/" + devsName, hostConfigData);
                    for (String info : diskInfo) {
                        System.out.println(info);
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
                    JSONObject newDiskInfo=configDataManager.getSampleFormat("diskInfo");
                    {
                        newDiskInfo.put("diskName",Serial.trim());
                        newDiskInfo.put("diskModel",Model.trim());
                        newDiskInfo.put("diskCapacitySize",LinuxDataProcess.doubleTo2bits_double(size*1.0f/1024/1024/1024));
                        newDiskInfo.put("type",1);
                        //TODO 加入smart信息对照

                    }
                    sampleData.getJSONArray("diskInfoList").add(newDiskInfo);
                    totalsize+=size;
                }
                sampleData.put("diskCapacityTotalSizeSum",LinuxDataProcess.doubleTo2bits_double(totalsize*1.0/1024/1024/1024));
            }
            //cpuInfoList
            {
                List<String> cmdResult = cmdExecutor.runCommand("cat /proc/cpuinfo |grep cpu",hostConfigData);

                for(String rowData:cmdResult){
                        JSONObject newCpuInfo = configDataManager.getSampleFormat("cpuInfo");
                        {
                            newCpuInfo.put("cpuName",rowData.split(":")[1]);
                        }
                        sampleData.getJSONArray("cpuInfoList").add(newCpuInfo);
                }
            }
            //gpuInfo
            {
                List<LinuxGPU> cardList = new ArrayList();
                List<String> lspci = cmdExecutor.runCommand("lspci -vnnm",hostConfigData);
                String name = "unknown";
                String deviceId = "unknown";
                String vendor = "unknown";
                List<String> versionInfoList = new ArrayList();
                boolean found = false;
                String lookupDevice = null;
                Iterator var8 = lspci.iterator();

                while(var8.hasNext()) {
                    String line = (String)var8.next();
                    String[] split = line.trim().split(":", 2);
                    String prefix = split[0];
                    if (prefix.equals("Class") && line.contains("VGA")) {
                        found = true;
                    } else if (prefix.equals("Device") && !found && split.length > 1) {
                        lookupDevice = split[1].trim();
                    }

                    if (found) {
                        if (split.length < 2) {
                            cardList.add(new LinuxGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), LinuxDataProcess.queryLspciMemorySize(lookupDevice)));
                            versionInfoList.clear();
                            found = false;
                        } else {
                            Pair pair;
                            if (prefix.equals("Device")) {
                                pair = LinuxDataProcess.parseLspciMachineReadable(split[1].trim());
                                if (pair != null) {
                                    name = (String)pair.getA();
                                    deviceId = "0x" + (String)pair.getB();
                                }
                            } else if (prefix.equals("Vendor")) {
                                pair = LinuxDataProcess.parseLspciMachineReadable(split[1].trim());
                                if (pair != null) {
                                    vendor = (String)pair.getA() + " (0x" + (String)pair.getB() + ")";
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
                    cardList.add(new LinuxGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), LinuxDataProcess.queryLspciMemorySize(lookupDevice)));
                }
                for(LinuxGPU linuxGPU:cardList){
                        JSONObject newGpuInfo = configDataManager.getSampleFormat("gpuInfo");
                        {
                            newGpuInfo.put("gpuName",linuxGPU.getName());
                            newGpuInfo.put("gpuAvailableRam",LinuxDataProcess.doubleTo2bits_double(linuxGPU.getVram()*1.0f/1024/1024/1024));
                        }
                        sampleData.getJSONArray("gpuInfoList").add(newGpuInfo);
                }
            }
        return sampleData;
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
    private long lastCPUUsed=0;
    private long lastCPUTotal=0;
    public void sampleHostData(HostConfigData hostConfigData,JSONObject sampleData){
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
            LinuxPeriodRecord record=new LinuxPeriodRecord();
            //TODO
            String scriptPath=System.getProperty("user.dir")+"/ConfigData/Client/SampleCommand.sh";
            List<String> sampleInfo=cmdExecutor.runCommand(scriptPath,hostConfigData);
            List<String> mountUsageInfo = cmdExecutor.runCommand("df",hostConfigData); //查询结果使用量为KB
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
            List<String> CPUInfo = cmdExecutor.runCommand("cat /proc/stat | grep cpu",hostConfigData);
            {
                for (String string : CPUInfo) {
                    String[] tokens = string.split("\\s+");
                    if (tokens[0].equals("cpu")) {
                        long total = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) + Long.parseLong(tokens[4])
                                + Long.parseLong(tokens[5]) + Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                        long used = Long.parseLong(tokens[1]) + Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) +
                                Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]);
                        record.setCPUused(used);
                        record.setCPUtotal(total);
                    }
                }

                Iterator<String> itr = sampleInfo.iterator();
                while (itr.hasNext()) {
                    String currentString = itr.next();
                    if (currentString.contains(scriptPath)) {
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
                        List<String> devMountInfo = cmdExecutor.runCommand("lsblk /dev/" + diskName,hostConfigData);
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
            //CPU
            {
                if(sampleData.containsKey("lastTotalTicks"))
                {
                    long oldTotalTicks = sampleData.getLong("lastTotalTicks");
                    long oldUsedTicks = sampleData.getLong("lastUsedTicks");
                    long newTotalTicks=record.getCPUtotal();
                    long newUsedTicks=record.getCPUused();
                    double cpuUsage = (newUsedTicks - oldUsedTicks)* 1.0f / (newTotalTicks - oldTotalTicks) ;
                    double cpuUsage2bits = LinuxDataProcess.doubleTo2bits_double(cpuUsage * 100);
                    sampleData.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", cpuUsage2bits);
                    sampleData.put("cpuUsage", cpuUsage2bits);
                    sampleData.put("lastTotalTicks",newTotalTicks);
                    sampleData.put("lastUsedTicks",newUsedTicks);
                }
                else {
                    sampleData.getJSONArray("cpuInfoList").getJSONObject(0).put("cpuUsage", LinuxDataProcess.doubleTo2bits_double(0.0 * 100));
                    sampleData.put("cpuUsage",  LinuxDataProcess.doubleTo2bits_double(0.0 * 100));
                    sampleData.put("lastTotalTicks",record.getCPUtotal());
                    sampleData.put("lastUsedTicks",record.getCPUused());
                }
            }
            //NetIO
            {
                sampleData.put("netSendSpeed",LinuxDataProcess.doubleTo2bits_double(record.getNetSend()*1.0f/1024/1024));
                sampleData.put("netReceiveSpeed",LinuxDataProcess.doubleTo2bits_double(record.getNetReceive()*1.0f/1024/1024));
            }
            //Disk
            {
                List<DiskInfo> DiskStoreList=record.getDisks();
                long totalUsedSize=0;
                for(int j=0;j<DiskStoreList.size();j++){
                    DiskInfo tempDiskInfo=DiskStoreList.get(j);
                    int i=findDiskIndex(tempDiskInfo.diskName,sampleData);
                    double usage2bits=0.0;
                    try {
                        usage2bits = LinuxDataProcess.doubleTo2bits_double(tempDiskInfo.diskUsedRadio);
                    } catch (Exception e) {
                        System.out.println("usage2bitsError");
                    }
                    JSONArray singleArray=new JSONArray();
                    double singleTotalSize=sampleData.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskCapacityTotalSize");
                    double singleUsedSize=tempDiskInfo.diskFSUsageAmount*1.0f/1024/1024;
                    singleArray.add(LinuxDataProcess.doubleTo2bits_double(singleUsedSize));
                    singleArray.add(singleTotalSize);
                    totalUsedSize+=singleUsedSize;
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskUsage",usage2bits);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(i).put("diskCapacitySize",singleArray);
                    double ReadRates = tempDiskInfo.diskReadSpeed;
                    double WriteRates = tempDiskInfo.diskWriteSpeed;
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskIOPS", tempDiskInfo.diskIOPS);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadSpeed", LinuxDataProcess.doubleTo2bits_double(ReadRates));
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteSpeed", LinuxDataProcess.doubleTo2bits_double(WriteRates));
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskRead",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskReadBytes",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskWrite",0);
                    sampleData.getJSONArray("diskInfoList").getJSONObject(j).put("diskWriteBytes",0);
                }
                JSONArray diskUsage=new JSONArray();
                diskUsage.add(LinuxDataProcess.doubleTo2bits_double(totalUsedSize));
                diskUsage.add(sampleData.getDouble("diskCapacityTotalSizeSum"));
                sampleData.put("diskCapacityTotalUsage",diskUsage);
            }
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
    private int findDiskIndex(String diskName,JSONObject dataObject){
        for(int i=0;i<dataObject.getJSONArray("diskInfoList").size();i++){
            if( dataObject.getJSONArray("diskInfoList").getJSONObject(i).getString("diskName").contains(diskName)){
                return i;
            }
        }
        return 0;
    }

    public void sampleHostProcess(HostConfigData hostConfigData,JSONObject sampleData){
        OSType osType = getOSType(hostConfigData);
        if(osType.equals(OSType.LINUX)){
            JSONArray processInfoList=new JSONArray();
            List<String> ProcessInfo = cmdExecutor.runCommand("top -b -n 1 ",hostConfigData);
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
                double memoryUsage=Double.parseDouble(tokens[9]);
                //时间算法有问题，以后再debug
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
                //TODO 进程过滤
                processInfoList.add(newProcess);
            }
            System.out.println("进程个数"+processInfoList.size());
            sampleData.put("processInfoList",processInfoList);

        }
        else if(osType.equals(OSType.WINDOWS)){

            List<String> cmdResult = cmdExecutor.runCommand("powershell -command \"Get-WmiObject -query { SELECT CreatingProcessID,Name,ElapsedTime,workingset,percentProcessorTime,ioReadBytesPersec,ioWriteBytesPersec  FROM Win32_PerfFormattedData_PerfProc_Process  }\""
                    ,hostConfigData);


        }
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
                    List<String> cmdResult = cmdExecutor.runCommand(getDiskListCmd,hostConfigData);
                    cmdResult.remove(0);
                    for(String currentStr:cmdResult){
                        if(!currentStr.equals("")){
                            diskList.add(currentStr.trim());
                        }
                    }
                }
                else if(osType.equals(OSType.LINUX)){
                    getDiskListCmd = "lsblk -bnd";
                    List<String> cmdResult = cmdExecutor.runCommand(getDiskListCmd,hostConfigData);
                    for(String currentStr:cmdResult) {
                        String[] rawData = currentStr.split("\\s+");
                        System.out.println(rawData.length);
                        diskList.add("/dev/"+rawData[0]);
                        System.out.println("/dev/"+rawData[0]);

                    }
                }
            }


            //以Json格式存数据
            String smartDiskInfoCmd = "smartctl -i ";
            String smartDataSampleCmd = "smartctl -A ";
            for(String currentDiskName: diskList){
                JSONObject currentDiskData = new JSONObject();
                {
                    List<String> cmdResult = cmdExecutor.runCommand(smartDiskInfoCmd + currentDiskName,hostConfigData);
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
                        List<String> cmdResult = cmdExecutor.runCommand(smartDataSampleCmd + currentDiskName,hostConfigData);
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
        System.out.println(diskData.toString());

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

        //TODO 写文件路径修改，直接写到服务器端的收集文件里，避免了文件传送
        String sampleDataFilePath=System.getProperty("user.dir")+"/DiskPredict/client/data.csv";
        CsvWriter csvWriter = new CsvWriter(sampleDataFilePath,',', Charset.forName("GBK"));
        try {
            //Smart属性个数
            int smartCount = 256;
            //获取表头
            List<String> headers = new ArrayList<>();
            {
                String[] staticHeaders = {"date", "serial_number", "model", "serialAlternative", "failure", "is_ssd", "pt_d"};
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

            //写入数据
            Set<String> serialNumberList = diskData.keySet();
            for(String serialNumber:serialNumberList){
                JSONObject currentDiskData = diskData.getJSONObject(serialNumber);
                List<String> rowData = new ArrayList<>();
                {
                    rowData.add(currentDate);
                    rowData.add(serialNumber);
                    rowData.add(currentDiskData.getString("Device Model"));
                    rowData.add("");
                    rowData.add("0");
                    rowData.add("0");
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }

    }

    public JSONObject ioTest(HostConfigData hostConfigData){
        OSType osType = getOSType(hostConfigData);
        String speedTestCmd="winsat disk";
        String readSpeed=null;
        String writeSpeed=null;
        if(osType.equals(OSType.WINDOWS)){
            speedTestCmd = "winsat disk";
        }
        else if(osType.equals(OSType.LINUX)){
            //TODO
            String cmdFilePath=System.getProperty("user.dir")+"/SpeedTest.sh";
            speedTestCmd = cmdFilePath;
        }
        List<String> cmdResult = cmdExecutor.runCommand(speedTestCmd,hostConfigData);
        if(cmdResult!=null){
            if(osType.equals(OSType.WINDOWS)){
                for(String currentOutput: cmdResult){
                    String[] rawData = currentOutput.split("\\s+");
                    if(currentOutput.contains("Disk  Sequential 64.0 Read")){
                        readSpeed = rawData[5] +" "+ rawData[6];
                    }
                    else if(currentOutput.contains("Disk  Sequential 64.0 Write")){
                        writeSpeed = rawData[5] +" "+ rawData[6];
                    }
                }
            }
            else{
                writeSpeed = cmdResult.get(0);
                readSpeed = cmdResult.get(1);
            }
            JSONObject result=new JSONObject();
            result.put("writeSpeed",writeSpeed);
            result.put("readSpeed",readSpeed);
        }
        return null;
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
