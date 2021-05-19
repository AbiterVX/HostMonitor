package com.hust.hostmonitor_data_collector.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

public class StreamProcessor implements Runnable{
    private DataInputStream inFromNode;
    private String remoteIp;
    private String remotePort;
    private DispersedHostMonitor parent;
    public StreamProcessor(DataInputStream inFromNode,String remoteIp,String remotePort,DispersedHostMonitor parent) {
        this.inFromNode=inFromNode;
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
                receivedString=inFromNode.readUTF();
                JSONObject UpdateObject=JSON.parseObject(receivedString);
                JSONObject oldDataObject=parent.hostInfoMap.get(remoteIp);
                oldDataObject.putAll(UpdateObject);
                oldDataObject.put("lastUpdateTime",new Timestamp(System.currentTimeMillis()));
                oldDataObject.put("connected",true);
                //parent.hostInfoMap.put(remoteIp,UpdateObject);
                System.out.println("["+remoteIp+"]"+"["+remotePort+"]"+"[Receive]"+receivedString);
            } catch (IOException e) {
                e.printStackTrace();
                inFromNode.close();
                parent.hostInfoMap.get(remoteIp).put("connected",false);
            }

        }
    }

    private void detect(){
        if(parent.hostInfoMap.get(remoteIp)==null){
            JSONObject hostInfoObject=new JSONObject();
            hostInfoObject.putAll(DispersedConfig.getInstance().getHostInfoJson());
            hostInfoObject.put("ip",remoteIp);
            parent.hostInfoMap.put(remoteIp,hostInfoObject);
        }
    }
}
