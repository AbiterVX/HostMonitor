package com.hust.hostmonitor_client.utils;

import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class DiskPredictDataSampler extends Thread {
    private String sampleFilePath = "";
    private String dataFilePath="";
   // private final long sampleInterval=24*3600*1000;
    private final long sampleInterval=10*1000;
    private Socket fileSocket;
    private String collectorString="127.0.0.1";
    private String hostName;
    private boolean flag=true;
    private int fileRetransmitInterval=3000;
    private SenderThread sender;
    public DiskPredictDataSampler(String name){
        this.hostName=name;
        sampleFilePath = System.getProperty("user.dir") +"/DiskPredict/client/data_collector.py";
        dataFilePath=System.getProperty("user.dir") +"/DiskPredict/client/sampleData/data.csv";
    }

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
    private void sendFile(String url) throws IOException {
        File file=new File(url);
        FileInputStream fis=new FileInputStream(file);
        DataOutputStream dos=new DataOutputStream(fileSocket.getOutputStream());
        dos.writeUTF(hostName);
        dos.flush();
        dos.writeLong(file.length());
        dos.flush();
        byte[] bytes=new byte[1024];
        int length=0;
        while((length=fis.read(bytes,0,bytes.length))!=-1){
            dos.write(bytes,0,length);
            dos.flush();
        }
        System.out.println("[File]Send "+hostName+"-data.csv");
        fis.close();
        dos.close();

    }
    private class DiskSampler extends SuperUserApplication{
        @Override
        public int run(String[] strings) {
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("python " + sampleFilePath);
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
                try {
                    fileSocket = new Socket(collectorString, 7001);
                    sendFile(dataFilePath);
                    fileSocket.close();
                    flag=true;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Can't connect to the collector.The disk sample file will be retransmitted in "+fileRetransmitInterval+"ms");
                    try {
                        Thread.sleep(fileRetransmitInterval);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
            if(!keepLooping){
                System.out.println("Because of the collector's disconnection"+new Date(System.currentTimeMillis()-24*3600*100)+" 's data doesn't be uploaded successfully");
            }
        }
    }
}
