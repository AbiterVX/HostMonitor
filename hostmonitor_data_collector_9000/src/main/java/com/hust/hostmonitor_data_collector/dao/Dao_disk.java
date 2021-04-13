package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface Dao_disk {
    @Select("select * from DiskFailure where ip=#{ip} ")
    List<DiskFailureRecord> getDiskFailureByIp(@Param("ip") String ip);

    @Select("select * from DiskFailure where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound} order by timestamp")
    List<DiskFailureRecord> getDiskFailureWithTimestamp(@Param("ip") String ip, @Param("lowbound")Timestamp lowbound,@Param("highbound")Timestamp highbound);

    @Select("select distinct timestamp from DiskFailure where ip=#{ip}")
    List<String> getDiskFailureTimestamp(@Param("ip") String ip);
    @Select("select details from DiskFailure where ip=#{ip} and timestamp=#{resultDate}")
    List<String> getDiskFailureInfo(@Param("ip")String ip,@Param("resultDate")String resultDate);
}
