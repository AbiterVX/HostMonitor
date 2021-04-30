package com.hust.hostmonitor_data_collector.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主机监控 -main class
 */
@Deprecated
public class HostMonitor implements Runnable {
    //---------成员
    SSHManager sshManager;

    //Config配置信息
    private Config configInfo;
    //Host配置信息
    List<HostConfigInfo> hostConfigInfoList;
    //Host系统信息
    List<HostInfo> hostInfoList;
    //采样间隔
    public int interval_ms;

    public Thread getNewThread() {
        return newThread;
    }

    //线程
    private Thread newThread;


    //线程开始
    public boolean threadStart;
    private boolean isDataHasBeenWritten=true;
    private volatile static HostMonitor hostMonitor;
    public static HostMonitor getInstance(){
        if(hostMonitor==null){
            synchronized (HostMonitor.class){
                if(hostMonitor==null){
                    hostMonitor=new HostMonitor();
                }
            }
        }
        return hostMonitor;
    }


    //---------Init
    private HostMonitor(){
        //延迟时间默认为10 * 1000，单位：ms
        this(10 * 1000);
    }

    public boolean isDataHasBeenWritten() {
        return isDataHasBeenWritten;
    }

    public void setDataHasBeenWritten(boolean dataHasBeenWritten) {
        isDataHasBeenWritten = dataHasBeenWritten;
    }


    public HostMonitor(int _interval_ms){
        //配置信息
        configInfo = Config.getInstance();
        //主机配置信息
        hostConfigInfoList = configInfo.getHostConfigInfoList();
        //主机返回信息
        hostInfoList =new ArrayList<>();
        for(int i=0;i<hostConfigInfoList.size();i++){
            HostInfo newHostInfo = new HostInfo(hostConfigInfoList.get(i).ip);
            hostInfoList.add(newHostInfo);
        }
        //延迟时间 单位：ms
        interval_ms = _interval_ms;
        //采样线程开关
        threadStart = false;
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();
    }

