package com.hust.hostmonitor_data_collector.utils.SSHConnect;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

public class ProxyConfigData {
    public int proxyId;
    public String proxyIp;
    public int proxyPort;

    public ProxyConfigData(){
        proxyId = -1;
        proxyIp = "";
        proxyPort = 0;
    }

    public ProxyConfigData(int proxyId, String proxyIp, int proxyPort) {
        this.proxyId = proxyId;
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
    }

    @Override
    public String toString(){
        return "["+ proxyId +"," + proxyIp+","+ proxyPort+"]";
    }
}
