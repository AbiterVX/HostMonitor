package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.python.antlr.ast.Str;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DiskFailureMapper {


    @Select("select details from DiskFailure where ip=#{ip} and timestamp=#{resultDate}")
    List<String> getDiskFailureInfo(@Param("ip")String ip,@Param("resultDate")String resultDate);

    @Insert("insert into diskHardwareInfo values (" +
            "#{diskSerial},#{hostName},#{capacity},#{isSSD},#{model})")
    void insertDiskHardwareInfo(@Param("diskSerial")String diskSerial,
                                @Param("hostName")String hostName,
                                @Param("capacity")double size,
                                @Param("isSSD")boolean isSSD,
                                @Param("model")String model);



    @Insert("insert into diskSampleInfo values (" +
            "#{diskSerial},#{timestamp},#{IOPS},#{ReadSpeed},#{WriteSpeed})")
    void insertDiskSampleInfo(@Param("diskSerial")String diskSerial,
                              @Param("timestamp")Timestamp timestamp,
                              @Param("IOPS")double iops,
                              @Param("ReadSpeed")double ReadSpeed,
                              @Param("WriteSpeed")double WriteSpeed);

    @Insert("insert into diskDFPInfo values (" +
            "#{diskSerial},#{hostName},#{timestamp},#{predictProbability},#{modelName})")
    void insertDiskDFPInfo(@Param("diskSerial")String diskSerial,
                           @Param("hostName")String hostName,
                           @Param("timestamp")Timestamp timestamp,
                           @Param("predictProbability") double predictProbability,
                           @Param("modelName")String modelName);

    //TODO 插入模型信息
    void insertTrainInfo();

    @Select("select * from diskHardwareInfo where diskSerial=#{diskSerial}")
    List<DiskHardWareInfo> queryDiskHardwareInfo(@Param("diskSerial") String diskSerial);

    @Select("select diskSerial from diskHardwareInfo")
    List<String> getDiskSerialList();

    @Select("select * from diskDFPInfo where diskSerial=#{diskSerial} order by timestamp desc limit 0,1")
    DFPRecord selectLatestDFPRecord(@Param("diskSerial")String diskSerial);

    @Select("select hostName from diskHardwareInfo where diskSerial=#{diskSerial}")
    String getHostName(@Param("diskSerial")String diskSerial);

    @Select("select a.* from " +
            "(select diskSerial,max(timestamp) timestamp from diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
            "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp")
    List<DFPRecord> selectLatestDFPRecordList();

    //TODO 查找模型信息
    void selectTrainModel();

    @Select("select diskSerial from diskHardwareInfo where diskSerial=#{diskSerial}")
    String queryDiskHardwareExists(@Param("diskSerial") String diskName);
}
