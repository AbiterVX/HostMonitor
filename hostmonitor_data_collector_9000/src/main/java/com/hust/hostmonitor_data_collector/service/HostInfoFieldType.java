package com.hust.hostmonitor_data_collector.service;


public enum HostInfoFieldType {
    CpuUsage,

    MemoryUsage,

    DiskOccupancyUsage,
    Disk_Util,
    Disk_Iops,
    Disk_Read,
    Disk_Write,

    NetSend,
    NetReceive,
    TcpEstablished,

    Temperature,

    Power,
}
