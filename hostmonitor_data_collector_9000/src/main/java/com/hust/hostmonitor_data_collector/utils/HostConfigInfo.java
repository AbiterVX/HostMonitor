package com.hust.hostmonitor_data_collector.utils;

/**
 * 主机配置信息Class,与Json配置文件字段一一映射
 */
public class HostConfigInfo {
    public HostConfigInfo(){}
    public HostConfigInfo(String _ip, String _username, String _password){
        ip = _ip;
        username = _username;
        password = _password;
        proxy = false;
    }
    public void setProxy(String _proxyIp,int _proxyPort){
        proxyIp = _proxyIp;
        proxyPort = _proxyPort;
        proxy = true;
    }

    //IP
    public String ip;
    //用户名
    public String username;
    //密码
    public String password;

    //代理
    public boolean proxy;

    public String proxyIp;

    public int proxyPort;
}
