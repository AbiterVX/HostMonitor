package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.hust.hostmonitor_data_collector.dao.*;
import com.hust.hostmonitor_data_collector.dao.entity.*;
import com.hust.hostmonitor_data_collector.utils.HostMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;


public class Service_Implementation implements Service_Interface, ApplicationRunner {
    @Autowired
    private Dao_record dao_record;
    @Autowired
    private Dao_disk dao_disk;
    private HostMonitor hostMonitor=HostMonitor.getInstance();
    private Thread pThread=null;
    private Thread uThread=null;
    private String[] indexEntry={};
    /**
     * 目前用于内存查询
     */
    private static HashMap<String, String> quickQuery=new HashMap<>();
    @Override
    public void insertNewRecord(String ip, Timestamp timestamp,float receiveBW,float transmitBW,float cpuUsage,float memoryUsage,
                                      float diskUsage,int iNumber,int oNumber,float temp,float energy) {

        dao_record.insertNewRecord(ip,timestamp,receiveBW,transmitBW,cpuUsage,memoryUsage,diskUsage,iNumber,oNumber,temp,energy);
//        if(quickQuery.containsKey(ip)){
//            quickQuery.get(ip).add();
//        }
    }

    @Override
    public void run(ApplicationArguments args) {
        memoryDataLoad();
        startMonitor();
        periodPersistence();
        periodMemoryUpdate();
        try {
            hostMonitor.getNewThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void memoryDataLoad(){
        JSONArray iplist=JSONArray.parseArray(hostMonitor.getHostIpList());
        for(int i=0;i<iplist.size();i++){
            String temp=(String)iplist.get(i);
            quickQuery.put(temp+"-60",getRecentInfoByIp(temp,60,FieldType.ALLFIELDS));
            quickQuery.put(temp+"-1440",getRecentInfoByIp(temp,1440,FieldType.ALLFIELDS));
        }
    }

    @Override
    public String getSingleNewestInfoByIp(String ip) {
        return JSON.toJSONString(newestData(ip));

    }

    @Override
    public String getHostInfoListOutputData() {
        return hostMonitor.getHostInfoListOutputData();
    }
    public String getHostHardwareInfoListOutputData(){
        return hostMonitor.getHostHardWareInfoListOutputData();
    }
    @Override
    public String getHostIpList() {
        return hostMonitor.getHostIpList();
    }

    private void startMonitor() {
        hostMonitor.start();
    }
    public String getHostIp(int index){
        return hostMonitor.getHostIp(index);
    }

    private Map<String,Object> newestData(String ip){
        List<Map<String,Object>> temp=hostMonitor.getOriginalHostInfoListOutputData();
        for(Map<String,Object> iterable:temp){
            if(iterable.get("ip").equals(ip)){
                return iterable;
            }
        }
        return null;
    }
    public List<BWrecord> getBWInTimeRange(String ip,int numberOfDays){
        return dao_record.getBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),new Timestamp(System.currentTimeMillis()),ip);
    }
    public List<IOrecord> getIOInTimeRange(String ip,int numberOfDays){
        return dao_record.getIOWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),new Timestamp(System.currentTimeMillis()),ip);
    }
    @Override
    public String getRecentInfoByIp(String ip, int numberOfMinutes, FieldType fieldType) {
        String result;
        String fieldName=fieldType.value();
        switch(fieldType){
            case CPUUSAGE:
                result=getRecentInfoByIpPostion1(ip,numberOfMinutes,fieldName);
                break;
            case MEMORYUSAGE:
                result=getRecentInfoByIpPosition2(ip,numberOfMinutes,fieldName);
                break;
            case DISKUSAGE:
                result=getRecentInfoByIpPosition3(ip,numberOfMinutes,fieldName);
                break;
            case RECEIVEBANDWIDTH:
                result=getRecentInfoByIpPosition4(ip,numberOfMinutes,fieldName);
                break;
            case TRANSMITBANDWIDTH:
                result=getRecentInfoByIpPosition5(ip,numberOfMinutes,fieldName);
                break;
            case INPUTNUMBER:
                result=getRecentInfoByIpPosition6(ip,numberOfMinutes,fieldName);
                break;
            case OUTPUTNUMBER:
                result=getRecentInfoByIpPosition7(ip,numberOfMinutes,fieldName);
                break;
            case TEMPERATURE:
                result=getRecentInfoByIpPosition8(ip,numberOfMinutes,fieldName);
                break;
            case ENERGY:
                result=getRecentInfoByIpPosition9(ip,numberOfMinutes,fieldName);
                break;
            default:
                result=getRecentInfoByIpPosition10(ip,numberOfMinutes,fieldName);
                break;
        }
        return result;
    }
    private String getRecentInfoByIpPostion1(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<CpuUsageRecord> resultList=dao_record.getCpuUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getCpuUsagef());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition2(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<MemoryUsageRecord> resultList=dao_record.getMemoryUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getMemoryUsagef());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition3(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<DiskUsageRecord> resultList=dao_record.getDiskUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getDiskUsagef());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition4(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<BWrecord3> resultList=dao_record.getReceiveBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getReceiveBWf());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition5(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<BWrecord2> resultList=dao_record.getTransmitBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getTransmitBWf());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition6(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<IOrecord2> resultList=dao_record.getInumberWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getiNumber());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition7(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<IOrecord3> resultList=dao_record.getOnumberWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getoNumber());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition8(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<TemperatureRecord> resultList=dao_record.getTempWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getTempf());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition9(String ip,int numberOfMinutes,String fieldName){
        JSONObject resultObject=new JSONObject();
        List<EnergyRecord> resultList=dao_record.getEnergyWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip);
        JSONArray fieldArray=new JSONArray();
        JSONArray timestampArray=new JSONArray();
        for(int i=0;i<resultList.size();i++){
            fieldArray.add(resultList.get(i).getEnergyf());
            timestampArray.add(resultList.get(i).getTimestamp());
        }
        resultObject.put(fieldName,fieldArray);
        resultObject.put("timestamp",timestampArray);
        JSONArray resultArray=new JSONArray();
        resultArray.add(resultObject);
        return resultArray.toJSONString();
    }
    private String getRecentInfoByIpPosition10(String ip,int numberOfMinutes,String fieldName){
        String result=JSON.toJSONString(dao_record.recordQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                new Timestamp(System.currentTimeMillis()),ip));
        return result;
    }
    public String getFullRecordsByIP(String ip, int numberOfMinutes){
//        int numberOfEntry= dao_record.recordNumberQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
//                new Timestamp(System.currentTimeMillis()),ip);
//        List<Record> tempResult=dao_record.recordQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
//                new Timestamp(System.currentTimeMillis()),ip);
        String targetObject=quickQuery.get(ip+"-"+numberOfMinutes);
        System.out.println(targetObject);
        List<Record> tempResult= JSON.parseArray(targetObject,Record.class);
        int numberOfEntry=tempResult.size();
        Float[] temp=new Float[numberOfEntry];
        Float[] cpuUsage=new Float[numberOfEntry];
        Float[] diskUsage=new Float[numberOfEntry];
        Float[] memoryUsage=new Float[numberOfEntry];
        Float[] receiveBW=new Float[numberOfEntry];
        Float[] transmitBW=new Float[numberOfEntry];
        Timestamp[] timestamp=new Timestamp[numberOfEntry];
        int i=0;
        for(Record record:tempResult){
            temp[i]=record.getTempf();
            cpuUsage[i]=record.getCpuUsagef();
            diskUsage[i]=record.getDiskUsagef();
            memoryUsage[i]=record.getMemoryUsagef();
            receiveBW[i]=record.getReceiveBWf();
            transmitBW[i]=record.getTransmitBWf();
            timestamp[i]=record.getTimestamp();
            i++;
        }
        JSONObject result=new JSONObject();
        result.put("temp",temp);
        result.put("cpuUsage",cpuUsage);
        result.put("diskUsage",diskUsage);
        result.put("memoryUsage",memoryUsage);
        result.put("receiveBW",receiveBW);
        result.put("transmitBW",transmitBW);
        result.put("timestamp",timestamp);
        return result.toJSONString();
    }

    public String getDiskFailureList(String ip) {
        List<String> timestampList=dao_disk.getDiskFailureTimestamp(ip);
        String resultDate=findLatest(timestampList);
        List<String> diskFailureInfo=dao_disk.getDiskFailureInfo(ip,resultDate);
        StringBuffer temp=new StringBuffer("");
        for(String string:diskFailureInfo){
            temp.append(string);
            temp.append("\n");
        }
        JSONArray result=new JSONArray();
        JSONObject jObject=new JSONObject();
        jObject.put("date",resultDate);
        jObject.put("type","未知");
        jObject.put("information",temp.toString());
        result.add(jObject);
        return result.toJSONString();
    }

    private String findLatest(List<String> timestampList) {
        ArrayList<DateParser> list=new ArrayList<>();
        for(String string:timestampList){
            list.add(new DateParser(string));
        }
        Collections.sort(list);
        return list.get(list.size()-1).getOriginalFormat();

    }

    public String getAllDeviceUsage(int numberOfMinutes) {
        JSONArray jsonArray=JSON.parseArray(getHostIpList());
        JSONArray result=new JSONArray();
        for(Object object:jsonArray.stream().toArray()){
            JSONObject tempObject=new JSONObject();
            tempObject.put("ip",(String)object);
            int numberOfEntry= dao_record.recordNumberQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                    new Timestamp(System.currentTimeMillis()),(String)object);
            List<Record> tempResult=dao_record.recordQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfMinutes*60000),
                    new Timestamp(System.currentTimeMillis()),(String)object);
            Float[] temp=new Float[numberOfEntry];
            Float[] cpuUsage=new Float[numberOfEntry];
            Float[] diskUsage=new Float[numberOfEntry];
            Float[] memoryUsage=new Float[numberOfEntry];
            Float[] receiveBW=new Float[numberOfEntry];
            Float[] transmitBW=new Float[numberOfEntry];
            Timestamp[] timestamp=new Timestamp[numberOfEntry];
            int i=0;
            for(Record record:tempResult){
                temp[i]=record.getTempf();
                cpuUsage[i]=record.getCpuUsagef();
                diskUsage[i]=record.getDiskUsagef();
                memoryUsage[i]=record.getMemoryUsagef();
                receiveBW[i]=record.getReceiveBWf();
                transmitBW[i]=record.getTransmitBWf();
                timestamp[i]=record.getTimestamp();
                i++;
            }
            tempObject.put("temp",temp);
            tempObject.put("cpuUsage",cpuUsage);
            tempObject.put("diskUsage",diskUsage);
            tempObject.put("memoryUsage",memoryUsage);
            tempObject.put("receiveBW",receiveBW);
            tempObject.put("transmitBW",transmitBW);
            tempObject.put("timestamp",timestamp);
            result.add(tempObject);
        }
        return result.toJSONString();
    }

    //获取当前Host状态
    public String getHostState(){
        return hostMonitor.getHostState();
    }
    private class PersistenceThread implements Runnable{

        public int interval_ms;
        public int time_offset=5000;
        public PersistenceThread(){
            interval_ms=hostMonitor.interval_ms;
        }
        @Override
        public void run() {
            int count=0;
            try {
                Thread.sleep(5*interval_ms+time_offset);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(hostMonitor.threadStart) {
                //while (hostMonitor.isDataHasBeenWritten()) ;
//                count++;
//                if (count % 12 == 11) {
                List<Map<String, Object>> listForWritten = hostMonitor.getOriginalHostInfoListOutputData();
                List<Boolean> stateList=hostMonitor.getHostStateList();
                for (int i=0;i<listForWritten.size();i++){
                    Map<String, Object> iterable=listForWritten.get(i);
                    float receiveBW,transmitBW,cpuUsage,memoryUsage,diskUsage;
                    receiveBW=(float) iterable.get("receiveBW");
                    transmitBW=(float) iterable.get("transmitBW");
                    cpuUsage=(float) iterable.get("cpuUsage");
                    memoryUsage=(float)iterable.get("memoryUsage");
                    diskUsage=(float) iterable.get("diskUsage");
                    System.out.println(""+receiveBW+" "+transmitBW+" "+cpuUsage+" "+memoryUsage+" "+diskUsage);
                    if(!(receiveBW==0.0f&&transmitBW==0.0f&&cpuUsage==0.0f&&memoryUsage==0.0f&&diskUsage==0.0f)) {
                        if(stateList.get(i)){
                        insertNewRecord((String) iterable.get("ip"), (Timestamp) iterable.get("timestamp"), receiveBW, transmitBW, cpuUsage, memoryUsage,
                                diskUsage, 250, 250, 36.0f, 600.0f);}
                        else{
                            System.out.println((String) iterable.get("ip")+" is down,the data is meaningless.");
                        }
                    }
                    else{
                        System.out.println("All the data is 0.0f,which would happen when the first time to sample data after the system setup");
                    }
                }
//                    count=0;
//                }
                //等待
                //hostMonitor.setDataHasBeenWritten(true);
                try {
                    Thread.sleep(12*interval_ms+time_offset);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void periodPersistence() {
        if(pThread==null) {
            pThread = new Thread(new PersistenceThread(), "Thread to persist");
            pThread.start();

        }
    }
    private class MemoryUpdate implements Runnable{
        private int interval_ms;
        private int time_offset=3000;
        public MemoryUpdate(){
            this.interval_ms=hostMonitor.interval_ms;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(12*interval_ms+time_offset);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONArray iplist=JSONArray.parseArray(hostMonitor.getHostIpList());
            while(hostMonitor.threadStart) {
                for(int i=0;i<iplist.size();i++){
                    String temp=(String)iplist.get(i);
                    quickQuery.put(temp+"-60",getRecentInfoByIp(temp,60,FieldType.ALLFIELDS));
                    quickQuery.put(temp+"-1440",getRecentInfoByIp(temp,1440,FieldType.ALLFIELDS));
                }
                try {
                    Thread.sleep(15*interval_ms+time_offset);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void periodMemoryUpdate(){
        if(uThread==null){
            uThread = new Thread(new MemoryUpdate(),"Thread to update memory");
            uThread.start();
        }
    }
}
