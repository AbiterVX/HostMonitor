package com.hust.hostmonitor_client.utils;


import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class SpecialProcessor {

    private ServerSocket server;
    private String OSName;
    public SpecialProcessor(String OSName){
        try {
            this.server=new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.OSName=OSName;
    }
    public void startListening(){
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                while (true){
                    Socket socket = server.accept();
                    StringTokenizer stringTokenizer=new StringTokenizer(socket.getRemoteSocketAddress().toString(),"/:");
                    String remoteIp=stringTokenizer.nextToken();
                    String remotePort=stringTokenizer.nextToken();
                    DataInputStream inFromCollector=new DataInputStream(socket.getInputStream());
                    DataOutputStream outToCollector=new DataOutputStream(socket.getOutputStream());
                    int workChoice=inFromCollector.readInt();
                    if(workChoice==0) {
                        Thread processingThread = new Thread(new TestProcessor(socket, remoteIp, remotePort, inFromCollector, outToCollector));
                        processingThread.start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class Processor{
        protected Socket socket;
        protected String remoteIp;
        protected String remotePort;
        protected DataInputStream inFromCollector;
        protected DataOutputStream outToCollector;
        public Processor(Socket socket, String remoteIp, String remotePort,DataInputStream inFromCollector,DataOutputStream outToCollector) {
            this.socket=socket;
            this.remoteIp = remoteIp;
            this.remotePort = remotePort;
            this.inFromCollector=inFromCollector;
            this.outToCollector=outToCollector;
        }
    }
    public class TestProcessor extends Processor implements Runnable{
        public TestProcessor(Socket socket, String remoteIp, String remotePort,DataInputStream inFromCollector,DataOutputStream outToCollector){
            super(socket,remoteIp,remotePort,inFromCollector,outToCollector);
        }
        @Override
        public void run() {
            try {
                int testChoice;
                while((testChoice=inFromCollector.readInt())!=-1){
                        if(testChoice==1){
                            String speedTestCmd=null;
                            String readSpeed=null;
                            String writeSpeed=null;
                           if(OSName.contains("Microsoft Windows")){
                               speedTestCmd = "winsat disk";
                           }
                           else {
                               String cmdFilePath=System.getProperty("user.dir")+"/SpeedTest.sh";
                               speedTestCmd = cmdFilePath;
                           }
                            List<String> cmdResult = new CmdExecutor(speedTestCmd).cmdResult;
                            if(cmdResult!=null){
                                if(OSName.contains("Microsoft Windows")){
                                    for(String currentOutput: cmdResult){
                                        String[] rawData = currentOutput.split("\\s+");
                                        if(currentOutput.contains("Disk  Sequential 64.0 Read")){
                                            readSpeed = rawData[5] +" "+ rawData[6];
                                        }
                                        else if(currentOutput.contains("Disk  Sequential 64.0 Write")){
                                            writeSpeed = rawData[5] +" "+ rawData[6];
                                        }
                                    }
                                }
                                else{
                                    writeSpeed = cmdResult.get(0);
                                    readSpeed = cmdResult.get(1);
                                }
                            }
                            outToCollector.writeUTF(writeSpeed);
                            outToCollector.writeUTF(readSpeed);
                        }
                }
                inFromCollector.close();
                outToCollector.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