    //---------SSH远程连接&指令调用
    //执行指令
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo){
        return sshManager.runCommand(command,hostConfigInfo);
    }
    //--------- 设置连接状态（如连接中断则保证其他主机也能正常连接）
    public void setSessionConnected(int index,boolean _sessionConnected){
        HostInfo currentHostInfo = hostInfoList.get(index);
        currentHostInfo.sessionConnected = _sessionConnected;
    }

    //--------- 采样
    //全部采样
    @Deprecated
    public void sampleAll(){
        sampleNetBindWidth();
        sampleCpuUsage();
        sampleMemory();
        sampleDisk();
    }
    //[网络带宽]采样
    @Deprecated
    public void sampleNetBindWidth(){
        String command = "cat /proc/net/dev";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            for(int j=0;j<commandResult.size();j++){
                String currentLine = commandResult.get(j);
                if(currentLine.contains("eth0")){
                    String[] datas = currentLine.split("\\s+");
                    //数据设置
                    HostInfo currentHostInfo = hostInfoList.get(i);
                    currentHostInfo.receiveBytes[0] = currentHostInfo.receiveBytes[1];
                    currentHostInfo.transmitBytes[0] = currentHostInfo.transmitBytes[1];
                    currentHostInfo.receiveBytes[1] = Integer.parseInt(datas[2]);
                    currentHostInfo.transmitBytes[1] = Integer.parseInt(datas[10]);
                    //System.out.println("In function sampleNetBinWidth of HostMonitor"+currentHostInfo.receiveBytes);
                    break;
                }
            }
        }


    }
    //[CPU利用率]采样
    @Deprecated
    public void sampleCpuUsage(){
        String command = "cat /proc/stat";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                String currentLine = commandResult.get(0);
                if(currentLine.contains("cpu")){
                    String[] datas = currentLine.split("\\s+");
                    int totalTime = 0;
                    for(int j=1;j<=7;j++){
                        totalTime += Integer.parseInt(datas[j]);
                    }

                    HostInfo currentHostInfo = hostInfoList.get(i);
                    currentHostInfo.cpuTotalTime[0] = currentHostInfo.cpuTotalTime[1];
                    currentHostInfo.cpuTotalTime[1] = totalTime;
                    currentHostInfo.cpuIdleTime[0] = currentHostInfo.cpuIdleTime[1];
                    currentHostInfo.cpuIdleTime[1] = Integer.parseInt(datas[4]);
                }
            }
        }
    }
    //[内存]采样
    @Deprecated
    public void sampleMemory(){
        String command = "head -n 5 /proc/meminfo";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.memTotal = Integer.parseInt(commandResult.get(0).split("\\s+")[1]);
                currentHostInfo.memAvaliable = Integer.parseInt(commandResult.get(2).split("\\s+")[1]);
            }
        }
    }
    //[磁盘]采样
    @Deprecated
    public void sampleDisk(){
        String command = "cat /proc/diskstats";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            for(String resultLine:commandResult){
                if(resultLine.contains("vda")){
                    HostInfo currentHostInfo = hostInfoList.get(i);

                    currentHostInfo.ioTimeSpent[0] = currentHostInfo.ioTimeSpent[1];
                    String[] datas = resultLine.split("\\s+");

                    currentHostInfo.ioTimeSpent[1]= Integer.parseInt(datas[13]);

                    break;
                }
            }
        }

    }

    //[]采样
    @Deprecated
    public void sampleMulti(){

    }
    //---------获得Host设备信息
    @Deprecated
    public String getHostInfoListOutputData(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            Map<String, Object> result = hostInfo.getOutputData(interval_ms);
            JSONObject jsonObject = new JSONObject(result);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    public String getHostIp(int index){
        return hostConfigInfoList.get(index).ip;
    }

    @Deprecated
    public Map<String, Object> getHostHardWareInfo(int index){
        Map<String, Object> result = new HashMap<>();
        HostInfo hostInfo = hostInfoList.get(index);
        result.put("CPU",hostInfo.cpuType);
        result.put("Memory",hostInfo.memoryType);
        result.put("Disk",hostInfo.diskType);
        result.put("OS",hostInfo.osType);
        result.put("MemorySize",hostInfo.memorySize);
        return result;
    }

    @Deprecated
    public String getHostHardWareInfoListOutputData(){
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<hostInfoList.size();i++){
            Map<String, Object> result = getHostHardWareInfo(i);
            JSONObject jsonObject = new JSONObject(result);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }


    //---------获得Host硬件设备信息
    public void sampleHostHardWareInfo(){
        getOSType();
        getCpuType();
        //getMemoryType();
        getDiskType();
        getMemorySize();
    }
    //获取操作系统类型
    @Deprecated
    public void getOSType() {
        String command = "head -n 1 /etc/issue";
        for (int i = 0; i < hostConfigInfoList.size(); i++) {
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.osType = commandResult.get(0);
            }
        }
    }
    //获取CPU类型
    @Deprecated
    public void getCpuType() {
        String command = "cat /proc/cpuinfo | grep \"model name\"";
        for (int i = 0; i < hostConfigInfoList.size(); i++) {
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.cpuType = commandResult.get(0).split(":")[1];
            }
        }
    }
    //获取内存类型
    @Deprecated
    public void getMemoryType() {
        String command = "grep MemTotal /proc/meminfo";
        for (int i = 0; i < hostConfigInfoList.size(); i++) {
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.cpuType = commandResult.get(0).split(":")[1];
            }
        }
    }
    //获取内存大小
    @Deprecated
    public void getMemorySize() {
        String command = "grep MemTotal /proc/meminfo";
        for (int i = 0; i < hostConfigInfoList.size(); i++) {
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.memorySize = commandResult.get(0).split(":")[1];
            }
        }
    }
    //获取磁盘类型
    @Deprecated
    public void getDiskType() {
        String command = "fdisk -l | grep \"Disk /dev\"   ";
        for (int i = 0; i < hostConfigInfoList.size(); i++) {
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                String diskInfoTxt = "";
                for(int j=0;j<commandResult.size();j++){
                    diskInfoTxt += commandResult.get(0) + "\n";
                }
                currentHostInfo.diskType = diskInfoTxt;
            }
        }
    }

    //获取Host的IP
    public String getHostIpList(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            jsonArray.add(hostInfo.ip);
        }
        return jsonArray.toJSONString();
    }
    public List<Map<String,Object>> getOriginalHostInfoListOutputData(){
        ArrayList<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
        for(int i=0;i<hostInfoList.size();i++){
            Map<String, Object> result = hostInfoList.get(i).getOutputData(interval_ms);
            mapList.add(i,result);
        }
        return mapList;
    }
    //获取当前Host状态
    public String getHostState(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            jsonArray.add(hostInfo.sessionConnected ? 1:0);
        }
        return jsonArray.toJSONString();
    }
    public List<Boolean> getHostStateList(){
        ArrayList<Boolean> arrayList=new ArrayList<>();
        for(HostInfo hostInfo:hostInfoList){
            arrayList.add(hostInfo.sessionConnected);
        }
        return arrayList;
    }
    //---------多线程执行
    //多线程运行
    @Override
    public void run() {
        //硬件设备信息
        sampleHostHardWareInfo();

        while(threadStart){
            //System.out.println("Check:"+isDataHasBeenWritten);
            //while(!isDataHasBeenWritten);
            //采样
            sampleAll();
            //获取
            //等待
            //setDataHasBeenWritten(false);
            try {
                Thread.sleep(interval_ms);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public void checkState(){

    }
    //开始线程
    public void start(){
        if(newThread == null){
            for(HostInfo hostInfo:hostInfoList){
                hostInfo.initValue();
            }
            threadStart = true;
            newThread = new Thread(this,"HostMonitor—thread");
            newThread.start();
        }
    }
    //终止线程
    public void stop(){
        threadStart = false;
        try {
            Thread.sleep(interval_ms*2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(newThread != null){
            newThread.interrupt();
            newThread = null;
        }
    }


    //main-用于测试
    public static void main(String[] args) {
        HostMonitor hostMonitor = new HostMonitor();
        hostMonitor.start();
        //System.out.println(hostMonitor.getHostInfoListOutputData());

//        try {
//            Thread.sleep(30000);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        hostMonitor.stop();
    }

}
