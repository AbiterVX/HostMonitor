package com.hust.hostmonitor_data_collector.utils;

import org.apache.poi.ss.formula.functions.T;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DataReceiver {
    public DataReceiver(){
        startListening();
    }
    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("===========Server Listening============");
            while (true){
                Socket socket = serverSocket.accept();
                // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                DataInputStream inFromNode = new DataInputStream(socket.getInputStream());
                Thread processingThread=new Thread(new StreamProcessor(socket.getRemoteSocketAddress().toString(),inFromNode));
                processingThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListening() {
        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("===========Server Listening============");
            while (true){
                Socket socket = serverSocket.accept();
                // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                DataInputStream inFromNode = new DataInputStream(socket.getInputStream());
                Thread processingThread=new Thread(new StreamProcessor(socket.getRemoteSocketAddress().toString(),inFromNode));
                processingThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
