package com.hust.hostmonitor_data_collector.utils;

import lombok.SneakyThrows;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class StreamProcessor implements Runnable{
    private DataInputStream inFromNode;
    private String remoteIp;
    private String remotePort;
    public StreamProcessor(String remoteAddress,DataInputStream inFromNode) {
        this.inFromNode=inFromNode;
        StringTokenizer stringTokenizer=new StringTokenizer(remoteAddress,"/:");
        this.remoteIp=stringTokenizer.nextToken();
        this.remotePort=stringTokenizer.nextToken();
    }

    @SneakyThrows
    @Override
    public void run() {
        String receivedString;
        while(true){
            try {
                receivedString=inFromNode.readUTF();
                System.out.println("["+remoteIp+"]"+"["+remotePort+"]"+"[Receive]"+receivedString);
            } catch (IOException e) {
                e.printStackTrace();
                inFromNode.close();
            }

        }
    }
}
