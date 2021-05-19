package com.hust.hostmonitor_data_collector.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

@Mapper
public interface ProcessMapper {
    @Insert("insert into ProcessData values(" +
            "#{ip},#{timestamp},#{pid},#{uid},#{readKbps},#{writeKbps},#{command})")
    void insertProcessRecord(@Param("ip") String ip,
                             @Param("timestamp") Timestamp timestamp,
                             @Param("pid") String pid,
                             @Param("uid") String uid,
                             @Param("readKbps")String readKbps,
                             @Param("writeKbps")String writeKbps,
                             @Param("command")String command);



}
