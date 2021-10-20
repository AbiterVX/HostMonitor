package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.Record;
import com.hust.hostmonitor_data_collector.dao.provider.RecordProvider;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RecordMapper {
    @InsertProvider(type = RecordProvider.class,method = "insertNewRecord")
    void insertNewRecord(@Param("ip") String ip,
                         @Param("timestamp") Timestamp timestamp,
                         @Param("NetReceive")Double NetReceive,
                         @Param("NetSend")Double NetSend,
                         @Param("MemTotal")Integer MemTotal,
                         @Param("MemFree")Integer MemFree,
                         @Param("MemAvailable")Integer MemAvailable,
                         @Param("Buffers")Double Buffers,
                         @Param("Cached")Double Cached,
                         @Param("TcpEstablished")Integer TcpEstablished,
                         @Param("DiskTotalSize")Double DiskTotalSize,
                         @Param("DiskOccupancyUsage")Double DiskOccupancyUsage,
                         @Param("CpuIdle")Double CpuIdle,
                         @Param("Power")Double Power,
                         @Param("Temperature") Double Temperature,
                         @Param("Iops") Integer Iops,
                         @Param("Type")String Type,
                         @Param("ReadRates") Double ReadRates,
                         @Param("WriteRates") Double WriteRates,
                         @Param("Utils") Double utils);
    @SelectProvider(type = RecordProvider.class,method = "queryRecordsWithTimeLimit")
    List<Record> queryRecordsWithTimeLimit(@Param("lowbound")Timestamp lowbound,
                                                    @Param("highbound")Timestamp highbound,
                                                    @Param("ip")String ip);

}
