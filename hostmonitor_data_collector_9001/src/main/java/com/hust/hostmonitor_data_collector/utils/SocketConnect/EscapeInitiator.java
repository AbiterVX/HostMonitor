package com.hust.hostmonitor_data_collector.utils.SocketConnect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class EscapeInitiator {
    private Socket srcSocket,dstSocket;
    private String srcNodeIp,dstNodeIp;
    private int Port=7050;
    private int escapePort=7060;
    private boolean flag=true;
    private int reconnectInterval=3000;
    private DataOutputStream outToSrc=null,outToDst=null;
    private DataInputStream inFromSrc=null,inFromDst=null;
    private boolean srcSocketFinished=false;
    private boolean dstSocketFinished=false;
    private String srcDiskBeanLogicalDrives;
    private String srcDiskBeanJson;

    public EscapeInitiator(String srcNodeIp,String dstNodeIp){
        this.srcNodeIp=srcNodeIp;
        this.dstNodeIp=dstNodeIp;
    }
    public void srcSocketInitialization(){
        while(!srcSocketFinished) {
            try {
                srcSocket = new Socket(srcNodeIp,Port);
                outToSrc=new DataOutputStream(srcSocket.getOutputStream());
                inFromSrc=new DataInputStream(srcSocket.getInputStream());
                srcSocketFinished=true;
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
    public void dstSocketInitialization(){
        while(!dstSocketFinished) {
            try {
                dstSocket = new Socket(dstNodeIp,Port);
                outToDst=new DataOutputStream(dstSocket.getOutputStream());
                inFromDst=new DataInputStream(dstSocket.getInputStream());
                dstSocketFinished=true;
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
    public String srcRequest(){
        try {
            outToSrc.writeInt(1);
            outToSrc.writeInt(1);
            //??????????????????
            String physicalDiskListJson=inFromSrc.readUTF();
            //????????????????????????????????????SN TODO
            String srcSN=null;
            outToSrc.writeUTF(srcSN);
            long size=inFromSrc.readLong();
            if(size==-1){
                //TODO ??????????????????
            }
            //???????????????????????????????????????????????????????????????
            String srcPath=null;
            outToSrc.writeUTF(srcPath);
            int compressResult=inFromSrc.readInt();
            if(compressResult==-1){
                return null;
            }
            else {
                outToSrc.writeUTF(dstNodeIp);
                outToSrc.writeInt(escapePort);
            }
            srcDiskBeanLogicalDrives=inFromSrc.readUTF();
            srcDiskBeanJson=inFromSrc.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public String dstRequest(){
        try {
            outToDst.writeInt(1);
            outToDst.writeInt(2);
            String physicalDiskListJson=inFromDst.readUTF();
            //????????????????????????????????????SN TODO
            String dstSN=null;
            outToDst.writeUTF(dstSN);
            long size=inFromDst.readLong();
            if(size==-1){
                //TODO ??????????????????
            }
            //TODO ????????????????????????????????????
            outToDst.writeUTF(srcDiskBeanLogicalDrives);
            outToDst.writeUTF(srcDiskBeanJson);

            //??????????????????

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public void closeSrcSocket(){
        try {
            outToSrc.writeInt(-1);
            inFromSrc.close();
            outToSrc.close();
            srcSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void closeDstSocket(){
        try {
            outToDst.writeInt(-1);
            inFromDst.close();
            outToDst.close();
            dstSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
