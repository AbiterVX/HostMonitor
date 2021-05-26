package com.hust.hostmonitor_client.utils;

import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class DiskPredictDataSampler extends Thread {
    private String exeFilePath = "";//ConfigData/Client/data_collector-windows.exe";
    private String dataFilePath="";
    private final long sampleInterval=12*3600*1000;
    private Socket fileSocket;
    private String collectorString="127.0.0.1";
    private String hostName;
    public DiskPredictDataSampler(String name){
        this.hostName=name;
        exeFilePath = System.getProperty("user.dir") +"/ConfigData/Client/data-collector.exe";
        dataFilePath=System.getProperty("user.dir") +"/ConfigData/Client/data.csv";
    }
    @SneakyThrows
    public void run(){
        while(true) {
            SU.setDaemon(true);
            SU.run(new DiskSampler());
            fileSocket=new Socket(collectorString,7001);
            sendFile(dataFilePath);
            fileSocket.close();
            Thread.sleep(sampleInterval);

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
                rt.exec(exeFilePath);
                System.out.println(new Date());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
