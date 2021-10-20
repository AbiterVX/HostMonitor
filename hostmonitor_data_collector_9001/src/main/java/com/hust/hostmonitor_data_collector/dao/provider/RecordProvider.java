package com.hust.hostmonitor_data_collector.dao.provider;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;

public class RecordProvider {
    public int dataSourceSelect= ConfigDataManager.getInstance().getConfigJson().getInteger("DataSourceSelect");
    public String insertNewRecord(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into HustMonitorRecord values (" +
                    "#{ip},#{timestamp},#{NetReceive},#{NetSend},#{MemTotal},#{MemFree},#{MemAvailable},#{Buffers},#{Cached}," +
                    "#{TcpEstablished},#{DiskTotalSize},#{DiskOccupancyUsage},#{CpuIdle},#{Power},#{Temperature},#{Iops},#{Type},#{ReadRates},#{WriteRates},#{Utils})";
        }
        else if (dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.HustMonitorRecord values (" +
                    "#{ip},#{timestamp},#{NetReceive},#{NetSend},#{MemTotal},#{MemFree},#{MemAvailable},#{Buffers},#{Cached}," +
                    "#{TcpEstablished},#{DiskTotalSize},#{DiskOccupancyUsage},#{CpuIdle},#{Power},#{Temperature},#{Iops},#{Type},#{ReadRates},#{WriteRates},#{Utils})";
        }
        return SQL;
    }
    public String queryRecordsWithTimeLimit(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="Select * from HustMonitorRecord " +
                    "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
                    "order by timestamp";
        }
        else if (dataSourceSelect==1){
            SQL="Select * from storagedevicemonitor.HustMonitorRecord " +
                    "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
                    "order by timestamp";
        }
        return SQL;
    }
}
