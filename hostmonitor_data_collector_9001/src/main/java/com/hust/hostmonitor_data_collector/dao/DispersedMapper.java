package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import com.hust.hostmonitor_data_collector.dao.provider.DiskFailureProvider;
import com.hust.hostmonitor_data_collector.dao.provider.DispersedProvider;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DispersedMapper {
    @InsertProvider(type = DispersedProvider.class,method = "insertNewRecord")
    void insertNewRecord(@Param("hostname") String hostname,
                         @Param("ip") String ip,
                         @Param("timestamp") Timestamp timestamp,
                         @Param("MemUsage")double MemUsage,
                         @Param("CpuUsage")Double CpuUsage,
                         @Param("NetRecv")Double NetRecv,
                         @Param("NetSent")Double NetSent,
                         @Param("DiskReadRates") double DiskReadRates,
                         @Param("DiskWriteRates") double DiskWriteRates);
    @SelectProvider(type = DispersedProvider.class,method = "queryRecordsWithTimeLimit")
    List<DispersedRecord> queryRecordsWithTimeLimit(@Param("lowbound")Timestamp lowbound,
                                                    @Param("highbound")Timestamp highbound,
                                                    @Param("ip")String ip);

}
