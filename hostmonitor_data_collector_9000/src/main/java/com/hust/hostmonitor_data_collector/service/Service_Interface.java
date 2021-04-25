package com.hust.hostmonitor_data_collector.service;

import com.hust.hostmonitor_data_collector.dao.entity.*;

import java.sql.Timestamp;

@Deprecated
public interface Service_Interface {

    void insertNewRecord(String ip, Timestamp timestamp, float receiveBW, float transmitBW, float cpuUsage, float memoryUsage,
                               float diskUsage, int iNumber, int oNumber, float temp, float energy);

    String getHostInfoListOutputData();

    String getHostIpList();

    String getSingleNewestInfoByIp(String ip);
    String getRecentInfoByIp(String ip, int numberOfDays, FieldType fieldType);
}
