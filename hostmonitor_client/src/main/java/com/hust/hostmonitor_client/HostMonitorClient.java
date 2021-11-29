package com.hust.hostmonitor_client;

import com.hust.hostmonitor_client.utils.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HostMonitorClient {
    public static Object lockObject=new Object();
    public static int SamplePeriod=FormatConfig.getInstance().getIntConifg("sampleInterval");
    public static int sampleIndex=0;
    private static int processFrequency=1;
    private static Sampler mainSampler=new OSHISampler();
    private static int WRITE_READ_UTF_MAX_LENGTH=65535;
    private static int SEGMENT_LENGTH=60000;
    private Logger logger= LoggerFactory.getLogger(HostMonitorClient.class);
    @SneakyThrows
    public static void main(String[] args) {
        boolean isTheFirstTimeToSample=true;
        Thread diskPredictDataSampler=new DiskPredictDataSampler(mainSampler.hostName(), mainSampler.OSName());
        diskPredictDataSampler.start();
        mainSampler.hardWareSample();
        SpecialProcessor specialProcessor=new SpecialProcessor(mainSampler.OSName());
        specialProcessor.startListening();
        DataSender senderThread=new DataSender();
        senderThread.setDaemon(true);
        senderThread.start();

        synchronized (lockObject){
            while(true){
                Util.sleep(SamplePeriod*1000);
                if(!isTheFirstTimeToSample){
                    mainSampler.periodSample(SamplePeriod,false);
                }
                else{
                    mainSampler.periodSample(SamplePeriod,true);
                    isTheFirstTimeToSample=false;
                }
                boolean judgeResult=false;
                if(sampleIndex%processFrequency==0){
                    mainSampler.processInfoSample(SamplePeriod,processFrequency);
                    sampleIndex=1;
                    judgeResult=true;
                }else{
                    sampleIndex++;
                }
                senderThread.setContextToBeSent(mainSampler.outputSampleData(judgeResult));
                lockObject.notifyAll();
                lockObject.wait();
            }

        }
    }
    public static class DataSender extends Thread{
        private String contextToBeSent=null;
        private FormatConfig formatConfig=FormatConfig.getInstance();
        private String collectorIP=formatConfig.getCollectorIP() ;
        private int collectorPort=formatConfig.getPort(1);
        private Socket clientSocket;
        private DataOutputStream outToCollector;
        private boolean connection_state=false;
        private int reconnectInterval=3000;
        private String checkString;
        public void setContextToBeSent(String contextToBeSent) {
            this.contextToBeSent = contextToBeSent;
        }
        @Override
        public void run() {
            checkString=Thread.currentThread().getName();
            while(!connection_state){
                connect();
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        private void connect() {
            try {
                clientSocket = new Socket(collectorIP, collectorPort);
                outToCollector = new DataOutputStream(clientSocket.getOutputStream());
                Thread thread = new Thread(new Client_send(clientSocket, outToCollector));
                thread.start();
                connection_state = true;
            } catch (IOException e) {
                System.err.println("["+Thread.currentThread().getName()+"]"+"Can't connect to the collector");
            }
        }
        private void reconnect() throws IOException {
            while(!connection_state){
                System.out.println("Try to reconnect to the collector");
                connect();
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public class Client_send implements Runnable{
            private Socket socket;
            private DataOutputStream outToCollector;
            public Client_send(Socket socket,DataOutputStream outToCollector){
                this.socket=socket;
                this.outToCollector=outToCollector;
            }
            @Override
            public void run() {
                try{
                    outToCollector.writeUTF(mainSampler.getHostName());
                    synchronized (lockObject) {
                        while (true) {
                            if (contextToBeSent.length() > WRITE_READ_UTF_MAX_LENGTH) {
                                int numberOfSegments = contextToBeSent.length() / SEGMENT_LENGTH + 1;
                                outToCollector.writeInt(numberOfSegments);
                                outToCollector.flush();
                                for (int i = 1; i <= numberOfSegments; i++) {
                                    outToCollector.writeUTF(contextToBeSent.substring(SEGMENT_LENGTH * (i - 1), SEGMENT_LENGTH * i < contextToBeSent.length() ? SEGMENT_LENGTH * i : contextToBeSent.length()));
                                    outToCollector.flush();
                                }
                            } else {
                                outToCollector.writeInt(1);
                                outToCollector.writeUTF(contextToBeSent);
                            }
                            System.out.println("[" + sampleIndex + "]" + "[Send]" + contextToBeSent);
                            lockObject.notifyAll();
                            lockObject.wait();
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                        System.err.println("["+Thread.currentThread().getName()+"]"+"Can't connect to the collector.The client keeps sampling but don't upload.");
                        System.err.println("["+Thread.currentThread().getName()+"]"+"Client will try to reconnect to the collector with the period of "+reconnectInterval+"ms");
                        connection_state=false;
                        reconnect();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
