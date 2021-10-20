package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.provider.ProcessProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

@Mapper
public interface ProcessMapper {
    @InsertProvider(type = ProcessProvider.class,method = "insertProcessRecord")
    void insertProcessRecord(@Param("ip") String ip,
                             @Param("timestamp") Timestamp timestamp,
                             @Param("pid") String pid,
                             @Param("uid") String uid,
                             @Param("readKbps")String readKbps,
                             @Param("writeKbps")String writeKbps,
                             @Param("command")String command);



}
