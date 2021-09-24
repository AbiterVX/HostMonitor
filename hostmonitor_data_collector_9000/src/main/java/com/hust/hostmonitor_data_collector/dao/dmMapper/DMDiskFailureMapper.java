package com.hust.hostmonitor_data_collector.dao.dmMapper;

import com.hust.hostmonitor_data_collector.dao.entity.*;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DMDiskFailureMapper {


    @Select("select details from storagedevicemonitor.DiskFailure where ip=#{ip} and timestamp=#{resultDate}")
    List<String> getDiskFailureInfo(@Param("ip")String ip,@Param("resultDate")String resultDate);

    @Insert("insert into storagedevicemonitor.diskHardwareInfo values (" +
            "#{diskSerial},#{hostName},#{capacity},#{isSSD},#{model},#{ip})")
    void insertDiskHardwareInfo(@Param("diskSerial")String diskSerial,
                                @Param("hostName")String hostName,

                                @Param("capacity")double size,
                                @Param("isSSD")boolean isSSD,
                                @Param("model")String model,
                                @Param("ip")String ip);



    @Insert("insert into storagedevicemonitor.diskSampleInfo values (" +
            "#{diskSerial},#{timestamp},#{IOPS},#{ReadSpeed},#{WriteSpeed})")
    void insertDiskSampleInfo(@Param("diskSerial")String diskSerial,
                              @Param("timestamp")Timestamp timestamp,
                              @Param("IOPS")double iops,
                              @Param("ReadSpeed")double ReadSpeed,
                              @Param("WriteSpeed")double WriteSpeed);

    @Insert("insert into storagedevicemonitor.diskDFPInfo values (" +
            "#{diskSerial},#{timestamp},#{predictProbability},#{modelName},#{predictTime})")
    void insertDiskDFPInfo(@Param("diskSerial")String diskSerial,
                           @Param("timestamp")Timestamp timestamp,
                           @Param("predictProbability") double predictProbability,
                           @Param("modelName")String modelName,
                           @Param("predictTime")Timestamp predictTime);



//    @Insert("insert into trainInfo(timestamp,predictModel,diskModel,FDR,FAR,AUC,FNR,Accuracy,pre,Specificity,ErrorRate,extraParams,operatorID) " +
//            "values (#{timestamp},#{predictModel},#{diskModel},#{FDR},#{FAR},#{AUC},#{FNR},#{Accuracy}," +
//            "#{Precision},#{Specificity},#{ErrorRate},#{Parameters},#{UserId})")
//    void insertTrainInfo(@Param("timestamp")Timestamp timestamp, @Param("predictModel")String predictModel, @Param("diskModel")String diskModel,
//                         @Param("FDR")double FDR, @Param("FAR")double FAR, @Param("AUC")double AUC, @Param("FNR")double FNR,
//                         @Param("Accuracy")double Accuracy,@Param("Precision")double precision,@Param("Specificity")double Specificity,
//                         @Param("ErrorRate")double ErrorRate,@Param("Parameters")String Parameters,@Param("UserId")String UserId);

    @Insert("set @date=now();\n" +
            "insert into storagedevicemonitor.trainInfo values (@date,#{PredictModel},#{DiskModel}, " +
            "#{FDR}, #{FAR}, #{AUC}, #{FNR}, #{Accuracy}, #{Precision},  #{Specificity}, #{ErrorRate}," +
            " #{Parameters},#{OperatorID});")
    void insertTrainInfo(@Param("PredictModel")int PredictModel, @Param("DiskModel")String DiskModel,
                         @Param("FDR")float FDR, @Param("FAR")float FAR, @Param("AUC")float AUC, @Param("FNR")float FNR,
                         @Param("Accuracy")float Accuracy,@Param("Precision")float Precision,@Param("Specificity")float Specificity,
                         @Param("ErrorRate")float ErrorRate,@Param("Parameters")String Parameters,@Param("OperatorID")String OperatorID);



    @Update("update storagedevicemonitor.trainInfo set FDR=#{FDR},FAR=#{FAR},AUC=#{AUC},FNR=#{FNR},Accuracy=#{Accuracy},Pre=#{Precision}," +
            "Specificity=#{Specificity},ErrorRate=#{ErrorRate} where timestamp=#{timestamp},predictModel=#{predictModel},diskModel=#{diskModel}")
    void updateTrainInfo(@Param("timestamp")Timestamp timestamp, @Param("predictModel")String predictModel, @Param("diskModel")String diskModel,
                         @Param("FDR")double FDR, @Param("FAR")double FAR, @Param("AUC")double AUC, @Param("FNR")double FNR,
                         @Param("Accuracy")double Accuracy,@Param("Precision")double precision,@Param("Specificity")double Specificity,
                         @Param("ErrorRate")double ErrorRate);

    @Select("select * from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}")
    List<DiskHardWareInfo> queryDiskHardwareInfo(@Param("diskSerial") String diskSerial);

    @Select("select diskSerial from storagedevicemonitor.diskHardwareInfo")
    List<String> getDiskSerialList();

    @Select("select a.diskSerial,b.hostName,a.timestamp,a.predictProbability,a.modelName from " +
            "(select * from storagedevicemonitor.diskDFPInfo a where diskSerial=#{diskSerial} order by timestamp desc limit 0,1) " +
            "join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial")
    DMDFPRecord selectLatestDFPRecord(@Param("diskSerial")String diskSerial);

    @Select("select hostName from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}")
    String getHostName(@Param("diskSerial")String diskSerial);

    @Select("select a.diskSerial,c.hostName,c.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
            "((select diskSerial,max(timestamp) timestamp from storagedevicemonitor.diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
            "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join storagedevicemonitor.diskHardwareInfo c on a.diskSerial=c.diskSerial")
    List<DMDFPRecord> selectLatestDFPRecordList();

    @Select("select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,c.hostName,c.hostIp,c.size,c.isSSd,c.model from " +
            "((select diskSerial,max(timestamp) timestamp from storagedevicemonitor.diskDFPInfo group by diskSerial) b join storagedevicemonitor.diskDFPInfo a " +
            "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join storagedevicemonitor.diskHardwareInfo c on a.diskSerial=c.diskSerial")
    List<DMHardWithDFPRecord> selectLatestDFPWithHardwareRecordList();

//    @Select("select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,b.hostName,b.hostIp,b.size,b.isSSd,b.model from " +
//            "diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial where timestamp>=#{lowbound} order by timestamp")
//    List<DMHardWithDFPRecord> selectRecentDFPWithHardwareRecordList(@Param("lowbound")Timestamp timestamp);

    @Select("select a.diskSerial diskSerial,a.timestamp timestamp,a.predictProbability predictProbability,a.modelName modelName,b.hostName hostName,b.hostIp hostIp,b.size size,b.isSSd isSSd,b.model model from " +
            "(storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
            "join (select max(timestamp) maxtimestamp from storagedevicemonitor.diskDFPInfo where timestamp>=#{lowbound} group by diskSerial) c on c.maxtimestamp=a.timestamp order by timestamp")
    List<DMHardWithDFPRecord> selectRecentDFPWithHardwareRecordList(@Param("lowbound")Timestamp timestamp);

    @Select("select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
            "storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial where a.diskSerial=#{diskSerial}")
    List<DMDFPRecord> selectDFPRecords(@Param("diskSerial") String diskSerial);

    @Select("select max(timestamp) from storagedevicemonitor.diskDFPInfo")
    Timestamp selectLatestRecordTime();

    @Select("select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName" +
            " from (storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
            "join (select max(timestamp)maxtimestamp from storagedevicemonitor.diskDFPInfo where timestamp>=#{lowbound} group by diskSerial)c on c.maxtimestamp=a.timestamp")
    List<DMDFPRecord> selectDFPRecordsByLowbound(@Param("lowbound")Timestamp lowbound);

    @Select("select diskSerial from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}")
    String queryDiskHardwareExists(@Param("diskSerial") String diskName);


    //表TrainInfo
    @Select("select count(*) from storagedevicemonitor.trainInfo")
    int queryTrainListCount();

    @Select("select * from storagedevicemonitor.trainInfo order by timestamp limit 0,#{number}")
    List<TrainInfo> selectTrainModel(@Param("number") int number );



    @Select("select * from storagedevicemonitor.trainInfo where id>#{id} limit #{pageSize}")
    List<TrainInfo> selectTrainInfoInPage(@Param("id") int idLowbound,
                                          @Param("pageSize")int pageSize);


    @Select("select avg(FDR) FDR,avg(FAR) FAR,avg(AUC) AUC,avg(FNR) FNR,avg(Accuracy) Accuracy,avg(Pre) Pre,avg(Specificity) Specificity,avg(ErrorRate) ErrorRate from storagedevicemonitor.trainInfo " +
            "where timestamp in (select max(timestamp) from storagedevicemonitor.trainInfo)")
    StatisRecord selectLatestTrainingSummary();

    @Insert("insert into storagedevicemonitor.RealDiskFailureInfo(timestamp,diskSerial) values(#{timestamp},#{diskSerial})")
    void insertIntoRealDiskFailureInfo(@Param("timestamp")Timestamp timestamp,
                                       @Param("diskSerial")String diskSerial);

    @Select("select timestamp from storagedevicemonitor.RealDiskFailureInfo order by timestamp desc limit 0,1")
    Timestamp selectLatestFailureTime();

    @Select("select a.timestamp,a.diskSerial,b.model,b.isSSd from storagedevicemonitor.ReadDiskFailureInfo a join storagedevicemonitor.DiskHardwareInfo b on a.diskSerial=b.diskSerial where timestamp>=#{lowbound} order by timestamp")
    List<RealDiskFailure> selectRecentRecords(@Param("lowbound")Timestamp lowbound);



    @Select("select * from storagedevicemonitor.trainInfo order by Timestamp desc")
    List<TrainInfo> selectAllTrainInfo();

    @Select("select count(*) from storagedevicemonitor.diskDFPInfo where diskSerial=#{diskSerial} and timestamp=#{timestamp}")
    int checkRecordExists(@Param("diskSerial")String diskSerial,
                          @Param("timestamp")Timestamp timestamp);
}
