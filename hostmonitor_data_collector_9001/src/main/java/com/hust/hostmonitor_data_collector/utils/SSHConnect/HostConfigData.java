package com.hust.hostmonitor_data_collector.utils.SSHConnect;


import com.hust.hostmonitor_data_collector.utils.OSType;

public class HostConfigData {
    //IP
    public String ip;
    //用户名
    public String userName;
    //密码
    public String password;
    //代理
    public ProxyConfigData proxyConfigData;
    //操作系统类型
    public OSType osType;
    public String router;

    public HostConfigData(){
        ip = "";
        userName = "";
        password = "";
        proxyConfigData = null;
        osType = OSType.NONE;
        router = "";
    }

    public HostConfigData(String ip, String username, String password, ProxyConfigData proxyConfigData,OSType osType,String router) {
        this.ip = ip;
        this.userName = username;
        this.password = password;
        this.proxyConfigData = proxyConfigData;
        this.osType = osType;
        this.router = router;
    }

    public void setProxy(ProxyConfigData proxyConfig){
        proxyConfigData = proxyConfig;
    }

    public boolean hasProxy(){
        return proxyConfigData != null;
    }
    @Override
    public String toString(){
        return "["+ ip +"," + userName+","+ password+","+ proxyConfigData +","+osType+","+router+"]";
    }
}
