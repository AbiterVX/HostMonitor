package com.hust.hostmonitor_data_collector.utils;

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

    @Override
    public String toString(){
        return "["+ proxyId +"," + proxyIp+","+ proxyPort+"]";
    }
}
