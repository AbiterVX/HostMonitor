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
    private JSONObject jsonObject;
    public Config() {
        try {
            //读文件
            String path = System.getProperty("user.dir");
            File file=new File(path,"ConfigData/StorageDeviceInfo.json");
            String content = FileUtils.readFileToString(file, "UTF-8");
            //解析Json
            jsonObject = JSONObject.parseObject(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取配置文件中的主机信息
    public List<HostConfigInfo> getStorageDeviceHost(){
        List<HostConfigInfo> hostConfigInfoList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("StorageDeviceHost");
        for (Object object : jsonArray) {
            //读取字段
            JSONObject jsonObject = (JSONObject) object;
            String ip = jsonObject.getString("ip");
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            HostConfigInfo newHostConfigInfo = new HostConfigInfo(ip,username,password);

            //代理信息
            if(jsonObject.containsKey("proxyIp") && jsonObject.containsKey("proxyPort")){
                String proxyIp = jsonObject.getString("proxyIp");
                int proxyPort = jsonObject.getInteger("proxyPort");
                newHostConfigInfo.setProxy(proxyIp,proxyPort);
            }

            //添加到List
            hostConfigInfoList.add(newHostConfigInfo);
        }
        return hostConfigInfoList;
    }
}
