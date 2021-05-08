package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.*;

/**
 * 读取json配置文件并解析
 */
public class Config {
    //Json配置文件
    private JSONObject jsonObject;
    private final String path = System.getProperty("user.dir");
    private final String sampleCommands;
    private final String processSampleCommand;
    private final String initEnvironmentCommand;
    private final String ioTestCommand;
    private final String testCommand;
    List<ProxyConfig> proxyConfigList = new ArrayList<>();
    List<HostConfigInfo> hostConfigInfoList = new ArrayList<>();


    private volatile static Config config;
    public static Config getInstance(){
        if(config==null){
            synchronized (Config.class){
                if(config==null){
                    config=new Config();
                }
            }
        }
        return config;
    }

    //Init
    private Config() {
        //解析Json
        jsonObject = JSONObject.parseObject(readFile("ConfigData/StorageDeviceInfo.json"));
        //采样指令
        sampleCommands = readFile("ConfigData/SampleCommand.sh");  //test  //SampleCommand
        //进程采样指令
        processSampleCommand = readFile("ConfigData/ProcessSampleCommand.sh");
        //环境初始化指令
        initEnvironmentCommand = readFile("ConfigData/InitEnvironment.sh");
        //测试指令
        testCommand = readFile("ConfigData/test.sh");
        //IO测试指令
        ioTestCommand = readFile("ConfigData/IOTest.sh");

        //Proxy excel
        EasyExcel.read(path+"/ConfigData/Proxy.xlsx",ProxyConfig.class,new ProxyConfigListener()).sheet().doRead();

        /*
        for(ProxyConfig proxyConfig:proxyConfigList){
            System.out.println(proxyConfig);
        }*/
        //List<ProxyConfig> tempList = proxyConfigList;
        //tempList.add(new ProxyConfig());
        //EasyExcel.write(path+"/ConfigData/Proxy.xlsx",ProxyConfig.class).sheet().doWrite(tempList);

        //host excel
        EasyExcel.read(path+"/ConfigData/Host.xlsx",HostConfigInfo.class,new HostConfigInfoListener()).sheet().doRead();

        //写入excel
        //List<HostConfigInfo> tempList = hostConfigInfoList;
        //tempList.add(new HostConfigInfo());
        //EasyExcel.write(path+"/ConfigData/Host.xlsx",HostConfigInfo.class).sheet().doWrite(tempList);
    }

    public String readFile(String filePath){
        String resultData = "";
        File file = new File(path,filePath);
        try {
            resultData = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    //----------静态字段
    private final String sampleDataFormat = "SampleDataFormat";
    private final String diskSampleDataFormat = "DiskSampleDataFormat";
    private final String temperatureSampleDataFormat = "TemperatureSampleDataFormat";

    //----------解析excel文件
    //解析Host-excel文件
    public class HostConfigInfoListener extends AnalysisEventListener<HostConfigInfo>{
        //每条数据解析都会调用
        @Override
        public void invoke(HostConfigInfo hostConfigInfo, AnalysisContext analysisContext) {
            if(hostConfigInfo.proxyId != 0){
                for(ProxyConfig proxyConfig:proxyConfigList){
                    if(hostConfigInfo.proxyId == proxyConfig.proxyId){
                        hostConfigInfo.setProxy(proxyConfig);
                        break;
                    }
                }
            }
            hostConfigInfoList.add(hostConfigInfo);
        }
        //全部解析完后调用
        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            System.out.println("Host-excel解析完成");
        }
    }
    //解析Proxy-excel文件
    public class ProxyConfigListener extends AnalysisEventListener<ProxyConfig>{
        //每条数据解析都会调用
        @Override
        public void invoke(ProxyConfig proxyConfig, AnalysisContext analysisContext) {
            proxyConfigList.add(proxyConfig);
        }
        //全部解析完后调用
        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            System.out.println("Proxy-excel解析完成");
        }
    }


    //----------外部接口
    //获取采样指令
    public String getSampleCommands() {
        return sampleCommands;
    }
    //获取进程采样指令
    public String getProcessSampleCommand(){
        return processSampleCommand;
    }
    //获取环境初始化指令
    public String getInitEnvironmentCommand() {
        return initEnvironmentCommand;
    }
    //获取测试指令
    public String getTestCommand() {
        return testCommand;
    }
    //获取Host配置信息
    public List<HostConfigInfo> getHostConfigInfoList() {
        return hostConfigInfoList;
    }
    //采样数据格式
    public JSONObject getSampleDataFormat() {
        JSONObject temp = new JSONObject();
        temp.putAll(jsonObject.getJSONObject(sampleDataFormat));
        return temp;
    }
    //磁盘采样数据格式
    public JSONObject getDiskSampleDataFormat(){
        JSONObject temp = new JSONObject();
        temp.putAll(jsonObject.getJSONObject(diskSampleDataFormat));
        return temp;
    }
    //温度采样数据格式
    public JSONObject getTemperatureSampleDataFormat(){
        JSONObject temp = new JSONObject();
        temp.putAll(jsonObject.getJSONObject(temperatureSampleDataFormat));
        return temp;
    }



    public static void main(String[] args) {
        Config config = Config.getInstance();
        List<HostConfigInfo> hostList= config.getHostConfigInfoList();
        for(HostConfigInfo hostConfigInfo:hostList){
            System.out.println(hostConfigInfo);
        }
    }
}
