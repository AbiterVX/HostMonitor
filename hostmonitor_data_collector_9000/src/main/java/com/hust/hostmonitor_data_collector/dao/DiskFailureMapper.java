package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
            "#{diskSerial},#{timestamp},#{predictProbability},#{modelName})")
    void insertDiskDFPInfo(@Param("diskSerial")String diskSerial,
                           @Param("timestamp")Timestamp timestamp,
                           @Param("predictProbability") double predictProbability,
                           @Param("modelName")String modelName);

    @Insert("insert into trainInfo values (" +
            "#{timestamp},#{predictModel},#{diskModel},#{FDR},#{FAR},#{AUC},#{FNR},#{Accuracy},#{Precesion},#{Specificity},#{ErrorRate},#{Parameters})")
    void insertTrainInfo(@Param("timestamp")Timestamp timestamp, @Param("predictModel")String predictModel, @Param("diskModel")String diskModel,
                         @Param("FDR")double FDR, @Param("FAR")double FAR, @Param("AUC")double AUC, @Param("FNR")double FNR,
                         @Param("Accuracy")double Accuracy,@Param("Precision")double precision,@Param("Specificity")double Specificity,
                         @Param("ErrorRate")double ErrorRate,@Param("Parameters")String Parameters);

    @Select("select * from diskHardwareInfo where diskSerial=#{diskSerial}")
    List<DiskHardWareInfo> queryDiskHardwareInfo(@Param("diskSerial") String diskSerial);

    @Select("select diskSerial from diskHardwareInfo")
    List<String> getDiskSerialList();

    @Select("select a.diskSerial,b.hostName,a.timestamp,a.predictProbability,a.modelName from " +
            "(select * from diskDFPInfo a where diskSerial=#{diskSerial} order by timestamp desc limit 0,1) " +
            "join diskHardwareInfo b on a.diskSerial=b.diskSerial")
    DFPRecord selectLatestDFPRecord(@Param("diskSerial")String diskSerial);

    @Select("select hostName from diskHardwareInfo where diskSerial=#{diskSerial}")
    String getHostName(@Param("diskSerial")String diskSerial);

    @Select("select a.diskSerial,c.hostName,c.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
            "((select diskSerial,max(timestamp) timestamp from diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
            "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join diskHardwareInfo c on a.diskSerial=c.diskSerial")
    List<DFPRecord> selectLatestDFPRecordList();

    @Select("select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,c.hostName,c.size,c.isSSd,c.model from " +
            "((select diskSerial,max(timestamp) timestamp from diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
            "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join diskHardwareInfo c on a.diskSerial=c.diskSerial")
    List<HardWithDFPRecord> selectLatestDFPWithHardwareRecordList();



    @Select("select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
            "diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial where a.diskSerial=#{diskSerial}")
    List<DFPRecord> selectDFPRecords(@Param("diskSerial") String diskSerial);




    @Select("select diskSerial from diskHardwareInfo where diskSerial=#{diskSerial}")
    String queryDiskHardwareExists(@Param("diskSerial") String diskName);


    //表TrainInfo
    @Select("select count(*) from trainInfo")
    int queryTrainListCount();

    @Select("select * from trainInfo order by timestamp limit 0,#{number}")
    List<TrainInfo> selectTrainModel(@Param("number") int number );

    @Select("select * from trainInfo where id>#{id} limit #{pageSize}")
    List<TrainInfo> selectTrainInfoInPage(@Param("id") int idLowbound,
                                          @Param("pageSize")int pageSize);
}
