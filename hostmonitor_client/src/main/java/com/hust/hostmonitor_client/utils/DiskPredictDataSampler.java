package com.hust.hostmonitor_client.utils;

import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class DiskPredictDataSampler extends Thread {
    private String sampleFilePath = "";
    private String dataFilePath="";
    private final long sampleInterval=24*3600*1000;
    //private final long sampleInterval=10*1000;
    private Socket fileSocket;
    private String collectorIp="";
    private int collectorPort=7000;
    private FormatConfig formatConfig=FormatConfig.getInstance();
    private String hostName;
    private boolean flag=true;
    private int fileRetransmitInterval=3000;
    private SenderThread sender;
    public DiskPredictDataSampler(String name){
        collectorIp= formatConfig.getCollectorIP();
        collectorPort= formatConfig.getPort(2);
        this.hostName=name;
        sampleFilePath = System.getProperty("user.dir") +"/DiskPredict/client/data_collector.py";
        dataFilePath=System.getProperty("user.dir") +"/DiskPredict/client/sampleData/data.csv";
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
            SU.run(new DiskSampler());
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
