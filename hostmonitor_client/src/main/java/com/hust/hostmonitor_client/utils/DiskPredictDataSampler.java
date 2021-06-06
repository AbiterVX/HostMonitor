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
    private final long sampleInterval=24*3600*1000;
    private Socket fileSocket;
    private String collectorString="127.0.0.1";
    private String hostName;
    public DiskPredictDataSampler(String name){
        this.hostName=name;
        sampleFilePath = System.getProperty("user.dir") +"/DiskPredict/client/data_collector.py";
        dataFilePath=System.getProperty("user.dir") +"/DiskPredict/client/sampleData/data.csv";
    }

    public void run(){
        while(true) {
            SU.setDaemon(true);
            SU.run(new DiskSampler());
            try {
                fileSocket=new Socket(collectorString,7001);
                sendFile(dataFilePath);
                fileSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can't connect to the collector.The disk sample file isn't uploaded in time");
            }
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
    public class DiskSampler extends SuperUserApplication{

        @Override
        public int run(String[] strings) {
            System.out.println("RUN-AS-ADMIN");
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("python " + sampleFilePath);
                System.out.println(new Date());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
