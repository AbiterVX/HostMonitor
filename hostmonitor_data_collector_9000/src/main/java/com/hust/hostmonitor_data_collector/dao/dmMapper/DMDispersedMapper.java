package com.hust.hostmonitor_data_collector.dao.dmMapper;

import com.hust.hostmonitor_data_collector.dao.entity.DispersedRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DMDispersedMapper {
    @Insert("insert into storagedevicemonitor.DISPERSEDMONITORRECORD values (" +
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
    @Select("Select * from storagedevicemonitor.DispersedMonitorRecord " +
            "where ip=#{ip} and timestamp<#{highbound} and timestamp>#{lowbound} " +
            "order by timestamp")
    List<DispersedRecord> queryRecordsWithTimeLimit(@Param("lowbound")Timestamp lowbound,
                                                    @Param("highbound")Timestamp highbound,
                                                    @Param("ip")String ip);

}
