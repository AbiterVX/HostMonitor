package com.hust.hostmonitor_data_collector.dao.provider;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public class DiskFailureProvider {
    public int dataSourceSelect= ConfigDataManager.getInstance().getDataSourceSelect();

    public String getDiskFailureInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select details from DiskFailure where ip=#{ip} and timestamp=#{resultDate}";

        }
        else if(dataSourceSelect==1){
            SQL="select details from storagedevicemonitor.DiskFailure where ip=#{ip} and timestamp=#{resultDate}";
        }
        return SQL;
    }
    public String insertDiskHardwareInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into diskHardwareInfo values (" +
                    "#{diskSerial},#{hostName},#{capacity},#{isSSD},#{model},#{ip})";
        }
        else if(dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.diskHardwareInfo values (" +
                    "#{diskSerial},#{hostName},#{capacity},#{isSSD},#{model},#{ip})";
        }
        return SQL;
    }

    public String insertDiskSampleInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into diskSampleInfo values (" +
                    "#{diskSerial},#{timestamp},#{IOPS},#{ReadSpeed},#{WriteSpeed})";
        }
        else if(dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.diskSampleInfo values (" +
                    "#{diskSerial},#{timestamp},#{IOPS},#{ReadSpeed},#{WriteSpeed})";
        }
        return SQL;
    }

    public String insertDiskDFPInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into diskDFPInfo values (" +
                    "#{diskSerial},#{timestamp},#{predictProbability},#{modelName},#{predictTime})";
        }
        else if(dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.diskDFPInfo values (" +
                    "#{diskSerial},#{timestamp},#{predictProbability},#{modelName},#{predictTime})";
        }
        return SQL;
    }

    public String insertTrainInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="set @date=now();\n" +
                    "insert into trainInfo values (@date,#{PredictModel},#{DiskModel}, " +
                    "#{FDR}, #{FAR}, #{AUC}, #{FNR}, #{Accuracy}, #{Precision},  #{Specificity}, #{ErrorRate}," +
                    " #{Parameters},#{OperatorID});";
        }
        else if(dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.trainInfo values (#{Timestamp},#{PredictModel},#{DiskModel}, " +
                    "#{FDR}, #{FAR}, #{AUC}, #{FNR}, #{Accuracy}, #{Precision},  #{Specificity}, #{ErrorRate}," +
                    " #{Parameters},#{OperatorID});";
        }
        return SQL;
    }

    public String updateTrainInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="update trainInfo set FDR=#{FDR},FAR=#{FAR},AUC=#{AUC},FNR=#{FNR},Accuracy=#{Accuracy},Pre=#{Precision}," +
                    "Specificity=#{Specificity},ErrorRate=#{ErrorRate} where timestamp=#{timestamp},predictModel=#{predictModel},diskModel=#{diskModel}";
        }
        else if(dataSourceSelect==1){
            SQL="update storagedevicemonitor.trainInfo set FDR=#{FDR},FAR=#{FAR},AUC=#{AUC},FNR=#{FNR},Accuracy=#{Accuracy},Pre=#{Precision}," +
                    "Specificity=#{Specificity},ErrorRate=#{ErrorRate} where timestamp=#{timestamp},predictModel=#{predictModel},diskModel=#{diskModel}";
        }
        return SQL;
    }
    public String queryDiskHardwareInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * from diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        else if(dataSourceSelect==1){
            SQL="select * from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        return SQL;
    }
    public String getDiskSerialList(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select diskSerial from diskHardwareInfo";
        }
        else if(dataSourceSelect==1){
            SQL="select diskSerial from storagedevicemonitor.diskHardwareInfo";
        }
        return SQL;
    }
    public String selectLatestDFPRecord(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial,b.hostName,a.timestamp,a.predictProbability,a.modelName from " +
                    "(select * from diskDFPInfo a where diskSerial=#{diskSerial} order by timestamp desc limit 0,1) " +
                    "join diskHardwareInfo b on a.diskSerial=b.diskSerial";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial,b.hostName,a.timestamp,a.predictProbability,a.modelName from " +
                    "(select * from storagedevicemonitor.diskDFPInfo a where diskSerial=#{diskSerial} order by timestamp desc limit 0,1) " +
                    "join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial";
        }
        return SQL;
    }
    public String getHostName(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select hostName from diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        else if(dataSourceSelect==1){
            SQL="select hostName from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        return SQL;
    }
    public String selectLatestDFPRecordList(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial,c.hostName,c.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
                    "((select diskSerial,max(timestamp) timestamp from diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
                    "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join diskHardwareInfo c on a.diskSerial=c.diskSerial";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial,c.hostName,c.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
                    "((select diskSerial,max(timestamp) timestamp from storagedevicemonitor.diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
                    "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join storagedevicemonitor.diskHardwareInfo c on a.diskSerial=c.diskSerial";
        }
        return SQL;
    }
    public String selectLatestDFPWithHardwareRecordList(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,c.hostName,c.hostIp,c.size,c.isSSd,c.model from " +
                    "((select diskSerial,max(timestamp) timestamp from diskDFPInfo group by diskSerial) b join diskDFPInfo a " +
                    "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join diskHardwareInfo c on a.diskSerial=c.diskSerial";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial,a.timestamp,a.predictProbability,a.modelName,c.hostName,c.hostIp,c.size,c.isSSd,c.model from " +
                    "((select diskSerial,max(timestamp) timestamp from storagedevicemonitor.diskDFPInfo group by diskSerial) b join storagedevicemonitor.diskDFPInfo a " +
                    "on a.diskSerial=b.diskSerial and a.timestamp=b.timestamp) join storagedevicemonitor.diskHardwareInfo c on a.diskSerial=c.diskSerial";
        }
        return SQL;
    }
    public String selectRecentDFPWithHardwareRecordList(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial diskSerial,a.timestamp timestamp,a.predictProbability predictProbability,a.modelName modelName,b.hostName hostName,b.hostIp hostIp,b.size size,b.isSSd isSSd,b.model model from " +
                    "(diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
                    "join (select max(timestamp) maxtimestamp from diskDFPInfo where timestamp>=#{lowbound} group by diskSerial) c on c.maxtimestamp=a.timestamp order by timestamp";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial diskSerial,a.timestamp timestamp,a.predictProbability predictProbability,a.modelName modelName,b.hostName hostName,b.hostIp hostIp,b.size size,b.isSSd isSSd,b.model model from " +
                    "(storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
                    "join (select max(timestamp) maxtimestamp from storagedevicemonitor.diskDFPInfo where timestamp>=#{lowbound} group by diskSerial) c on c.maxtimestamp=a.timestamp order by timestamp";
        }
        return SQL;
    }
    public String selectDFPRecords(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
                    "diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial where a.diskSerial=#{diskSerial}";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName from " +
                    "storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial where a.diskSerial=#{diskSerial}";
        }
        return SQL;
    }
    public String selectLatestRecordTime(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select max(timestamp) from diskDFPInfo";
        }
        else if(dataSourceSelect==1){
            SQL="select max(timestamp) from storagedevicemonitor.diskDFPInfo";
        }
        return SQL;
    }
    public String selectDFPRecordsByLowbound(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName" +
                    " from (diskDFPInfo a join diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
                    "join (select max(timestamp)maxtimestamp from diskDFPInfo where timestamp>=#{lowbound} group by diskSerial)c on c.maxtimestamp=a.timestamp";
        }
        else if(dataSourceSelect==1){
            SQL="select a.diskSerial,b.hostName,b.isSSd,a.timestamp,a.predictProbability,a.modelName" +
                    " from (storagedevicemonitor.diskDFPInfo a join storagedevicemonitor.diskHardwareInfo b on a.diskSerial=b.diskSerial) " +
                    "join (select max(timestamp)maxtimestamp from storagedevicemonitor.diskDFPInfo where timestamp>=#{lowbound} group by diskSerial)c on c.maxtimestamp=a.timestamp";
        }
        return SQL;
    }
    public String queryDiskHardwareExists(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select diskSerial from diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        else if(dataSourceSelect==1){
            SQL="select diskSerial from storagedevicemonitor.diskHardwareInfo where diskSerial=#{diskSerial}";
        }
        return SQL;
    }
    public String queryTrainListCount(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select count(*) from trainInfo";
        }
        else if(dataSourceSelect==1){
            SQL="select count(*) from storagedevicemonitor.trainInfo";
        }
        return SQL;
    }
    public String selectTrainModel(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * from trainInfo order by timestamp limit 0,#{number}";
        }
        else if(dataSourceSelect==1){
            SQL="select * from storagedevicemonitor.trainInfo order by timestamp limit 0,#{number}";
        }
        return SQL;
    }
    public String selectTrainInfoInPage(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * from trainInfo where id>#{id} limit #{pageSize}";
        }
        else if(dataSourceSelect==1){
            SQL="select * from storagedevicemonitor.trainInfo where id>#{id} limit #{pageSize}";
        }
        return SQL;
    }
    public String selectLatestTrainingSummary(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select avg(FDR) FDR,avg(FAR) FAR,avg(AUC) AUC,avg(FNR) FNR,avg(Accuracy) Accuracy,avg(Pre) Pre,avg(Specificity) Specificity,avg(ErrorRate) ErrorRate from trainInfo " +
                    "where timestamp in (select max(timestamp) from trainInfo)";
        }
        else if(dataSourceSelect==1){
            SQL="select avg(FDR) FDR,avg(FAR) FAR,avg(AUC) AUC,avg(FNR) FNR,avg(Accuracy) Accuracy,avg(Pre) Pre,avg(Specificity) Specificity,avg(ErrorRate) ErrorRate from storagedevicemonitor.trainInfo " +
                    "where timestamp in (select max(timestamp) from storagedevicemonitor.trainInfo)";
        }
        return SQL;
    }
    public String insertIntoRealDiskFailureInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into RealDiskFailureInfo(timestamp,diskSerial) values(#{timestamp},#{diskSerial})";
        }
        else if(dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.RealDiskFailureInfo(timestamp,diskSerial) values(#{timestamp},#{diskSerial})";
        }
        return SQL;
    }
    public String selectLatestFailureTime(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select timestamp from RealDiskFailureInfo order by timestamp desc limit 0,1";
        }
        else if(dataSourceSelect==1){
            SQL="select timestamp from storagedevicemonitor.RealDiskFailureInfo order by timestamp desc limit 0,1";
        }
        return SQL;
    }
    public String selectRecentRecords(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select a.timestamp,a.diskSerial,b.model,b.isSSd from ReadDiskFailureInfo a join DiskHardwareInfo b on a.diskSerial=b.diskSerial where timestamp>=#{lowbound} order by timestamp";
        }
        else if(dataSourceSelect==1){
            SQL="select a.timestamp,a.diskSerial,b.model,b.isSSd from storagedevicemonitor.ReadDiskFailureInfo a join storagedevicemonitor.DiskHardwareInfo b on a.diskSerial=b.diskSerial where timestamp>=#{lowbound} order by timestamp";
        }
        return SQL;
    }
    public String selectAllTrainInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * from trainInfo order by Timestamp desc";
        }
        else if(dataSourceSelect==1){
            SQL="select * from storagedevicemonitor.trainInfo order by Timestamp desc";
        }
        return SQL;
    }
    public String checkRecordExists(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select count(*) from diskDFPInfo where diskSerial=#{diskSerial} and timestamp=#{timestamp}";
        }
        else if(dataSourceSelect==1){
            SQL="select count(*) from storagedevicemonitor.diskDFPInfo where diskSerial=#{diskSerial} and timestamp=#{timestamp}";
        }
        return SQL;
    }
}
