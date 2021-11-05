package com.hust.hostmonitor_data_collector.utils.SocketConnect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.service.DataCollectorService;
import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;

public class StreamProcessor implements Runnable{
    private Socket socket;
    private DataInputStream inFromNode;
    private String remoteIp;
    private String remotePort;
    private DataCollectorService parent;
    private String hostName;

    public StreamProcessor(Socket socket,String remoteIp,String remotePort,DataCollectorService parent) {
        this.socket=socket;
        try {
            this.inFromNode=new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.remoteIp=remoteIp;
        this.remotePort=remotePort;
        this.parent =parent;
    }

    @SneakyThrows
    @Override
    public void run() {
        detect();
        String receivedString;
        while(true){
            try {
                int numberOfSegments=inFromNode.readInt();
                StringBuilder tempString=new StringBuilder();
                for(int i=0;i<numberOfSegments;i++){
                    String temp=inFromNode.readUTF();
                    tempString.append(temp);
                }
                receivedString=tempString.toString();
                JSONObject UpdateObject=JSON.parseObject(receivedString);
                JSONObject oldDataObject=parent.getSocketMap().get(remoteIp);
                oldDataObject.putAll(UpdateObject);
                oldDataObject.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));
                oldDataObject.put("connected",true);
                oldDataObject.put("hasPersistent",false);
                //parent.hostInfoMap.put(remoteIp,UpdateObject);
                //System.out.println("["+remoteIp+"]"+"["+remotePort+"]"+"[Receive]"+receivedString);
            } catch (IOException e) {
                e.printStackTrace();
                inFromNode.close();
                parent.getSocketMap().get(remoteIp).put("connected",false);
                socket.close();
                break;
            }
        }
    }

    @SneakyThrows
    private void detect(){
       hostName=inFromNode.readUTF();
        if(parent.getSocketMap().get(remoteIp)==null){
            JSONObject hostInfoObject=new JSONObject();
            hostInfoObject.putAll(ConfigDataManager.getInstance().getSampleFormat("hostInfo"));
            hostInfoObject.put("ip",remoteIp);
            hostInfoObject.put("hostName",hostName);
            parent.getSocketMap().put(remoteIp,hostInfoObject);
        }
    }
}
