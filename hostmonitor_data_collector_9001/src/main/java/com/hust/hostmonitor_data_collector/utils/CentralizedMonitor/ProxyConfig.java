package com.hust.hostmonitor_data_collector.utils.CentralizedMonitor;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProxyConfig {
    @ExcelProperty("proxyId")
    public int proxyId;
    @ExcelProperty("proxyIp")
    public String proxyIp;
    @ExcelProperty("proxyPort")
    public int proxyPort;

    public ProxyConfig(){
        proxyId = -1;
        proxyIp = "0.0.0.0";
        proxyPort = 0;
    }

    @Override
    public String toString(){
        return "["+ proxyId +"," + proxyIp+","+ proxyPort+"]";
    }
}
