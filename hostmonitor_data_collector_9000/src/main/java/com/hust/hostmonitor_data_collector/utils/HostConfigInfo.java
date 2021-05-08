package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 主机配置信息Class,与Json配置文件字段一一映射
 */
@Data
public class HostConfigInfo {
    public HostConfigInfo(){
        proxy = false;
    }
    public void setProxy(ProxyConfig proxyConfig){
        proxyIp = proxyConfig.proxyIp;
        proxyPort = proxyConfig.proxyPort;
        proxy = true;
    }

    //IP
    @ExcelProperty("ip")
    public String ip;
    //用户名
    @ExcelProperty("username")
    public String username;
    //密码
    @ExcelProperty("password")
    public String password;

    //代理
    public boolean proxy;

    @ExcelProperty("proxyId")
    public int proxyId;

    public String proxyIp;

    public int proxyPort;

    @Override
    public String toString(){
        return "["+ ip +"," + username+","+ password+","+proxy+","+proxyIp +","+proxyPort +","+ proxyId+"]";
    }
}
