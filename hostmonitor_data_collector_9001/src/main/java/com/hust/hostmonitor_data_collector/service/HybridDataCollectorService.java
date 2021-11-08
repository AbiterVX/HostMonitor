package com.hust.hostmonitor_data_collector.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import com.hust.hostmonitor_data_collector.dao.DispersedMapper;

import com.hust.hostmonitor_data_collector.dao.UserDao;
import com.hust.hostmonitor_data_collector.dao.entity.*;
import com.hust.hostmonitor_data_collector.utils.DataSampleManager;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredictProgress;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.QueryResources;
import com.hust.hostmonitor_data_collector.utils.SocketConnect.DataReceiver;
import com.hust.hostmonitor_data_collector.utils.TestInitiator;
import com.hust.hostmonitor_data_collector.utils.linuxsample.LinuxDataProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import com.hust.hostmonitor_data_collector.utils.DataSampleManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HybridDataCollectorService implements DataCollectorService{

    //自动注入类
    @Autowired
    UserDao userDao;
    @Autowired
    DiskFailureMapper diskFailureMapper;
    @Autowired
    DispersedMapper dispersedMapper;


    //日志输出
    Logger logger= LoggerFactory.getLogger(HybridDataCollectorService.class);
    //数据采样管理
    private DataSampleManager dataSampleManager = DataSampleManager.getInstance();
    //配置数据管理
    private ConfigDataManager configDataManager= ConfigDataManager.getInstance();
    //采样方式选择
    private int sampleSelect=configDataManager.getSampleMethod();
    //cmd命令执行
    DataSampleManager cmdSampleManager=DataSampleManager.getInstance();
    private String dataPath;
    //格式资源变量
    private final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

    //Sokect数据接收
    private DataReceiver dataReceiver;
    //----- 监控节点
    //SSH 连接的节点配置数据List
    private List<HostConfigData> sshHostList;
    //Socket 连接的节点List
    //List<> socketHostList;
    //线程池(用于SSH节点的定时采样)
    private ExecutorService executorService;

    //----- 定时作业
    //定时器
    private Timer mainTimer = new Timer();
    //定时器周期参数
    private final long sampleInterval=10000;
    private final int sampleStoreDelayMS=500;
    private final int offset=1000;
    private final long predictInterval=24*3600*1000;
    //定时任务(Host性能采样)
    private TimerTask performanceSampleTask= new TimerTask() {
        @Override
        public void run() {
            CountDownLatch latch=new CountDownLatch(dataSampleManager.hostList.size());
            for(HostConfigData hostConfigData:dataSampleManager.hostList){
                if(sshSampleData.containsKey(hostConfigData.ip)){
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            dataSampleManager.sampleHostData(hostConfigData,sshSampleData.get(hostConfigData.ip));
                            latch.countDown();
                        }
                    });

                }
                else{
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject initObject=dataSampleManager.sampleHostHardwareData(hostConfigData);
                            sshSampleData.put(hostConfigData.ip,initObject);
                            dataSampleManager.sampleHostData(hostConfigData,initObject);
                            latch.countDown();
                        }
                    });

                }
               logger.info("[DSManager]"+hostConfigData.ip+" performance sample");
            }
            try {
                latch.await();
                logger.info("[DSManager] performance sample finish");
                storeSampleData();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("[DSManager] performance sample latch error");
            }

        }
    };
    //定时任务(Host进程采样)
    private TimerTask processSampleTask=new TimerTask() {
        @Override
        public void run() {
            CountDownLatch latch=new CountDownLatch(dataSampleManager.hostList.size());
            for(HostConfigData hostConfigData:dataSampleManager.hostList){
                if(sshSampleData.containsKey(hostConfigData.ip)){
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            dataSampleManager.sampleHostProcess(hostConfigData,sshSampleData.get(hostConfigData.ip));
                            latch.countDown();
                        }
                    });

                }
                else{
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject initObject=dataSampleManager.sampleHostHardwareData (hostConfigData) ;
                            sshSampleData.put(hostConfigData.ip,initObject);
                            dataSampleManager.sampleHostProcess(hostConfigData,initObject);
                            latch.countDown();
                        }
                    });
                }
                logger.info("[DSManager]"+hostConfigData.ip+" process sample");
            }
            try {
                latch.await();
                logger.info("[DSManager] process sample finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("[DSManager] process sample latch error");
            }
        }
    };
    //定时器采样任务
    private final TimerTask diskPredictTask= new TimerTask() {
        @Override
        public void run() {
            logger.info("[DiskPredict]"+new Date());
            diskPredict();
        }
    };
    //socket采样持久化任务
    private final TimerTask dataPersistenceTask = new TimerTask() {
        @Override
        public void run() {
            logger.info("[DataPersistance]"+new Date());
            try {
                Thread.sleep(sampleStoreDelayMS);
                storeSampleData();
            } catch (InterruptedException e) {
                logger.error("[ThreadSleepError]: In TimerTask dataPersistenceTask");
            }
        }
    };
    //实际数据库持久化操作
    private void storeSampleData(){
        logger.info("[Persistence]DataHostsSize "+hostsSampleData.size());
        for(Map.Entry<String, JSONObject> entry: hostsSampleData.entrySet()){
            JSONObject tempObject=entry.getValue();
            if(!tempObject.getBoolean("hasPersistent")){  //TODO
                double memUsage;
                if(tempObject.getJSONArray("memoryUsage").getDouble(1)==0){
                    memUsage=0;
                }
                else{
                    memUsage=tempObject.getJSONArray("memoryUsage").getDouble(0)/tempObject.getJSONArray("memoryUsage").getDouble(1);
                }

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
                JSONArray diskArray=tempObject.getJSONArray("diskInfoList");
                for(int i=0;i<diskArray.size();i++){
                    JSONObject tempDiskObject=diskArray.getJSONObject(i);
                    String diskSerial=diskFailureMapper.queryDiskHardwareExists(tempDiskObject.getString("diskName"));
                    if(diskSerial==null){
                        diskSerial=tempDiskObject.getString("diskName");
                        diskFailureMapper.insertDiskHardwareInfo(diskSerial,tempObject.getString("hostName"),
                                tempDiskObject.getDoubleValue("diskTotalSize"),
                                tempDiskObject.getIntValue("type")>0? true:false,
                                tempDiskObject.getString("diskModel"),
                                tempObject.getString("ip"));
                    }
                    //System.out.println("["+diskSerial+"]"+tempObject.getTimestamp("lastUpdateTime"));
                    diskFailureMapper.insertDiskSampleInfo(diskSerial, tempObject.getTimestamp("lastUpdateTime"),tempDiskObject.getDoubleValue("diskIOPS"),
                            tempDiskObject.getDoubleValue("diskReadSpeed"),tempDiskObject.getDoubleValue("diskWriteSpeed"));
                }
            }
        }
    }



    //----- 数据 -----
    //总体统计数据
    private JSONObject summaryInfo;
    public float[][] loadPartition;
    //IO测试数据
    public Map<String, JSONObject> ioTestInfoList = new HashMap<>();
    //采样数据
    private HashMap<String,JSONObject> sshSampleData = null;
    private HashMap<String,JSONObject> socketSampleData=null;
    private HashMap<String,JSONObject> hostsSampleData=null;

    @Override
    public HashMap<String, JSONObject> getSocketMap() {
        return socketSampleData;
    }

    //----- 内部函数 -----
    //构造函数
    public HybridDataCollectorService(){
        sshHostList = dataSampleManager.hostList;
        //线程池大小设为Host个数*2
        executorService= Executors.newFixedThreadPool(sshHostList.size()*2);
        dataPath=System.getProperty("user.dir")+"/DiskPredict/";
        logger.info("Check select in Config.json [1]SSH Commands [2]OSHI/选择采样模式:[1]SSH远程执行指令[2]OSHI");
        logger.info("Default:SSH Commands/默认使用SSH远程执行指令");
        if(sampleSelect==1){
            sshSampleData=new HashMap<>();
            hostsSampleData=sshSampleData;
            for(HostConfigData hostConfigData:sshHostList){

                JSONObject initObject=dataSampleManager.sampleHostHardwareData(hostConfigData);

                sshSampleData.put(hostConfigData.ip,initObject);
            }
            mainTimer.schedule(performanceSampleTask,7*1000,sampleInterval);
            mainTimer.schedule(processSampleTask,13*1000,sampleInterval);

        }
        else if(sampleSelect==2){

            socketSampleData=new HashMap<>();
            hostsSampleData=sshSampleData;
            dataReceiver=new DataReceiver(this);
            dataReceiver.startListening();
            mainTimer.schedule(dataPersistenceTask,sampleInterval/2,sampleInterval-offset);
        }

        summaryInfo=configDataManager.getSummaryFormat();
        loadPartition=configDataManager.getLoadPartitionFormat();
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date date=calendar.getTime();
        if(date.before(new Date())){
            date=addDay(date,1);
        }
        mainTimer.schedule(diskPredictTask,date,predictInterval);
    }
    private Date addDay(Date date,int num){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,num);
        return  calendar.getTime();
    }




    //-----外部服务接口-----

    @Override
    public String getDashboardSummary() {
        UpdateSummaryInfo();
        return summaryInfo.toJSONString();
    }
    public void UpdateSummaryInfo(){
        double totalSumCapacity=0;
        int windowsCount=0,linuxCount=0,HDDCount=0,SSDCount=0,connectedCount=0;
        float[][] loadCount = new float[][]{{0,0,0},{0,0,0},{0,0,0}};
        JSONArray hostIp=new JSONArray();
        for(Map.Entry<String,JSONObject> hostInfo: sshSampleData.entrySet()){
            JSONObject hostInfoJson = hostInfo.getValue();
            hostIp.add(hostInfo.getKey());
            totalSumCapacity+=hostInfoJson.getDoubleValue("diskTotalSize");
            if(hostInfoJson.getString("osName").toLowerCase().contains(("windows").toLowerCase())){
                windowsCount++;
            }
            else{
                linuxCount++;
            }
            if(hostInfoJson.getBoolean("connected")){
                connectedCount++;
            }
            //HDD SSD统计
            for(int i=0;i<hostInfoJson.getJSONArray("diskInfoList").size();i++){
                if(hostInfoJson.getJSONArray("diskInfoList").getJSONObject(i).getIntValue("type")==1){
                    SSDCount++;
                }
                else{
                    HDDCount++;
                }
            }


            //-----负载统计
            //cpu负载统计
            JSONArray cpuInfoList = hostInfoJson.getJSONArray("cpuInfoList");
            for(int i=0;i<cpuInfoList.size();i++){
                float cpuUsage = cpuInfoList.getJSONObject(i).getFloat("cpuUsage");
                for(int j=0;j<loadPartition[0].length;j++){
                    if(cpuUsage <= loadPartition[0][j]){
                        loadCount[0][j] += 1;
                        break;
                    }
                }
            }
            //内存负载统计
            JSONArray memoryUsageJson =  hostInfoJson.getJSONArray("memoryUsage");
            float memoryUsage;
            if(memoryUsageJson.getFloat(1)==0){
                memoryUsage=0;
            }
            else {
                memoryUsage = (memoryUsageJson.getFloat(0) / memoryUsageJson.getFloat(1))*100;
            }
            for(int j=0;j<loadPartition[1].length;j++){
                if(memoryUsage <= loadPartition[1][j]){
                    loadCount[1][j] += 1;
                    break;
                }
            }
            //硬盘负载统计
            JSONArray diskInfoList = hostInfoJson.getJSONArray("diskInfoList");
            for(int i=0;i<diskInfoList.size();i++){
                double singleTotal= diskInfoList.getJSONObject(i).getDoubleValue("diskTotalSize");
                double singleFree=diskInfoList.getJSONObject(i).getDoubleValue("diskTotalFreeSize");
                double diskUsage = (singleTotal-singleFree) / singleTotal*100;
                for(int j=0;j<loadPartition[2].length;j++){
                    if(diskUsage <= loadPartition[2][j]){
                        loadCount[2][j] += 1;
                        break;
                    }
                }
            }

        }

        summaryInfo.put("hostIp",hostIp);
        summaryInfo.put("sumCapacity",totalSumCapacity);
        summaryInfo.put("windowsHostCount",windowsCount);
        summaryInfo.put("linuxHostCount",linuxCount);
        summaryInfo.put("hddCount",HDDCount);
        summaryInfo.put("ssdCount",SSDCount);
        summaryInfo.put("connectedCount",connectedCount);
        summaryInfo.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));


        JSONArray load = summaryInfo.getJSONArray("load");
        for(int i=0;i<load.size();i++){
            JSONArray currentLoad = load.getJSONArray(i);
            for(int j=0;j<currentLoad.size();j++){
                currentLoad.set(j,loadCount[i][j]);
            }
        }


    }
    @Override
    public String getHostInfoDashboardAll() {
        JSONObject resultObject=new JSONObject();
        for(Map.Entry<String, JSONObject> entry: hostsSampleData.entrySet()){
            resultObject.put(entry.getKey(),entry.getValue());
            double iops=0,diskWriteSpeed=0,diskReadSpeed=0;
            JSONArray diskArray=entry.getValue().getJSONArray("diskInfoList");
            for(int i=0;i<diskArray.size();i++){
                iops+=diskArray.getJSONObject(i).getDouble("diskIOPS");
                diskWriteSpeed+=diskArray.getJSONObject(i).getDouble("diskWriteSpeed");
                diskReadSpeed+=diskArray.getJSONObject(i).getDouble("diskReadSpeed");
            }
            resultObject.getJSONObject(entry.getKey()).put("diskTotalIOPS",iops);
            resultObject.getJSONObject(entry.getKey()).put("diskTotalWriteSpeed",diskWriteSpeed);
            resultObject.getJSONObject(entry.getKey()).put("diskTotalReadSpeed",diskReadSpeed);
        }
        return resultObject.toJSONString();
    }

    @Override
    public String getHostInfoDetail(String IP) {
        String result=hostsSampleData.get(IP).toJSONString();
        return result;
    }

    @Override
    public String getDiskInfoAll() {
        return null;
    }

    @Override
    public String getDiskInfo(String hostName) {
        return null;
    }

    @Override
    public String getHostInfoDetailTrend(String Ip) {
        int hours=24;
        Timestamp highbound=new Timestamp(System.currentTimeMillis());
        Timestamp lowbound=new Timestamp(System.currentTimeMillis()-hours*3600*1000);
        List<DispersedRecord> dispersedRecordList= dispersedMapper.queryRecordsWithTimeLimit(lowbound,highbound,Ip);

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
    @Override
    public String getDFPInfoTrend(String hostIp, String diskSerial) {
        JSONArray result=new JSONArray();
        List<DFPRecord> dfpRecordList=diskFailureMapper.selectDFPRecords(diskSerial);
        for(DFPRecord dfpRecord:dfpRecordList){
            result.add(createNewValue(dfpRecord.timestamp,dfpRecord.predictProbability));
        }
        return result.toJSONString();
    }

    @Override
    public String getDFPInfoAll() {
        JSONArray result=new JSONArray();
        List<HardWithDFPRecord> hardWithDFPRecordList=diskFailureMapper.selectLatestDFPWithHardwareRecordList();
        for(HardWithDFPRecord dfpRecord:hardWithDFPRecordList){
            JSONObject tempObject=new JSONObject();
            tempObject.put("hostName",dfpRecord.hostName);
            tempObject.put("ip",dfpRecord.hostIp);
            tempObject.put("diskSerial",dfpRecord.diskSerial);
            tempObject.put("diskType",dfpRecord.isSSd?1:0);
            tempObject.put("manufacturer",dfpRecord.model);
            tempObject.put("diskCapacity",dfpRecord.size);
            tempObject.put("model",dfpRecord.modelName);
            tempObject.put("timestamp",dfpRecord.timestamp);
            tempObject.put("predictProbability",dfpRecord.predictProbability);
            tempObject.put("predictResult",dfpRecord.predictProbability<=0.1f?1:0);
            result.add(tempObject);
        }
        return result.toJSONString();
    }
    //-----模型训练进度条
    private boolean isTraining = false;
    private int currentTrainState = 0;
    private DiskPredictProgress preprocessProgress;
    private DiskPredictProgress getTrainDataProgress;
    private List<DiskPredictProgress> trainProgress;
    List<Float> progressPercentage = new ArrayList(Arrays.asList(-1,-1,-1));
    private boolean doSpecPredict=false;
    //获取模型训练进度
    @Override
    public List<Float> getTrainProgress(){
        return progressPercentage;
    }

    @Override
    public void train(int modelType, float positiveDataProportion, float negativeDataProportion, float verifyProportion, JSONObject extraParams, String operatorID) {
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
                    int modelYear= 2016;
                    int latestYear=Calendar.getInstance().get(Calendar.YEAR);
                    File originalData=new File(dataPath+"original_data/"+latestYear);
                    File modelConfig=new File(dataPath+"models");
                    //TODO 检查数据量
                    if(originalData.exists()&&modelConfig.exists()&&false){
                        modelYear=latestYear;
                    }
                    if(modelYear==2016){
                        doSpecPredict=true;
                    }
                    //
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
                    if(doSpecPredict){
                        System.out.println("Special disk predict task");
                        diskPredict();
                    }
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
    //有待实现对预测范围的选择
    private void diskPredict(){
        Timestamp predictTime=new Timestamp(System.currentTimeMillis());
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String date=sdf.format(calendar.getTime());
        File file=new File(System.getProperty("user.dir")+"/DiskPredict/original_data/"+calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+date+".csv");
        if(file.exists()) {
            DiskPredict.predictWithoutProgess(System.getProperty("user.dir") + "/DiskPredict/original_data/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1), date + ".csv");
        }else {
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date=sdf.format(calendar.getTime());
            file=new File(System.getProperty("user.dir")+"/DiskPredict/original_data/"+calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+date+".csv");
            if(file.exists()){
                DiskPredict.predictWithoutProgess(System.getProperty("user.dir") + "/DiskPredict/original_data/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1), date + ".csv");
            }
            else {
                System.err.println("Today and yesterday neither have the updated data,the diskPredict() will do nothing.");
                return;
            }
        }
        //在路径下读出所有的预测结果
        List<JSONObject> result=DiskPredict.getDiskPredictResult("/DiskPredict/result/"+sdf.format(new Date())+"/"+date+".csv");
        // 插入数据库，注意修改接受文件时同时修改下列状态
        for(JSONObject jsonObject:result) {
            System.out.println(jsonObject);
            if (diskFailureMapper.checkRecordExists(jsonObject.getString("diskSerial"), jsonObject.getTimestamp("timestamp")) == 0) {

                diskFailureMapper.insertDiskDFPInfo(jsonObject.getString("diskSerial"), jsonObject.getTimestamp("timestamp"), LinuxDataProcess.doubleTo2bits_double(jsonObject.getDoubleValue("predictProbability") * 100), jsonObject.getString("modelName"),predictTime);

            }
            else{
                //TODO 更新
            }

        }
    }
    @Override
    public boolean userAuthoirtyCheck(String user, String password, int checkLevel) {
        SystemUser systemUser=userDao.signIn(user,password);
        return (checkLevel <= systemUser.getUserType());
    }

    @Override
    public String getSpeedMeasurementInfoAll() {
        return null;
    }

    @Override
    public String getDFPTrainList() {
        List<TrainInfo> queryResult=diskFailureMapper.selectAllTrainInfo();
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

    //磁盘故障预测统计页面阈值参数
    private final double mediumLow=0.20f;
    private final double mediumHigh=0.70f;
    @Override
    public String getDFPSummary() {
        JSONObject result=new JSONObject();

        //左侧表格,取出最新的模型的性能数据
        JSONArray comparison=new JSONArray();
        StatisRecord statisRecord=diskFailureMapper.selectLatestTrainingSummary();

        JSONObject tempObject;
        if(statisRecord==null){
            comparison.add(new JSONObject());
            comparison.add(new JSONObject());
            result.put("comparison",comparison);
        }
        else {
            tempObject = new JSONObject();
            tempObject.put("field", "predict");
            tempObject.put("FDR", statisRecord.FDR);
            tempObject.put("FAR", statisRecord.FAR);
            tempObject.put("AUC", statisRecord.AUC);
            tempObject.put("FNR", statisRecord.FNR);
            tempObject.put("Accuracy", statisRecord.Accuracy);
            tempObject.put("Precision", statisRecord.Accuracy);
            tempObject.put("Specificity", statisRecord.Accuracy);
            tempObject.put("ErrorRate", statisRecord.Accuracy);
            comparison.add(tempObject);

            tempObject = new JSONObject();
            tempObject.put("field", "reality");
            tempObject.put("FDR", 0.6);
            tempObject.put("FAR", 0.6);
            tempObject.put("AUC", 0.6);
            tempObject.put("FNR", 0.6);
            tempObject.put("Accuracy", 0.6);
            tempObject.put("Precision", 0.6);
            tempObject.put("Specificity", 0.6);
            tempObject.put("ErrorRate", 0.6);
            comparison.add(tempObject);

            result.put("dfpComparison", comparison);
        }
        //仪表盘
        Timestamp timestamp=diskFailureMapper.selectLatestRecordTime();
        if(timestamp==null){
            System.err.println("[Database]There is no dfp records in mysql");
            JSONArray SummaryChart=new JSONArray();
            SummaryChart.add(0);
            SummaryChart.add(0);
            SummaryChart.add(0);
            result.put("SummaryChart",SummaryChart);
            result.put("diskType",new JSONArray());
            result.put("ssdCount",new JSONArray());
            result.put("hddCount",new JSONArray());
            result.put("trend",new JSONArray());
            return result.toJSONString();
        }
        Calendar calendar=Calendar.getInstance();

        calendar.setTimeInMillis(timestamp.getTime());
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        Timestamp lowbound=new Timestamp(calendar.getTimeInMillis());

        List<DFPRecord> queryList=diskFailureMapper.selectDFPRecordsByLowbound(lowbound);
        int lowCount=0,mediumCount=0,highCount=0;
        for(DFPRecord dfpRecord:queryList){
            if(dfpRecord.predictProbability<=mediumLow){
                highCount++;
            }
            else if(dfpRecord.predictProbability>mediumHigh){
                lowCount++;
            }
            else {
                mediumCount++;
            }
        }
//        double lowProportion,mediumProportion,highProportion;
//        int totalCount=lowCount+mediumCount+highCount;
//        lowProportion=doubleTo2bits_double(lowCount*1.0/totalCount);
//        mediumProportion=doubleTo2bits_double(mediumCount*1.0/totalCount);
//        highProportion=doubleTo2bits_double(1-lowProportion-mediumProportion);
//        JSONArray diskProbabilityStatistic=new JSONArray();
        JSONArray SummaryChart=new JSONArray();
        SummaryChart.add(lowCount);
        SummaryChart.add(mediumCount);
        SummaryChart.add(highCount);
        result.put("SummaryChart",SummaryChart);
//        result.put("diskProbabilityStatistic",diskProbabilityStatistic);
//        result.getJSONArray("diskProbabilityStatistic").add(lowProportion);
//        result.getJSONArray("diskProbabilityStatistic").add(mediumProportion);
//        result.getJSONArray("diskProbabilityStatistic").add(highProportion);
        //条状图，先用预测的结果替代
//        timestamp=diskFailureMapper.selectLatestFailureTime();
//        calendar.setTimeInMillis(timestamp.getTime());
//        calendar.set(Calendar.MINUTE,0);
//        calendar.set(Calendar.SECOND,0);
//        calendar.set(Calendar.HOUR_OF_DAY,0);
//        lowbound=new Timestamp(calendar.getTimeInMillis());
//        List<RealDiskFailure> realDiskFailureList=diskFailureMapper.selectRecentRecords(lowbound);
//        for(RealDiskFailure diskFailure:realDiskFailureList){
//
//
//
//        }
        //TODO 临时版本 ,暂定四个厂商
        List<HardWithDFPRecord> RecordList=diskFailureMapper.selectRecentDFPWithHardwareRecordList(lowbound);
//        JSONObject rightChart=new JSONObject();
//        rightChart.put("西部数据",new JSONArray());
//        rightChart.put("希捷",new JSONArray());
//        rightChart.put("东芝",new JSONArray());
//        rightChart.put("三星",new JSONArray());
//        rightChart.put("其他",new JSONArray());
        JSONArray brands=new JSONArray();
        brands.add("西部数据");
        brands.add("希捷");
        brands.add("东芝");
        brands.add("三星");
        brands.add("其他");
        int[] count=new int[10];
        for(int i=0;i<10;i++){
            count[i]=0;
        }
        for(HardWithDFPRecord hardWithDFPRecord:RecordList){
            int index;
            if(hardWithDFPRecord.predictProbability<=mediumLow){
                index= QueryResources.queryDiskIndex(hardWithDFPRecord.diskSerial);
                index*=2;
                if(!hardWithDFPRecord.isSSd){
                    index+=1;
                }
                count[index]++;
            }
        }
//        rightChart.getJSONArray("西部数据").add(count[0]);
//        rightChart.getJSONArray("西部数据").add(count[1]);
//        rightChart.getJSONArray("希捷").add(count[2]);
//        rightChart.getJSONArray("希捷").add(count[3]);
//        rightChart.getJSONArray("东芝").add(count[4]);
//        rightChart.getJSONArray("东芝").add(count[5]);
//        rightChart.getJSONArray("三星").add(count[6]);
//        rightChart.getJSONArray("三星").add(count[7]);
//        rightChart.getJSONArray("其他").add(count[8]);
//        rightChart.getJSONArray("其他").add(count[9]);
//        result.put("rightChart",rightChart);
        JSONArray hddCount=new JSONArray();
        JSONArray ssdCount=new JSONArray();
        Iterator<Object> itr=brands.iterator();
        int m=0;
        while(itr.hasNext()){

            if(count[2*m]==0&&count[2*m+1]==0){

                itr.next();
                itr.remove();
            }
            else{

                itr.next();
                ssdCount.add(count[2*m]);
                hddCount.add(count[2*m+1]);
            }
            m++;
        }
        result.put("diskType",brands);
        result.put("ssdCount",ssdCount);
        result.put("hddCount",hddCount);


        //错误盘数趋势图,统计的是两周的，每天的磁盘损坏数量
        JSONArray Trend=new JSONArray();
        calendar.add(Calendar.DAY_OF_MONTH,-13);

        lowbound=new Timestamp(calendar.getTimeInMillis());
        RecordList=diskFailureMapper.selectRecentDFPWithHardwareRecordList(lowbound);
        int[] twoWeeks=new int[14];
        for(int i=0;i<14;i++){
            twoWeeks[i]=0;
        }
        int i=0;
        calendar.add(Calendar.DAY_OF_MONTH,1);
        long highbound=calendar.getTimeInMillis();
        for(HardWithDFPRecord hardWithDFPRecord:RecordList){
            if(hardWithDFPRecord.timestamp.getTime()>highbound){
                i++;
                calendar.add(Calendar.DAY_OF_MONTH,1);
                highbound=calendar.getTimeInMillis();
            }
            if(hardWithDFPRecord.predictProbability<=mediumLow){
                twoWeeks[i]++;
            }

        }
        calendar.setTimeInMillis(lowbound.getTime());
        for(int j=0;j<14;j++){
            JSONArray tempJSONArray=new JSONArray();
            tempJSONArray.add(new Timestamp(calendar.getTimeInMillis()));
            tempJSONArray.add(twoWeeks[j]);
            Trend.add(tempJSONArray);
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        result.put("trend",Trend);
        return result.toJSONString();
    }
    @Override
    public String remoteTest(String nodeIp){

        //客户端版本
        if(sampleSelect==2) {
            TestInitiator testInitiator = new TestInitiator(nodeIp);
            testInitiator.socketInitialization();
            String result = testInitiator.executeTest(1);
            testInitiator.closeTestSocket();
            return result;
        }
        else if(sampleSelect==1){
            HostConfigData testConfig=null;
            for(HostConfigData hostConfigData:sshHostList){
                if(hostConfigData.ip.equals(nodeIp)){
                    testConfig=hostConfigData;
                    break;
                }
            }
            if(testConfig==null){
                return "Node not found";
            }
            JSONObject result=dataSampleManager.ioTest(testConfig);
            //JSONObject result = cmdSampleManager.ioTest(nodeIp);
            return result.toJSONString();
        }
        return "sampleSelect Error";
    }

    public String providerTest(String userName, String password, Timestamp timestamp){
        return userDao.signUp(userName,password,timestamp);
    }

    @Override
    public void setAllDiskDFPState(String hostIp, boolean b) {
        if(socketSampleData.containsKey(hostIp)){
            JSONObject jsonObject=socketSampleData.get(hostIp);
            JSONArray diskArray=jsonObject.getJSONArray("diskInfoList");
            for(int i=0;i<diskArray.size();i++) {
                String diskName=diskArray.getJSONObject(i).getString("diskName");
                jsonObject.getJSONObject("DFPList").put(diskName, b);
            }
        }
    }

    //返回所有节点的实时信息
    @Override
    public String getAllHostsInfoDetail() {
        JSONObject result = new JSONObject();
        Set<String> ipSet = hostsSampleData.keySet();
        for(String currentIp :ipSet){
            JSONObject currentSampleData = hostsSampleData.get(currentIp);
            JSONObject hostResultData = new JSONObject();
            {
                hostResultData.put("cpuUsage",currentSampleData.getDouble("cpuUsage"));
                hostResultData.put("memoryUsage",currentSampleData.getJSONArray("memoryUsage"));
                hostResultData.put("allDiskTotalFreeSize",currentSampleData.getDouble("allDiskTotalFreeSize"));
                hostResultData.put("allDiskTotalSize",currentSampleData.getDouble("allDiskTotalSize"));
                hostResultData.put("netSendSpeed",currentSampleData.getDouble("netSendSpeed"));
                hostResultData.put("netReceiveSpeed",currentSampleData.getDouble("netReceiveSpeed"));
                hostResultData.put("diskReadBytes",currentSampleData.getDouble("diskReadBytes"));
                hostResultData.put("diskWriteBytes",currentSampleData.getDouble("diskWriteBytes"));
            }
            result.put(currentIp,hostResultData);
        }
        return result.toJSONString();
    }

    //返回所有hostList里面的信息（无密码和账号）
    @Override
    public String getHostsRouterInfo() {

        JSONArray result = new JSONArray();
        List<HostConfigData> hostList = configDataManager.getSSHConfigHostList();
        for(HostConfigData hostConfigData:hostList){
            JSONObject currentHost = new JSONObject();
            {
                currentHost.put("ip",hostConfigData.ip);
                currentHost.put("router",hostConfigData.router);
            }
            result.add(currentHost);
        }
        return result.toJSONString();
    }
}
