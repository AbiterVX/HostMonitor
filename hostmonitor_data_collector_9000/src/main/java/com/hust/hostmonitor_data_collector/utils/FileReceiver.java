package com.hust.hostmonitor_data_collector.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class FileReceiver {
    private DispersedHostMonitor parent;
    private ServerSocket server;
    private String fileRepository;
    public FileReceiver(DispersedHostMonitor parent){
        fileRepository = System.getProperty("user.dir") +"/DiskPredictData/input/";
        this.parent=parent;
        try {
            this.server=new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListening(){
        System.out.println("[FileReceiver]Start listening for disk data");
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                System.out.println("===========Server Listening============");
                while (true){
                    Socket socket = server.accept();
                    // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                    StringTokenizer stringTokenizer=new StringTokenizer(socket.getRemoteSocketAddress().toString(),"/:");
                    String remoteIp=stringTokenizer.nextToken();
                    String remotePort=stringTokenizer.nextToken();
                    Thread processingThread=new Thread(new FileProcessor(socket,remoteIp,remotePort,parent));
                    processingThread.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class FileProcessor implements Runnable{
        private Socket socket;
        private DataInputStream inFromNode;
        private String remoteIp;
        private String remotePort;
        private DispersedHostMonitor parent;
        private String hostName;
        public FileProcessor(Socket socket, String remoteIp, String remotePort, DispersedHostMonitor parent) {
            this.socket=socket;
            try {
                this.inFromNode = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.remoteIp = remoteIp;
            this.remotePort = remotePort;
            this.parent = parent;

        }

        @Override
        public void run() {
            try {
                hostName=inFromNode.readUTF();
                long fileLength=inFromNode.readLong();

                File file=new File(fileRepository+hostName+"-data.csv");
                FileOutputStream fos=new FileOutputStream(file);
                byte[] bytes=new byte[1024];
                int length=0;
                while((length=inFromNode.read(bytes,0,bytes.length))!=-1){
                    fos.write(bytes,0,length);
                    fos.flush();
                }
                fos.close();
                inFromNode.close();
                socket.close();
                System.out.println("[File]Receive "+hostName+"-data.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
