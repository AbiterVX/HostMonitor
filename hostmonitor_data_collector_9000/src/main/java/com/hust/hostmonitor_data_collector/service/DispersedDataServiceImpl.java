package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import com.hust.hostmonitor_data_collector.dao.DispersedMapper;
import com.hust.hostmonitor_data_collector.dao.UserDao;
import com.hust.hostmonitor_data_collector.dao.entity.*;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredictProgress;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.PredictModel;
import com.hust.hostmonitor_data_collector.utils.DispersedHostMonitor;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


public class DispersedDataServiceImpl implements DispersedDataService{
    @Autowired
    DispersedMapper dispersedMapper;
    @Autowired
    DiskFailureMapper diskFailureMapper;
    @Autowired
    UserDao userDao;
    private DispersedHostMonitor dispersedHostMonitor;
    private String dataPath;
    //定时器周期参数
    private final long sampleInterval=10000;
    private final int sampleStoreDelayMS=500;
    private final int offset=1000;
    private final long predictInterval=24*3600*1000;
    //定时器
    private long DiskPredictTime;
    private Timer mainTimer = new Timer();
    private final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    //定时器任务
    private final TimerTask dataPersistanceTask = new TimerTask() {
        @Override
        public void run() {
            //采样
            System.out.println("[TimerTask:Data Persistance]"+new Date());
            try {
                Thread.sleep(sampleStoreDelayMS);
                //存储新采样的数据
                storeSampleData();
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println("[Thread Sleep Error]: In TimerTask run()");
            }
        }
    };
    //预测任务
    private final TimerTask diskPredictTask= new TimerTask() {
        @Override
        public void run() {
            System.out.println("[TimerTask:Disk Predict]"+new Date());
            diskPredict();
        }
    };





