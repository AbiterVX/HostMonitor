package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final String StorageDeviceHostTxt = "StorageDeviceHost";
    private final String ipTxt = "ip";
    private final String usernameTxt = "username";
    private final String passwordTxt = "password";
    private final String proxyIpTxt = "proxyIp";
    private final String proxyPortTxt = "proxyPort";
    private final String sampleDataKeyTxt = "value";
    private final String sampleDataUnitTxt = "unit";
    private final String sampleDataFormat = "SampleDataFormat";
    private final String diskSampleDataFormat = "DiskSampleDataFormat";
    private final String temperatureSampleDataFormat = "TemperatureSampleDataFormat";



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
        List<HostConfigInfo> hostConfigInfoList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(StorageDeviceHostTxt);
        for (Object object : jsonArray) {
            //读取字段
            JSONObject jsonObject = (JSONObject) object;
            String ip = jsonObject.getString(ipTxt);
            String username = jsonObject.getString(usernameTxt);
            String password = jsonObject.getString(passwordTxt);
            HostConfigInfo newHostConfigInfo = new HostConfigInfo(ip,username,password);

            //代理信息
            if(jsonObject.containsKey(proxyIpTxt) && jsonObject.containsKey(proxyPortTxt)){
                String proxyIp = jsonObject.getString(proxyIpTxt);
                int proxyPort = jsonObject.getInteger(proxyPortTxt);
                newHostConfigInfo.setProxy(proxyIp,proxyPort);
            }

            //添加到List
            hostConfigInfoList.add(newHostConfigInfo);
        }
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

    List<HostConfigInfo> getHostConfigInfoData(){
        List<HostConfigInfo> hostConfigInfoList = new ArrayList<>();



        return hostConfigInfoList;
    }


    public JSONObject getHostList(){
        // 设定Excel文件所在路径
        String excelFileName = "/Users/Dreamer-1/Desktop/myBlog/java解析Excel/readExample.xlsx";
        // 读取Excel文件内容
        //List<HostConfigInfo> readResult = ExcelReader.readExcel(excelFileName);


        JSONObject temp = new JSONObject();


        return jsonObject;
    }

    public static void main(String[] args) {
        Config config = Config.getInstance();
        config.getHostList();
    }
}
