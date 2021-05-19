package com.hust.hostmonitor_client;

import com.hust.hostmonitor_client.utils.DataSampler;
import lombok.SneakyThrows;
import oshi.util.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HostMonitorClient {
    public static Object lockObject=new Object();
    public static int SamplePeriod=10000;
    public static int sampleIndex=0;
    @SneakyThrows
    public static void main(String[] args) {
        DataSampler mainSampler=new DataSampler();
        boolean isTheFirstTimeToSample=true;
        String sampleResultJson;
        mainSampler.hardWareSample();
        DataSender senderThread=new DataSender();
        senderThread.start();

        synchronized (lockObject){
            while(true){
                Util.sleep(SamplePeriod);
                if(!isTheFirstTimeToSample){
                    mainSampler.periodSample(SamplePeriod/1000,false);
                }
                else{
                    mainSampler.periodSample(SamplePeriod/1000,true);
                    isTheFirstTimeToSample=false;
                }
                sampleIndex++;
                senderThread.setContextToBeSent(mainSampler.outputSampleData());
                lockObject.notifyAll();
                lockObject.wait();
            }

        }
    }
    public static class DataSender extends Thread{
        private String contextToBeSent=null;
        private String collectorIP="127.0.0.1";
        public void setContextToBeSent(String contextToBeSent) {
            this.contextToBeSent = contextToBeSent;
        }
        @Override
        public void run() {
            try {
                Socket clientSocket = new Socket(collectorIP,7000);
                DataOutputStream outToCollector=new DataOutputStream(clientSocket.getOutputStream());
                synchronized (lockObject) {
                    while (true){
                        outToCollector.writeUTF(contextToBeSent);
                        System.out.println("["+sampleIndex+"]"+"[Send]" + contextToBeSent);
                        lockObject.notifyAll();
                        lockObject.wait();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
