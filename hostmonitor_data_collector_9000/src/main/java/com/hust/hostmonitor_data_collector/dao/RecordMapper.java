package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RecordMapper {
    @Insert("insert into HustMonitorRecord values (" +
            "#{ip},#{timestamp},#{NetReceive},#{NetSend},#{MemTotal},#{MemFree},#{MemAvailable},#{Buffers},#{Cached}," +
            "#{TcpEstablished},#{DiskTotalSize},#{DiskOccupancyUsage},#{CpuIdle},#{Power},#{Temperature},#{Iops},#{Type},#{ReadRates},#{WriteRates},#{Utils})")
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
    @Select("Select * from HustMonitorRecord " +
            "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
            "order by timestamp")
    List<Record> queryRecordsWithTimeLimit(@Param("lowbound")Timestamp lowbound,
                                           @Param("highbound")Timestamp highbound,
                                           @Param("ip")String ip);

}
