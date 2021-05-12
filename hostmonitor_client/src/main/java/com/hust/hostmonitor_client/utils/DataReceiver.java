package com.hust.hostmonitor_client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DataReceiver {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("Begin");
            while (true){
                Socket socket = serverSocket.accept();

                // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = inputStream.read(bytes)) != -1) {
                    // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                    sb.append(new String(bytes, 0, len, "UTF-8"));
                }
                System.out.println("get message from client: " + sb);
                inputStream.close();
                socket.close();
            }

            //inputStream.close();
            //socket.close();
            //serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
