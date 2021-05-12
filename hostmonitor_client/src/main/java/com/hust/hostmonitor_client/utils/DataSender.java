package com.hust.hostmonitor_client.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class DataSender {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("127.0.0.1",7000);
            OutputStream outputStream = clientSocket.getOutputStream();

            while (true){

                String message="测试数据";
                clientSocket.getOutputStream().write(message.getBytes("UTF-8"));
                outputStream.flush();
                clientSocket.getOutputStream().flush();
                System.out.println("Send");

                Thread.sleep(1000);
            }
            //outputStream.close();
            //clientSocket.close();




        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
