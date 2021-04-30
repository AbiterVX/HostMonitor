package com.hust.hostmonitor_data_collector.service;


import java.util.HashMap;
import java.util.Map;

public enum HostInfoFieldType {
    CpuUsage("CpuUsage"),
    CpuIdle("CpuIdle"),
    MemoryUsage("MemoryUsage"),

    DiskOccupancyUsage("DiskOccupancyUsage"),
    Disk_Util("Disk_Util"),
    Disk_Iops("Disk_Iops"),
    Disk_Read("Disk_Read"),
    Disk_Write("Disk_Write"),

    NetSend("NetSend"),
    NetReceive("NetReceive"),
    TcpEstablished("TcpEstablished"),

    Temperature("Temperature"),

    Power("Power"),
    Meaningless(null);

    private final String value;
    private final static Map<String,HostInfoFieldType> FIELD_TYPE_MAP=new HashMap<String,HostInfoFieldType>();
    private HostInfoFieldType(String value){
        this.value=value;
    }
    public String value(){
        return value;
    }
    static{
        for(HostInfoFieldType f:values()){
            FIELD_TYPE_MAP.put(f.value(),f);
        }
    }
    public static HostInfoFieldType fromString(String f){
        HostInfoFieldType fieldType=FIELD_TYPE_MAP.get(f);
        return fieldType==null? Meaningless:fieldType;
    }
}
