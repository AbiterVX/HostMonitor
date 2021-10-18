package com.hust.hostmonitor_data_collector.utils.SSHConnect;


public class HostConfigData {
    //IP
    public String ip;
    //用户名
    public String userName;
    //密码
    public String password;
    //代理
    public ProxyConfigData proxyConfigData;

    public HostConfigData(){
        ip = "";
        userName = "";
        password = "";
        proxyConfigData = null;
    }

    public HostConfigData(String ip, String username, String password, ProxyConfigData proxyConfigData) {
        this.ip = ip;
        this.userName = username;
        this.password = password;
        this.proxyConfigData = proxyConfigData;
    }

    public void setProxy(ProxyConfigData proxyConfig){
        proxyConfigData = proxyConfig;
    }

    public boolean hasProxy(){
        return proxyConfigData != null;
    }
    @Override
    public String toString(){
        return "["+ ip +"," + userName+","+ password+","+ proxyConfigData +"]";
    }
}
