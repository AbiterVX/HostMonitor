package com.hust.hostmonitor_data_collector.CentralizedHostMonitor;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 主机配置信息Class,与Json配置文件字段一一映射
 */
@Data
public class HostConfigInfo {
    public HostConfigInfo(){
        ip = "0.0.0.0";
        username = "null user";
        password = "null password";

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
    @ExcelIgnore
    public boolean proxy;

    @ExcelProperty("proxyId")
    public int proxyId;

    @ExcelIgnore
    public String proxyIp;

    @ExcelIgnore
    public int proxyPort;

    @Override
    public String toString(){
        return "["+ ip +"," + username+","+ password+","+proxy+","+proxyIp +","+proxyPort +","+ proxyId+"]";
    }
}
