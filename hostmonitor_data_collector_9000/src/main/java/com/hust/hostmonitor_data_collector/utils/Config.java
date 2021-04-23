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
    private String sampleCommands;

    //Init
    public Config() {
        try {
            //读配置文件
            File configFile=new File(path,"ConfigData/StorageDeviceInfo.json");
            String configFileContent = FileUtils.readFileToString(configFile, "UTF-8");
            //解析Json
            jsonObject = JSONObject.parseObject(configFileContent);

            //采样指令
            File sampleCommandFile = new File(path,"ConfigData/SampleCommand.sh"); //test
            sampleCommands = FileUtils.readFileToString(sampleCommandFile, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

}
