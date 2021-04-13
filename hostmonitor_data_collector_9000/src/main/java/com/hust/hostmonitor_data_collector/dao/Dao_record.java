package com.hust.hostmonitor_data_collector.dao;


import com.hust.hostmonitor_data_collector.dao.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface Dao_record {

    @Insert("insert into NodeRecord values\n"+
            "(#{ip},#{timestamp},#{receiveBW},#{transmitBW},#{cpuUsage},#{memoryUsage},#{diskUsage},#{iNumber},#{oNumber},#{temp},#{energy})"
            )
    void insertNewRecord(@Param("ip") String ip,
                         @Param("timestamp")Timestamp timestamp,
                         @Param("receiveBW")float receiveBW,
                         @Param("transmitBW")float transmitBW,
                         @Param("cpuUsage")float cpuUsage,
                         @Param("memoryUsage")float memoryUsage,
                         @Param("diskUsage")float diskUsage,
                         @Param("iNumber")int iNumber,
                         @Param("oNumber")int oNumber,
                         @Param("temp")float temp,
                         @Param("energy")float energy
                         );
    @Select("select * from NodeRecord where ip=#{ip} order by timestamp")
    List<Record> recordQueryWithIP(@Param("ip") String ip);

    @Select("select * from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp " )
    List<Record> recordQueryWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);

    @Select("select count(*) from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp ")
    int recordNumberQueryWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);
    @Select("select receiveBW,transmitBW,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<BWrecord> getBWWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);

    @Select("select transmitBW,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp"  )
    List<BWrecord2> getTransmitBWWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);

    @Select("select receiveBW,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp"  )
    List<BWrecord3> getReceiveBWWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);
    @Select("select memoryUsage,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<MemoryUsageRecord> getMemoryUsageWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select diskUsage,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<DiskUsageRecord> getDiskUsageWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select temp,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<TemperatureRecord> getTempWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select cpuUsage,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<CpuUsageRecord> getCpuUsageWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);

    @Select("select energy,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<EnergyRecord> getEnergyWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select iNumber,oNumber,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<IOrecord> getIOWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select iNumber,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<IOrecord2> getInumberWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select oNumber,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<IOrecord3> getOnumberWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select count(*) from NodeRecord " +
            "where timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp ")
    int allRecordNumberQueryWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound);

    @Select("select * from NodeRecord where timestamp > #{lowbound} and timestamp < #{highbound} ")
    List<Record> allCpuUsageQueryWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound);
}
