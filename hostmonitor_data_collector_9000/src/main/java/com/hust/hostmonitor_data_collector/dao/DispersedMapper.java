package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DispersedMapper {
    @Insert("insert into DispersedMonitorRecord values (" +
            "#{hostname},#{ip},#{timestamp},#{MemUsage},#{CpuUsage},#{NetRecv},#{NetSent},#{DiskReadRates},#{DiskWriteRates})")
    void insertNewRecord(@Param("hostname") String hostname,
                         @Param("ip") String ip,
                         @Param("timestamp") Timestamp timestamp,
                         @Param("MemUsage")double MemUsage,
                         @Param("CpuUsage")Double CpuUsage,
                         @Param("NetRecv")Double NetRecv,
                         @Param("NetSent")Double NetSent,
                         @Param("DiskReadRates") double DiskReadRates,
                         @Param("DiskWriteRates") double DiskWriteRates);
    @Select("Select * from DispersedMonitorRecord " +
            "where hostname=#{hostname} and timestamp<#{highbound} and timestamp>#{lowbound} " +
            "order by timestamp")
    List<DispersedRecord> queryRecordsWithTimeLimit(@Param("lowbound")Timestamp lowbound,
                                                    @Param("highbound")Timestamp highbound,
                                                    @Param("hostname")String hostname);

}
