package com.hust.hostmonitor_data_collector.utils.SocketConnect;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TestInitiator {
    private Socket testSocket;
    private String testNodeIp="";
    private int testPort=7050;
    private boolean flag=true;
    private int reconnectInterval=3000;
    private DataOutputStream outToNode=null;
    private DataInputStream inFromNode=null;
    private boolean socketFinished=false;
    public TestInitiator(String nodeIp){
    this.testNodeIp=nodeIp;
    }
    public void socketInitialization(){
        while(!socketFinished) {
            try {
                testSocket = new Socket(testNodeIp, testPort);
                outToNode=new DataOutputStream(testSocket.getOutputStream());
                inFromNode=new DataInputStream(testSocket.getInputStream());
                socketFinished=true;
            } catch (IOException e) {
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
    public String executeTest(int choice){
        JSONObject result=new JSONObject();
        String readSpeed=null;
        String writeSpeed=null;
        try {
            //发出测试请求
            outToNode.writeInt(0);
            //发出测试选择, 1磁盘测速
            outToNode.writeInt(choice);
            //接收处理结果
            writeSpeed=inFromNode.readUTF();
            readSpeed=inFromNode.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("writeSpeed",writeSpeed);
        result.put("readSpeed",readSpeed);
        return result.toString();
    }
    public void closeTestSocket(){
        try {
            outToNode.writeInt(-1);//结束测试
            inFromNode.close();
            outToNode.close();
            testSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
