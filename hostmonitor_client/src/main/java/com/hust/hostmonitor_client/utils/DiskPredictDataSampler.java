package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiskPredictDataSampler extends Thread {
    private String sampleFilePath = "";
    private String dataFilePath="";
    private final long sampleInterval=24*3600*1000;
    private final String osType;
    //private final long sampleInterval=10*1000;
    private Socket fileSocket;
    private String collectorIp="";
    private int collectorPort=7000;
    private FormatConfig formatConfig=FormatConfig.getInstance();
    private String hostName;
    private boolean flag=true;
    private int fileRetransmitInterval=3000;
    private SenderThread sender;
    public DiskPredictDataSampler(String name,String osType){
        collectorIp= formatConfig.getCollectorIP();
        collectorPort= formatConfig.getPort(2);
        this.hostName=name;
        this.osType=osType;
        sampleFilePath = System.getProperty("user.dir") +"/DiskPredict/client/data_collector.py";
        dataFilePath=System.getProperty("user.dir") +"/DiskPredict/client/data.csv";
    }
    //修改成定时任务最好
    public void run(){
        while(true) {
            SU.setDaemon(true);
            if(!flag){
                sender.setKeepLooping(false);
                while(sender.isAlive());
                System.out.println("Sender exits");
            }
            SU.run(new CommandSampler(osType));
            flag=false;
            sender=new SenderThread();
            sender.start();
            try {
                Thread.sleep(sampleInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
    //
    private void sendFile(String url) throws IOException {
        File file=new File(url);
        FileInputStream fis=new FileInputStream(file);
        DataOutputStream dos=new DataOutputStream(fileSocket.getOutputStream());
        dos.writeInt(1);//代表特殊socket执行选项1，即接受文件
        dos.flush();
        dos.writeUTF(hostName);
        dos.flush();
        //提示日期？

        dos.writeLong(file.length());
        dos.flush();
        byte[] bytes=new byte[1024];
        int length=0;
        while((length=fis.read(bytes,0,bytes.length))!=-1){
            dos.write(bytes,0,length);
            dos.flush();
        }
        //System.out.println("[File]Send "+hostName+"-data.csv");
        fis.close();
        dos.close();

    }
    private class DiskSampler extends SuperUserApplication{
        @Override
        public int run(String[] strings) {
            try {
                Runtime rt = Runtime.getRuntime();
                System.out.println("python3");
                Process process=rt.exec("python3 "+sampleFilePath);
                BufferedReader stdInput=new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader ErrInput=new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String s;
                while((s=stdInput.readLine())!=null){
                    System.out.println(s);
                }
                while((s=ErrInput.readLine())!=null){
                    System.out.println(s);
                }

                stdInput.close();
                ErrInput.close();
                System.out.println("python3 finishes");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
    public class CommandSampler extends SuperUserApplication{
        public CommandSampler(String osType){
            this.osType=osType;
        }
        private String osType;
        @Override
        public int run(String[] strings) {

            //获取硬盘smart数据
            JSONObject diskData = new JSONObject();
            {
                //获取硬盘名
                List<String> diskList = new ArrayList<>();
                String getDiskListCmd = "";
                if(osType.toLowerCase().contains("windows")){
                    getDiskListCmd = "wmic logicaldisk get deviceid";
                    List<String> cmdResult = new CmdExecutor(getDiskListCmd).cmdResult;
                    cmdResult.remove(0);
                    for(String currentStr:cmdResult){
                        if(!currentStr.equals("")){
                            diskList.add(currentStr.trim());
                        }
                    }
                }
                else
                {
                    getDiskListCmd = "lsblk -bnd";
                    List<String> cmdResult = new CmdExecutor(getDiskListCmd).cmdResult;
                    for(String currentStr:cmdResult) {
                        String[] rawData = currentStr.split("\\s+");
                        System.out.println(rawData.length);
                        diskList.add("/dev/"+rawData[0]);
                        System.out.println("/dev/"+rawData[0]);
                    }
                }
                //以Json格式存数据
                String smartDiskInfoCmd = "smartctl -i ";
                String smartDataSampleCmd = "smartctl -A ";
                for(String currentDiskName: diskList){
                    JSONObject currentDiskData = new JSONObject();
                    {
                        List<String> cmdResult = new CmdExecutor(smartDiskInfoCmd + currentDiskName).cmdResult;
                        for (int i = 0; i < 4; i++) {
                            cmdResult.remove(0);
                        }
                        cmdResult.remove(cmdResult.size() - 1);
                        for(String currentOutput: cmdResult){
                            String[] rawData = currentOutput.split(":\\s+");
                            if(rawData.length<=1){
                                continue;
                            }
                            if(rawData[0].equals("Model Number")){
                                rawData[0]="Device Model";
                            }

                            currentDiskData.put(rawData[0],rawData[1]);
                        }
                    }

                    String serialNumber = currentDiskData.getString("Serial Number");
                    if(!diskData.containsKey(serialNumber)){
                        diskData.put(serialNumber,currentDiskData);
                        JSONObject smartData = new JSONObject();
                        {

                            List<String> cmdResult = new CmdExecutor(smartDataSampleCmd + currentDiskName).cmdResult;
                            for (int i = 0; i < 7; i++) {
                                cmdResult.remove(0);
                            }
                            for(String currentOutput: cmdResult){
                                if(!currentOutput.equals("")){
                                    currentOutput = currentOutput.trim();

                                    String[] rawData = currentOutput.split("\\s+");
                                    if(rawData.length<10){
                                        continue;
                                    }
                                    JSONObject currentSmart = new JSONObject();
                                    currentSmart.put("ATTRIBUTE_NAME",rawData[1]);
                                    currentSmart.put("VALUE",rawData[3]);
                                    currentSmart.put("RAW_VALUE",rawData[9]);
                                    smartData.put(rawData[0],currentSmart);
                                }
                            }
                            currentDiskData.put("SmartData",smartData);
                        }
                    }
                }
            }
            System.out.println(diskData.toString());
            //获取当前时间:
            String currentDate = "";
            String pt_d = "";
            {
                Date date = new Date();
                {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyy/MM/dd HH:mm");
                    currentDate = sdf.format(date);
                }
                {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyyMMdd");
                    pt_d = sdf.format(date);
                }
            }

            //写文件
            String sampleDataFilePath=System.getProperty("user.dir")+"/DiskPredict/client/data.csv";
            CsvWriter csvWriter = new CsvWriter(sampleDataFilePath,',', Charset.forName("GBK"));
            try {
                //Smart属性个数
                int smartCount = 256;
                //获取表头
                List<String> headers = new ArrayList<>();
                {
                    String[] staticHeaders = {"date", "serial_number", "model", "serialAlternative", "failure", "is_ssd", "pt_d"};
                    for(String staticHeader:staticHeaders){
                        headers.add(staticHeader);
                    }

                    String[] smartTagAttributes = {"_normalized","_raw"};
                    for(int i=0;i<smartCount;i++){
                        for(String attribute: smartTagAttributes){
                            headers.add("smart_"+ Integer.toString(i) + attribute);
                        }
                    }
                }

                //写入header头
                csvWriter.writeRecord(headers.toArray(new String[0]));

                //写入数据
                Set<String> serialNumberList = diskData.keySet();
                for(String serialNumber:serialNumberList){
                    JSONObject currentDiskData = diskData.getJSONObject(serialNumber);
                    List<String> rowData = new ArrayList<>();
                    {
                        rowData.add(currentDate);
                        rowData.add(serialNumber);
                        rowData.add(currentDiskData.getString("Device Model"));
                        rowData.add("");
                        rowData.add("0");
                        rowData.add("0");
                        rowData.add(pt_d);
                        JSONObject smartData = currentDiskData.getJSONObject("SmartData");
                        //@Todo 0到255
                        for(int i=0;i<smartCount;i++){
                            JSONObject currentSmart = smartData.getJSONObject(Integer.toString(i));
                            if(currentSmart != null){
                                rowData.add(currentSmart.getString("VALUE"));
                                rowData.add(currentSmart.getString("RAW_VALUE"));
                            }
                            else{
                                rowData.add("");
                                rowData.add("");
                            }
                        }
                    }
                    csvWriter.writeRecord(rowData.toArray(new String[0]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                csvWriter.close();
            }
            return 0;
        }

    }
    private class SenderThread extends Thread{
        volatile private boolean keepLooping=true;

        public void setKeepLooping(boolean keepLooping) {
            this.keepLooping = keepLooping;
        }

        public void run(){
            while(keepLooping&&!flag) {
                Calendar calendar=Calendar.getInstance();
//                if(calendar.get(Calendar.HOUR_OF_DAY)>23){
//                    System.out.println("Upload shoule be finished before 23:00,upload fails");
//                    break;
//                }
                try {
                    fileSocket = new Socket(collectorIp, collectorPort);
                    sendFile(dataFilePath);
                    fileSocket.close();
                    flag=true;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Can't connect to the collector.The disk sample file will be retransmitted in "+fileRetransmitInterval+"ms");
                    try {
                        Thread.sleep(fileRetransmitInterval);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
            if(!keepLooping){
                System.err.println("Because of the collector's disconnection"+new Date(System.currentTimeMillis()-24*3600*100)+" 's data doesn't be uploaded successfully");
            }
        }
    }
}
