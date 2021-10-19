package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.ProxyConfigData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

//配置数据管理类
public class ConfigDataManager {
    //配置文件父路径(最终为打包jar的同级目录)
    private final String rootPath = System.getProperty("user.dir");
    //配置文件JSON
    private final JSONObject configJson = JSONObject.parseObject(readFile("ConfigData/Server/Config.json"));
    //系统设置
    private final String systemSettingFilePath = "ConfigData/Server/SystemSetting.json";
    private JSONObject systemSetting = JSONObject.parseObject(readFile(systemSettingFilePath));

    //单例
    private volatile static ConfigDataManager configDataManager;
    public static ConfigDataManager getInstance(){
        if(configDataManager ==null){
            synchronized (ConfigDataManager.class){
                if(configDataManager ==null){
                    configDataManager =new ConfigDataManager();
                }
            }
        }
        return configDataManager;
    }
    private ConfigDataManager(){}

    //----- 读写文件
    public String readFile(String filePath){
        String resultData = "";
        File file = new File(rootPath,filePath);
        try {
            resultData = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    public void writeFile(String filePath,String contentData){
        File file = new File(filePath);
        try {
            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(contentData);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public JSONArray readCSV(String filePath){
        JSONArray jsonArray = new JSONArray();
        try {
            CsvReader csvReader = new CsvReader(filePath,',', Charset.forName("gb2312"));
            csvReader.readHeaders();
            String[] headers= csvReader.getHeaders();
            while(csvReader.readRecord()) {
                String[] rowData = csvReader.getValues();
                if(rowData.length >0){
                    JSONObject rowJSON = new JSONObject();
                    for(int i=0;i<headers.length;i++){
                        rowJSON.put(headers[i],rowData[i]);
                    }
                    jsonArray.add(rowJSON);
                }
            }
            csvReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
    public void writeCSV(String filePath,List<String> headers,JSONArray dataContent){
        CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.forName("GBK"));
        try {
            //Smart属性个数
            int smartCount = 256;

            //写入header头
            csvWriter.writeRecord(headers.toArray(new String[0]));
            //写入数据
            for(int i=0;i<dataContent.size();i++){
                JSONObject currentData = dataContent.getJSONObject(i);
                List<String> rowData = new ArrayList<>();
                for(int j=0;j<headers.size();i++){
                    rowData.add(currentData.getString(headers.get(j)));
                }
                csvWriter.writeRecord(rowData.toArray(new String[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }
    }

    //获取配置文件JSON
    public final JSONObject getConfigJson() {
        return configJson;
    }

    //获取系统设置数据
    public JSONObject getSystemSetting() {
        return JSONObject.parseObject(systemSetting.toJSONString());
    }

    //更新系统设置
    public void updateSystemSetting(JSONObject currentSystemSetting){
        systemSetting = JSONObject.parseObject(currentSystemSetting.toJSONString());
        writeFile(systemSettingFilePath,systemSetting.toString());
    }

    //获取监控的节点列表
    public List<HostConfigData> getSSHConfigHostList(){
        //代理
        Map<Integer, ProxyConfigData> proxyMap = new HashMap<>();
        {
            JSONArray proxyList = readCSV(rootPath+ "/ConfigData/Server/Proxy.csv");
            for(int i=0;i<proxyList.size();i++){
                JSONObject currentProxy = proxyList.getJSONObject(i);
                int proxyId = currentProxy.getInteger("proxyId");
                String proxyIp = currentProxy.getString("proxyIp");
                int proxyPort = currentProxy.getInteger("proxyPort");
                proxyMap.put(proxyId,new ProxyConfigData(proxyId,proxyIp,proxyPort));
            }
        }

        //Host节点
        List<HostConfigData> hostConfigDataList = new ArrayList<>();
        {
            JSONArray hostList = readCSV(rootPath+ "/ConfigData/Server/HostList.csv");
            for(int i=0;i<hostList.size();i++){
                JSONObject currentHost = hostList.getJSONObject(i);
                String ip = currentHost.getString("ip");
                String username = currentHost.getString("userName");
                String password = currentHost.getString("password");
                int proxyId = currentHost.getInteger("proxyId");
                ProxyConfigData proxyConfigData = null;
                //@Todo
                OSType osType = OSType.NONE;
                {
                    String osName = currentHost.getString("OSType");
                    if(!osName.equals("")){
                        try {
                            osType = OSType.valueOf(osName.toUpperCase());
                        }
                        catch (IllegalArgumentException exception){
                            System.out.println("Illegal OSType");
                        }
                    }
                };
                if(proxyMap.containsKey(proxyId)){
                    proxyConfigData = proxyMap.get(proxyId);
                }
                hostConfigDataList.add(new HostConfigData(ip,username,password,proxyConfigData,osType));
            }
        }
        return hostConfigDataList;
    }

    //获取采样格式
    public JSONObject getSampleFormat(String key){
        return JSONObject.parseObject(configJson.getJSONObject("SampleFormat").getJSONObject(key).toJSONString());
    }

    public static void main(String[] args) {
        ConfigDataManager configDataManager = ConfigDataManager.getInstance();
        List<HostConfigData> hostConfigDataList = configDataManager.getSSHConfigHostList();
        for (HostConfigData hostConfigData:hostConfigDataList){
            System.out.println(hostConfigData.toString());
        }
    }
}
