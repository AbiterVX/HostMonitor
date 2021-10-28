package com.hust.hostmonitor_client.utils;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskChecker;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskPartition;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.beans.PhysicalDiskBean;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SpecialProcessor {

    private ServerSocket server;
    private String OSName;
    public SpecialProcessor(String OSName){
        try {
            this.server=new ServerSocket(7050);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.OSName=OSName;
    }
    public void startListening(){
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                while (true){
                    Socket socket = server.accept();
                    StringTokenizer stringTokenizer=new StringTokenizer(socket.getRemoteSocketAddress().toString(),"/:");
                    String remoteIp=stringTokenizer.nextToken();
                    String remotePort=stringTokenizer.nextToken();
                    DataInputStream inFromCollector=new DataInputStream(socket.getInputStream());
                    DataOutputStream outToCollector=new DataOutputStream(socket.getOutputStream());
                    int workChoice=inFromCollector.readInt();
                    if(workChoice==0) {
                        Thread processingThread = new Thread(new TestProcessor(socket, remoteIp, remotePort, inFromCollector, outToCollector));
                        processingThread.start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class Processor{
        protected Socket socket;
        protected String remoteIp;
        protected String remotePort;
        protected DataInputStream inFromCollector;
        protected DataOutputStream outToCollector;
        public Processor(Socket socket, String remoteIp, String remotePort,DataInputStream inFromCollector,DataOutputStream outToCollector) {
            this.socket=socket;
            this.remoteIp = remoteIp;
            this.remotePort = remotePort;
            this.inFromCollector=inFromCollector;
            this.outToCollector=outToCollector;
        }
    }
    public class TestProcessor extends Processor implements Runnable{
        public TestProcessor(Socket socket, String remoteIp, String remotePort,DataInputStream inFromCollector,DataOutputStream outToCollector){
            super(socket,remoteIp,remotePort,inFromCollector,outToCollector);
        }
        @Override
        public void run() {
            try {
                int testChoice;
                while((testChoice=inFromCollector.readInt())!=-1){
                        if(testChoice==1){
                            String speedTestCmd=null;
                            String readSpeed=null;
                            String writeSpeed=null;
                           if(OSName.contains("Microsoft Windows")){
                               speedTestCmd = "winsat disk";
                           }
                           else {
                               String cmdFilePath=System.getProperty("user.dir")+"/SpeedTest.sh";
                               speedTestCmd = cmdFilePath;
                           }
                            List<String> cmdResult = new CmdExecutor(speedTestCmd).cmdResult;
                            if(cmdResult!=null){
                                if(OSName.contains("Microsoft Windows")){
                                    for(String currentOutput: cmdResult){
                                        String[] rawData = currentOutput.split("\\s+");
                                        if(currentOutput.contains("Disk  Sequential 64.0 Read")){
                                            readSpeed = rawData[5] +" "+ rawData[6];
                                        }
                                        else if(currentOutput.contains("Disk  Sequential 64.0 Write")){
                                            writeSpeed = rawData[5] +" "+ rawData[6];
                                        }
                                    }
                                }
                                else{
                                    writeSpeed = cmdResult.get(0);
                                    readSpeed = cmdResult.get(1);
                                }
                            }
                            outToCollector.writeUTF(writeSpeed);
                            outToCollector.writeUTF(readSpeed);
                        }
                        else if(testChoice==2){
                            //作为源节点
                            String physicalDiskListJson=DiskChecker.diskCheck();
                            JSONArray jsonArray = JSON.parseArray(physicalDiskListJson);
                            List<PhysicalDiskBean> physicalDiskList = new ArrayList<>();
                            // 以下处理不必要
                            physicalDiskList.clear();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                physicalDiskList.add(jsonObject.toJavaObject(PhysicalDiskBean.class));
                            }
                            System.out.println("\n########### 硬盘信息如下 ###########\n" + DiskChecker.prettyJson(physicalDiskList));
                            //返回给服务器
                            String srcSN=null;
                            PhysicalDiskBean srcDiskBean = null;

                            for (PhysicalDiskBean diskBean : physicalDiskList) {
                                if (diskBean.getSn().equals(srcSN)) {
                                    srcDiskBean = diskBean;
                                }
                            }
                            //返回大小，检查
                            List<LogicalDiskBean> lDiskList = srcDiskBean.getLogicalDrives();
                        }
                        else if(testChoice==3){
                            String physicalDiskListJson=DiskChecker.diskCheck();
                            JSONArray jsonArray = JSON.parseArray(physicalDiskListJson);
                            List<PhysicalDiskBean> physicalDiskList = new ArrayList<>();
                            // 以下处理不必要
                            physicalDiskList.clear();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                physicalDiskList.add(jsonObject.toJavaObject(PhysicalDiskBean.class));
                            }
                            System.out.println("\n########### 硬盘信息如下 ###########\n" + DiskChecker.prettyJson(physicalDiskList));
                            //返回给服务器
                            String dstSN=null;
                            PhysicalDiskBean dstDiskBean = null;
                            for (PhysicalDiskBean diskBean : physicalDiskList) {
                                if (diskBean.getSn().equals(dstSN)) {
                                    dstDiskBean = diskBean;
                                }
                            }
                            //返回大小，检查
                            //对目标盘进行分区，设置分区格式
                            List<LogicalDiskBean> dstLDiskList = new ArrayList<>();
                            List<LogicalDiskBean> lDiskList=null;

                            for (LogicalDiskBean lBean : lDiskList) {
                                LogicalDiskBean lDiskBean = new LogicalDiskBean();
                                System.out.println("########### 源硬盘分区信息如下 ###########");
                                System.out.println(DiskChecker.prettyJson(lBean));
                                System.out.println("====> 选择分区类型（Primary: 0 / Logical: 1）:");
                                int isPrimary = in.nextInt();
                                System.out.println("====> Bootable: 0 / UnBootable: 1）:");
                                int isBoot = in.nextInt();
                                in.nextLine();
                                System.out.println("====> 请设置盘符（不能选择重复盘符）:");
                                String cpation = in.nextLine();
                                System.out.println("====> 请设置分区名称:");
                                String name = in.nextLine();
                                System.out.println("====> 请设置文件系统（NTFS / FAT32）:");
                                String fileSystem = in.nextLine();
                                System.out.println("====> 请设置大小，（单位：Byte）:");
                                long capacity = in.nextLong();
                                in.nextLine();
                                lDiskBean.setPrimaryPartition(isPrimary == 0);
                                lDiskBean.setBootPartition(isBoot == 0);
                                // lDiskBean.setBootable(isBootVolume == 0);
                                lDiskBean.setCaption(cpation);
                                lDiskBean.setName(name);
                                lDiskBean.setFileSystem(fileSystem);
                                lDiskBean.setTotal(capacity);
                                dstLDiskList.add(lDiskBean);
                            }
                            dstDiskBean.setLogicalDrives(dstLDiskList);

                            // 在目的硬盘上进行分区，此步骤会格式化目的硬盘导致数据丢失，须谨慎操作
                            DiskPartition.diskPartition(JSON.toJSONString(dstDiskBean));


                        }
                }
                inFromCollector.close();
                outToCollector.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
