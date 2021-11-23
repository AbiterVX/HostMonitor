package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.*;
import com.hust.hostmonitor_data_collector.dao.provider.DiskFailureProvider;
import org.apache.ibatis.annotations.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DiskFailureMapper {


    @SelectProvider(type = DiskFailureProvider.class,method = "getDiskFailureInfo")
    List<String> getDiskFailureInfo(@Param("ip")String ip,@Param("resultDate")String resultDate);

    @InsertProvider(type = DiskFailureProvider.class,method = "insertDiskHardwareInfo")
    void insertDiskHardwareInfo(@Param("diskSerial")String diskSerial,
                                @Param("hostName")String hostName,

                                @Param("capacity")double size,
                                @Param("isSSD")boolean isSSD,
                                @Param("model")String model,
                                @Param("ip")String ip);

    @InsertProvider(type =DiskFailureProvider.class,method = "insertDiskSampleInfo")
    void insertDiskSampleInfo(@Param("diskSerial")String diskSerial,
                              @Param("timestamp")Timestamp timestamp,
                              @Param("IOPS")double iops,
                              @Param("ReadSpeed")double ReadSpeed,
                              @Param("WriteSpeed")double WriteSpeed);

    @InsertProvider(type= DiskFailureProvider.class,method = "insertDiskDFPInfo")
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

    @InsertProvider(type=DiskFailureProvider.class,method = "insertTrainInfo")
    void insertTrainInfo(@Param("Timestamp")Timestamp timestamp,@Param("PredictModel")int PredictModel, @Param("DiskModel")String DiskModel,
                         @Param("FDR")float FDR, @Param("FAR")float FAR, @Param("AUC")float AUC, @Param("FNR")float FNR,
                         @Param("Accuracy")float Accuracy, @Param("Precision")float Precision, @Param("Specificity")float Specificity,
                         @Param("ErrorRate")float ErrorRate, @Param("Parameters")String Parameters, @Param("OperatorID")String OperatorID);



    @UpdateProvider(type=DiskFailureProvider.class,method = "updateTrainInfo")
    void updateTrainInfo(@Param("timestamp")Timestamp timestamp, @Param("predictModel")String predictModel, @Param("diskModel")String diskModel,
                         @Param("FDR")double FDR, @Param("FAR")double FAR, @Param("AUC")double AUC, @Param("FNR")double FNR,
                         @Param("Accuracy")double Accuracy,@Param("Precision")double precision,@Param("Specificity")double Specificity,
                         @Param("ErrorRate")double ErrorRate);

    @SelectProvider(type=DiskFailureProvider.class,method = "queryDiskHardwareInfo")
    List<DiskHardWareInfo> queryDiskHardwareInfo(@Param("diskSerial") String diskSerial);

    @SelectProvider(type=DiskFailureProvider.class,method = "getDiskSerialList")
    List<String> getDiskSerialList();

    @SelectProvider(type=DiskFailureProvider.class,method = "selectLatestDFPRecord")
    DFPRecord selectLatestDFPRecord(@Param("diskSerial")String diskSerial);

    @SelectProvider(type=DiskFailureProvider.class,method = "getHostName")
    String getHostName(@Param("diskSerial")String diskSerial);

    @SelectProvider(type=DiskFailureProvider.class,method = "selectLatestDFPRecordList")
    List<DFPRecord> selectLatestDFPRecordList();

    @SelectProvider(type=DiskFailureProvider.class,method="selectLatestDFPWithHardwareRecordList")
    List<HardWithDFPRecord> selectLatestDFPWithHardwareRecordList();

//    @Select("select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,b.hostName,b.hostIp,b.size,b.isSSd,b.model from " +
//            "diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial where timestamp>=#{lowbound} order by timestamp")
//    List<HardWithDFPRecord> selectRecentDFPWithHardwareRecordList(@Param("lowbound")Timestamp timestamp);

    @SelectProvider(type=DiskFailureProvider.class,method = "selectRecentDFPWithHardwareRecordList")
    List<HardWithDFPRecord> selectRecentDFPWithHardwareRecordList(@Param("lowbound")Timestamp timestamp);

    @SelectProvider(type=DiskFailureProvider.class,method = "selectDFPRecords")
    List<DFPRecord> selectDFPRecords(@Param("diskSerial") String diskSerial);

    @SelectProvider(type=DiskFailureProvider.class,method = "selectLatestRecordTime")
    Timestamp selectLatestRecordTime();

    @SelectProvider(type=DiskFailureProvider.class,method="selectDFPRecordsByLowbound")
    List<DFPRecord> selectDFPRecordsByLowbound(@Param("lowbound")Timestamp lowbound);

    @SelectProvider(type=DiskFailureProvider.class,method="queryDiskHardwareExists")
    String queryDiskHardwareExists(@Param("diskSerial") String diskName);


    //表TrainInfo
    @SelectProvider(type=DiskFailureProvider.class,method="queryTrainListCount")
    int queryTrainListCount();

    @SelectProvider(type=DiskFailureProvider.class,method="selectTrainModel")
    List<TrainInfo> selectTrainModel(@Param("number") int number );

    @SelectProvider(type=DiskFailureProvider.class,method = "selectTrainInfoInPage")
    List<TrainInfo> selectTrainInfoInPage(@Param("id") int idLowbound,
                                          @Param("pageSize")int pageSize);


    @SelectProvider(type = DiskFailureProvider.class,method = "selectLatestTrainingSummary")
    StatisRecord selectLatestTrainingSummary();

    @InsertProvider(type=DiskFailureProvider.class,method = "insertIntoRealDiskFailureInfo")
    void insertIntoRealDiskFailureInfo(@Param("timestamp")Timestamp timestamp,
                                       @Param("diskSerial")String diskSerial);

    @SelectProvider(type=DiskFailureProvider.class,method = "selectLatestFailureTime")
    Timestamp selectLatestFailureTime();

    @SelectProvider(type=DiskFailureProvider.class,method = "selectRecentRecords")
    List<RealDiskFailure> selectRecentRecords(@Param("lowbound")Timestamp lowbound);



    @SelectProvider(type = DiskFailureProvider.class,method = "selectAllTrainInfo")
    List<TrainInfo> selectAllTrainInfo();

    @SelectProvider(type = DiskFailureProvider.class,method = "checkRecordExists")
    int checkRecordExists(@Param("diskSerial")String diskSerial,
                          @Param("timestamp")Timestamp timestamp);

    @UpdateProvider(type= DiskFailureProvider.class,method="updateDiskState")
    void updateDiskState(@Param("diskSerial")String diskSerial,@Param("state")Boolean state);

    @SelectProvider(type= DiskFailureProvider.class,method = "selectAllFailureWithHardwareLists")
    List<DiskHardWareInfo> selectAllFailureWithHardwareLists();
}
