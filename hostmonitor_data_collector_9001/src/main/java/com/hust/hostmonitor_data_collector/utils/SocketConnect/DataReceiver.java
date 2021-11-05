package com.hust.hostmonitor_data_collector.utils.SocketConnect;

import com.hust.hostmonitor_data_collector.service.DataCollectorService;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class DataReceiver {
    private DataCollectorService parent;
    public DataReceiver(DataCollectorService parent){
        this.parent=parent;
    }
    public void startListening() {
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                ServerSocket serverSocket = new ServerSocket(7000);
                //System.out.println("===========Server Listening============");
                while (true){
                    Socket socket = serverSocket.accept();
                    // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                    StringTokenizer stringTokenizer=new StringTokenizer(socket.getRemoteSocketAddress().toString(),"/:");
                    String remoteIp=stringTokenizer.nextToken();
                    String remotePort=stringTokenizer.nextToken();

                    Thread processingThread=new Thread(new StreamProcessor(socket,remoteIp,remotePort,parent));
                    processingThread.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
