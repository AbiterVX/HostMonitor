package com.hust.hostmonitor_data_collector.dao.provider;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;

public class DispersedProvider {
    public int dataSourceSelect= ConfigDataManager.getInstance().getConfigJson().getInteger("DataSourceSelect");

    public String insertNewRecord(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL= "insert into DispersedMonitorRecord values (" +
                    "#{hostname},#{ip},#{timestamp},#{MemUsage},#{CpuUsage},#{NetRecv},#{NetSent},#{DiskReadRates},#{DiskWriteRates})";
        }
        else if (dataSourceSelect==1){
            SQL= "insert into storagedevicemonitor.DISPERSEDMONITORRECORD values (" +
                    "#{hostname},#{ip},#{timestamp},#{MemUsage},#{CpuUsage},#{NetRecv},#{NetSent},#{DiskReadRates},#{DiskWriteRates})";
        }
        return SQL;
    }
    public String queryRecordsWithTimeLimit(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="Select * from DispersedMonitorRecord " +
                    "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
                    "order by timestamp";
        }
        else if (dataSourceSelect==1){
            SQL="Select * from storagedevicemonitor.DispersedMonitorRecord " +
                    "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
                    "order by timestamp";
        }
        return SQL;
    }

}