    public DispersedDataServiceImpl(){
        dataPath=System.getProperty("user.dir")+"/DiskPredict/";
        dispersedHostMonitor=DispersedHostMonitor.getInstance();
        mainTimer.schedule(dataPersistanceTask,sampleInterval/2,sampleInterval-offset);
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date date=calendar.getTime();
        if(date.before(new Date())){
            date=addDay(date,1);
        }
        System.out.println(date);
        //mainTimer.schedule(diskPredictTask,date,predictInterval);
        //FIXME
        mainTimer.schedule(diskPredictTask,0,predictInterval);
        //启动阶段以以默认参数自动训练一次

        /*JSONObject extraParams=new JSONObject();
        extraParams.put("max_depth",new int[]{10, 20, 30});
        extraParams.put("max_features",new int[]{4, 7, 10});
        extraParams.put("n_estimators",new int[]{10, 20, 30, 40});
        train(1,1.0f,3.0f,0.1f,extraParams,"hust");*/
    }
    private Date addDay(Date date,int num){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,num);
        return  calendar.getTime();
    }

    //-----模型训练进度条
    private boolean isTraining = false;
    private int currentTrainState = 0;
    private DiskPredictProgress preprocessProgress;
    private DiskPredictProgress getTrainDataProgress;
    private List<DiskPredictProgress> trainProgress;
    List<Float> progressPercentage = new ArrayList(Arrays.asList(-1,-1,-1));

    //获取模型训练进度
    @Override
    public List<Float> getTrainProgress(){
        return progressPercentage;
    }

    private void storeSampleData(){
        System.out.println("[hostInfoMap size]"+dispersedHostMonitor.hostInfoMap.size());
        for(Map.Entry<String, JSONObject> entry: dispersedHostMonitor.hostInfoMap.entrySet()){
            JSONObject tempObject=entry.getValue();

            if(!tempObject.getBoolean("hasPersistent")){
                double memUsage=tempObject.getJSONArray("memoryUsage").getDouble(0)/tempObject.getJSONArray("memoryUsage").getDouble(1);
                double DiskReadRates=0;
                double DiskWriteRates=0;
                for(int i=0;i<tempObject.getJSONArray("diskInfoList").size();i++){
                    DiskWriteRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskWriteSpeed");
                    DiskReadRates+=tempObject.getJSONArray("diskInfoList").getJSONObject(i).getDouble("diskReadSpeed");
                }
                if( tempObject.getDouble("cpuUsage")==0){
                    continue;
                }

                dispersedMapper.insertNewRecord(tempObject.getString("hostName"),tempObject.getString("ip"),
                        tempObject.getTimestamp("lastUpdateTime")/*这里可能要改时间*/,memUsage,
                        tempObject.getDouble("cpuUsage"),
                        tempObject.getDouble("netReceiveSpeed"),
                        tempObject.getDouble("netSendSpeed"),DiskReadRates,DiskWriteRates);
                entry.getValue().put("hasPersistent",true);
                System.out.println("[Database]Insert a record.");
                JSONArray diskArray=tempObject.getJSONArray("diskInfoList");
                for(int i=0;i<diskArray.size();i++){
                    JSONObject tempDiskObject=diskArray.getJSONObject(i);
                    String diskSerial=diskFailureMapper.queryDiskHardwareExists(tempDiskObject.getString("diskName"));
                    if(diskSerial==null){
                        diskSerial=tempDiskObject.getString("diskName");

                        System.out.println(tempDiskObject.getJSONArray("diskCapacitySize").getDoubleValue(1));
                        diskFailureMapper.insertDiskHardwareInfo(diskSerial,tempObject.getString("hostName"),
                                tempDiskObject.getJSONArray("diskCapacitySize").getDoubleValue(1),
                                tempDiskObject.getIntValue("type")>0? true:false,
                                tempDiskObject.getString("diskModel"));
                    }
                    System.out.println("["+diskSerial+"]"+tempObject.getTimestamp("lastUpdateTime"));
                    diskFailureMapper.insertDiskSampleInfo(diskSerial, tempObject.getTimestamp("lastUpdateTime"),tempDiskObject.getDoubleValue("diskIOPS"),
                            tempDiskObject.getDoubleValue("diskReadSpeed"),tempDiskObject.getDoubleValue("diskWriteSpeed"));
                }
            }


        }
    }

    /**
     * 获取信息-Dashboard-Summary统计
     * 格式：{"summary":}
     */
    @Override
    public String getDashboardSummary() {
        dispersedHostMonitor.UpdateSummaryInfo();
        return dispersedHostMonitor.summaryInfo.toJSONString();
    }

    /**
     * 获取信息-Dashboard-HostInfo-全部Host
     * 格式：{"hostName1":{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}, }
     */
    @Override
    public String getHostInfoDashboardAll() {
        JSONObject resultObject=new JSONObject();
        for(Map.Entry<String, JSONObject> entry: dispersedHostMonitor.hostInfoMap.entrySet()){
            resultObject.put(entry.getKey(),entry.getValue());
        }
        return resultObject.toJSONString();
    }

    /**
     * 获取信息-HostDetail-HostInfo-某个Host
     * 参数：hostName
     * 格式：{"hostInfo":{},"cpuInfoList":[],"gpuInfoList":{},"processInfoList":{}}
     */
    @Override
    public String getHostInfoDetail(String hostName) {
        String result=dispersedHostMonitor.hostInfoMap.get(hostName).toJSONString();
        return result;
    }
    private JSONObject selectMainProcess(double cpuThreshold,JSONObject jsonObject){
        JSONObject resultObject=new JSONObject();
        resultObject.putAll(jsonObject);
        Iterator iterator=resultObject.getJSONArray("processInfoList").iterator();
        while(iterator.hasNext()){
            JSONObject tempObject=(JSONObject) iterator.next();
            if(tempObject.getDouble("cpuUsage")<cpuThreshold){
                iterator.remove();
            }
            else{
                tempObject.put("cpuUsage",doubleTo2bits_double(tempObject.getDouble("cpuUsage")*100));
                tempObject.put("memUsage",doubleTo2bits_double(tempObject.getDouble("memoryUsage")*100));
            }
        }
        return resultObject;
    }
    private double doubleTo2bits_double(double original){
        BigDecimal b=new BigDecimal(original);
        return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取信息-Dashboard-DiskInfo-全部Host
     * 格式：{"hostName1":[{},{}], }
     */
    public String getDiskInfoAll() {
        return null;
    }

    /**
     * 获取信息-HostDetail-DiskInfo-某个Host
     * 参数：hostName
     * 格式：{}
     */
    public String getDiskInfo(String hostName) {
        return null;
    }

    //时间段 cpuusage,memory,
    @Override
    public String getHostInfoDetailTrend(String hostName) {
        int hours=24;
        Timestamp highbound=new Timestamp(System.currentTimeMillis());
        Timestamp lowbound=new Timestamp(System.currentTimeMillis()-hours*3600*1000);
        List<DispersedRecord> dispersedRecordList= dispersedMapper.queryRecordsWithTimeLimit(lowbound,highbound,hostName);

        JSONArray result=new JSONArray();
        for(int i=0;i<6;i++){
            result.add(new JSONArray());
        }

        for(DispersedRecord dispersedRecord:dispersedRecordList){
            Timestamp timestamp = dispersedRecord.getTimestamp();

            result.getJSONArray(0).add(createNewValue(timestamp,dispersedRecord.getCpuUsage()));
            result.getJSONArray(1).add(createNewValue(timestamp,dispersedRecord.getMemUsage()));
            result.getJSONArray(2).add(createNewValue(timestamp,dispersedRecord.getDiskReadRates()));
            result.getJSONArray(3).add(createNewValue(timestamp,dispersedRecord.getDiskWriteRates()));
            result.getJSONArray(4).add(createNewValue(timestamp,dispersedRecord.getNetRecv()));
            result.getJSONArray(5).add(createNewValue(timestamp,dispersedRecord.getNetSent()));
        }
        return  result.toJSONString();
    }

    private JSONArray createNewValue(Object object1,Object object2){
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(object1);
        jsonArray.add(object2);
        return  jsonArray;
    }

    /**
     * 获取信息-DFP-Trend-某个Host
     * 参数：hostName,diskName
     * 格式：[[0,0], ]
     */
    @Override
    public String getDFPInfoTrend(String hostName, String diskSerial) {
        JSONArray result=new JSONArray();
        List<DFPRecord> dfpRecordList=diskFailureMapper.selectDFPRecords(diskSerial);
        for(DFPRecord dfpRecord:dfpRecordList){
               result.add(createNewValue(dfpRecord.timestamp,dfpRecord.predictProbability));
        }
        return result.toJSONString();
    }

    /**
     * 获取信息-DFP-All
     * 格式：[{},{} ]
     * hostName,diskName,diskType,manufacturer,diskCapacity,model,predictTime,predictProbability,
     */
    @Override
    public String getDFPInfoAll() {
        JSONArray result=new JSONArray();
        List<HardWithDFPRecord> hardWithDFPRecordList=diskFailureMapper.selectLatestDFPWithHardwareRecordList();
        for(HardWithDFPRecord dfpRecord:hardWithDFPRecordList){
            JSONObject tempObject=new JSONObject();
            tempObject.put("hostName",dfpRecord.hostName);
            tempObject.put("diskSerial",dfpRecord.diskSerial);
            tempObject.put("diskType",dfpRecord.isSSd?1:0);
            tempObject.put("manufacturer",dfpRecord.model);
            tempObject.put("diskCapacity",dfpRecord.size);
            tempObject.put("model",dfpRecord.modelName);
            tempObject.put("timestamp",dfpRecord.timestamp);
            tempObject.put("predictProbability",dfpRecord.predictProbability);
            result.add(tempObject);
        }
        return result.toJSONString();
    }

    //有待实现对预测范围的选择
    private void diskPredict(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String date=sdf.format(calendar.getTime());
        DiskPredict.predictWithoutProgess(System.getProperty("user.dir")+"/DiskPredict/original_data/"+calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1),date+".csv");
        //+"/"+date+".csv"
        //在路径下读出所有的预测结果
        List<JSONObject> result=DiskPredict.getDiskPredictResult("/DiskPredict/result/"+sdf.format(new Date())+"/"+sdf.format(new Date())+".csv");
        // 插入数据库，注意修改接受文件时同时修改下列状态
        for(JSONObject jsonObject:result) {
            System.out.println(jsonObject);
            diskFailureMapper.insertDiskDFPInfo(jsonObject.getString("diskSerial"), jsonObject.getTimestamp("timestamp"), doubleTo2bits_double(jsonObject.getDoubleValue("predictProbability")*100), jsonObject.getString("modelName"));
        }
    }

    @Override
    public void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion, JSONObject extraParams,String operatorID){
        if(!isTraining){
            isTraining = true;
            progressPercentage = new ArrayList(Arrays.asList(0,0,0));
            //模型训练
            currentTrainState = 0;
            preprocessProgress = null;
            getTrainDataProgress = null;
            trainProgress = null;

            Thread progressThread = new Thread(() -> {
                try {
                    int modelYear= 2016; //Calendar.getInstance().get(Calendar.YEAR);

                    while (isTraining){
                        if(currentTrainState==0){
                            if(preprocessProgress== null){
                                preprocessProgress = DiskPredict.preprocess(""+modelYear,0);
                            }
                            else{
                                progressPercentage.set(0,preprocessProgress.getProgressPercentage());
                                if(preprocessProgress.isFinished()){
                                    currentTrainState = 1;
                                }
                            }
                        }
                        else if(currentTrainState==1){
                            if(getTrainDataProgress== null){
                                getTrainDataProgress = DiskPredict.getTrainData(String.valueOf(modelYear),positiveDataProportion/negativeDataProportion,verifyProportion);
                            }
                            else{
                                progressPercentage.set(1,getTrainDataProgress.getProgressPercentage());
                                if(getTrainDataProgress.isFinished()){
                                    currentTrainState = 2;
                                }
                            }
                        }
                        else if(currentTrainState==2){
                            if(trainProgress== null){
                                if(modelType==1){
                                    trainProgress = DiskPredict.train(String.valueOf(modelYear),extraParams);
                                }
                                else{
                                    break;
                                }
                            }
                            else{
                                DiskPredictProgress tempProgress = new DiskPredictProgress();
                                int completedTaskCount = 0;
                                boolean isAllFinished = true;
                                for(int i=0;i<trainProgress.size();i++){
                                    completedTaskCount+= trainProgress.get(i).getCompletedTaskCount();
                                    if(!trainProgress.get(i).isFinished()){
                                        isAllFinished = false;
                                    }
                                }
                                tempProgress.setCurrentProgress(completedTaskCount,trainProgress.size()*DiskPredictProgress.trainTotalTaskCount);
                                progressPercentage.set(2,tempProgress.getProgressPercentage());
                                if(isAllFinished){
                                    break;
                                }
                            }
                        }
                        Thread.sleep(500);
                    }
                    for(int i=0;i<trainProgress.size();i++){
                        JSONObject trainResult = DiskPredictProgress.parsingTrainResultData(trainProgress.get(i).getResultData());
                        diskFailureMapper.insertTrainInfo(
                                modelType,
                                trainResult.getString("modelName"),
                                trainResult.getFloat("FDR"),
                                trainResult.getFloat("FAR"),
                                trainResult.getFloat("AUC"),
                                trainResult.getFloat("FNR"),
                                trainResult.getFloat("Accuracy"),
                                trainResult.getFloat("Precision"),
                                trainResult.getFloat("Specificity"),
                                trainResult.getFloat("ErrorRate"),
                                extraParams.toJSONString(),
                                operatorID);
                    }
                    progressPercentage = new ArrayList(Arrays.asList(-1,-1,-1));
                    currentTrainState = 0;
                    isTraining = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("[模型训练]错误！无文件");
                    progressPercentage = new ArrayList(Arrays.asList(-1,-1,-1));
                    currentTrainState = 0;
                    isTraining = false;
                }
            });
            progressThread.start();
        }
    }

    //1 admin,2 superAdmin
    @Override
    public boolean userAuthoirtyCheck(String user,String password,int checkLevel){
        SystemUser systemUser=userDao.signIn(user,password);
        return (checkLevel <= systemUser.getUserType());
    }
    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }

    @Override
    public String getDFPTrainList(){
        List<TrainInfo> queryResult=diskFailureMapper.selectTrainInfoInPage();
        JSONArray resultArray=new JSONArray();
        for(TrainInfo trainInfo:queryResult){
            JSONObject tempObject=new JSONObject();
            tempObject.put("buildTime",trainInfo.timestamp);
            tempObject.put("model",trainInfo.PredictModel);
            tempObject.put("diskModel",trainInfo.DiskModel);
            tempObject.put("FDR",trainInfo.FDR);
            tempObject.put("FAR",trainInfo.FAR);
            tempObject.put("AUC",trainInfo.AUC);
            tempObject.put("FNR",trainInfo.FNR);
            tempObject.put("Accuracy",trainInfo.Accuracy);
            tempObject.put("Precision",trainInfo.Precision);
            tempObject.put("Specificity",trainInfo.Specificity);
            tempObject.put("ErrorRate",trainInfo.ErrorRate);
            tempObject.put("params", trainInfo.Parameters); // JSON.parse(trainInfo.Parameters)
            tempObject.put("OperatorID", trainInfo.OperatorID);
            resultArray.add(tempObject);
        }
        return resultArray.toJSONString();
    }
    public int getDFPTrainListPageCount(int pageSize){
        int count=diskFailureMapper.queryTrainListCount();
        return (count+pageSize-1)/pageSize;
    }

}
