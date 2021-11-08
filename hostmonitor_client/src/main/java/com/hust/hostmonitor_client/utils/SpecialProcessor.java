package com.hust.hostmonitor_client.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_client.utils.data_escape.DataEscapeUtils.DstNode.ServerManager;
import com.hust.hostmonitor_client.utils.data_escape.DataEscapeUtils.SrcNode.ClientManager;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskChecker;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskPartition;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskRecover;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.DiskZipper;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.beans.ImageFileBean;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.hust.hostmonitor_client.utils.data_escape.DiskUtils.cmds.DISM;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
                    else if(workChoice==1){
                        Thread processingThread =new Thread(new EscapeProcessor(socket,remoteIp,remotePort,inFromCollector,outToCollector));
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

                }
                inFromCollector.close();
                outToCollector.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class EscapeProcessor extends Processor implements Runnable{
        public EscapeProcessor(Socket socket, String remoteIp, String remotePort,DataInputStream inFromCollector,DataOutputStream outToCollector){
            super(socket,remoteIp,remotePort,inFromCollector,outToCollector);
        }
        @Override
        public void run() {
            try{
                int choice;
                while((choice=inFromCollector.readInt())!=-1){
                    if(choice==1){
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
                        //System.out.println("\n########### 硬盘信息如下 ###########\n" + DiskChecker.prettyJson(physicalDiskList));
                        outToCollector.writeUTF(physicalDiskListJson);
                        String srcSN=inFromCollector.readUTF();
                        PhysicalDiskBean srcDiskBean = null;
                        for (PhysicalDiskBean diskBean : physicalDiskList) {
                            if (diskBean.getSn().equals(srcSN)) {
                                srcDiskBean = diskBean;
                            }
                        }
                        //返回大小，检查

                        List<LogicalDiskBean> lDiskList = srcDiskBean.getLogicalDrives();

                        // 源硬盘压缩
                        // 首先需要选择要压缩的硬盘，以及压缩文件暂存的位置，这里只提供盘符供选择
                        // 以下逻辑需要 web 端进行实现
                        // 选择需要压缩的硬盘（故障硬盘）
                        System.out.println("====> 需要备份迁移的故障硬盘（SN）:"+srcDiskBean);
                        if (srcDiskBean == null) {
                            System.out.println("########### 没有匹配的 SN 码 ###########");
                            outToCollector.writeLong(-1);
                            break;
                        }
                        outToCollector.writeLong(srcDiskBean.getUsed());
                        // 这里还需要检查容量是否足够，压缩的比例约为0.9，由 web端 完成
                        System.err.println("====> 选择备份文件的暂存位置（盘符、根目录）:");
                        String srcCapation = inFromCollector.readUTF();
                        // 向节点发出请求，开始备份:
                        // 备份完成后返回服务器
                        String imageFileJson = DiskZipper.diskZip(JSON.toJSONString(srcDiskBean), srcCapation);

                        // web端接收到返回信息，展示结果
                        ImageFileBean imageFileBean;
                        if (imageFileJson == null) {
                            System.out.println("########### 硬盘压缩失败 ###########");
                            outToCollector.writeInt(-1);
                            break;
                        } else {
                            // 展示结果
                            imageFileBean = JSON.parseObject(imageFileJson, ImageFileBean.class);
                            System.out.println("\n########### 压缩后的硬盘镜像文件如下 ###########\n" + DiskChecker.prettyJson(imageFileBean));
                            System.out.println("\n########### 压缩后的硬盘镜像文件内容如下 ###########\n" + DISM.getInfoFromImage(imageFileBean.getPath()));
                            outToCollector.writeInt(1);
                        }
                        String dstNodeIP=inFromCollector.readUTF();
                        int escapePort=inFromCollector.readInt();
                        System.out.println("====> 请输入文件片大小 默认20 单位:M:");
                        int fileShardSize = 20;
                        // 连接开始，此时必须先启动服务器
                        ClientManager clientManager = new ClientManager(dstNodeIP, escapePort, JSON.toJSONString(imageFileBean), fileShardSize);
                        clientManager.clientStart();
                        // 当文件传输完毕后，源节点的业务流程就已经全部结束了
                        outToCollector.writeUTF(JSON.toJSONString(srcDiskBean.getLogicalDrives()));
                        outToCollector.writeUTF(JSON.toJSONString(srcDiskBean));
                    }
                    else if(choice==2){
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
                        String dstSN= inFromCollector.readUTF();
                        PhysicalDiskBean dstDiskBean = null;
                        for (PhysicalDiskBean diskBean : physicalDiskList) {
                            if (diskBean.getSn().equals(dstSN)) {
                                dstDiskBean = diskBean;
                            }
                        }
                        System.out.println("====> 接收备份的目的磁盘（SN）:"+dstDiskBean);
                        if (dstDiskBean == null) {
                            System.out.println("########### 没有匹配的 SN 码 ###########");
                            outToCollector.writeLong(-1);
                            break;
                        }
                        outToCollector.writeLong(dstDiskBean.getTotal());
                        //返回大小，检查
                        //对目标盘进行分区，设置分区格式
                        List<LogicalDiskBean> dstLDiskList = new ArrayList<>();
                        List<LogicalDiskBean> lDiskList=null;
                        Scanner in =new Scanner(System.in);
                        for (LogicalDiskBean lBean : lDiskList) {
                            LogicalDiskBean lDiskBean = new LogicalDiskBean();
                            System.out.println("########### 源硬盘分区信息如下 ###########");
                            System.out.println(DiskChecker.prettyJson(lBean));
                            //System.out.println("====> 选择分区类型（Primary: 0 / Logical: 1）:");
                            int isPrimary = 1;
                            //System.out.println("====> Bootable: 0 / UnBootable: 1）:");
                            int isBoot =0;
                            System.out.println("====> 请设置盘符（不能选择重复盘符）:");
                            String cpation = "Z";
                            System.out.println("====> 请设置分区名称:");
                            String name = "backup";
                            System.out.println("====> 请设置文件系统（NTFS / FAT32）:");
                            String fileSystem = "NTFS";
                            System.out.println("====> 请设置大小，（单位：Byte）:");
                            long capacity = 500*1024*1024*1024;
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


                        in.nextLine();
                        // 这一步在 web端完成，先于源节点的传输请求启动
                        System.out.println("====> 请输入 IP:");
                        String serverIP ="127.0.0.1";
                        System.out.println("====> 请输入 端口:");
                        int serverPort = 7060;
                        // 缓冲区的大小必须比源节点的文件片大小大,可以通过算法设置不必用户输入
                        System.out.println("====> 请输入缓冲区大小 默认20 单位:M:");
                        int fileShardSize = 20;
                        System.out.println("====> 请输文件保存位置（盘符 / 根目录）");
                        String fileSavePath ="/";
                        ServerManager serverManager = new ServerManager(serverIP, serverPort, fileShardSize, fileSavePath);
                        serverManager.serverStart();


                        System.out.println("########### 硬盘镜像文件将解压到 ###########");
                        System.out.println(dstDiskBean.toString());

                        // 这里存在一个映射关系，因为源硬盘分区与目的硬盘分区的盘符不一样
                        // 不过在分区的时候这个映射关系已经确定了
                        System.out.println("########### 分区映射关系如下所示 ###########");
                        String srcLDiskBeanLogicalDrives =inFromCollector.readUTF();
                        String srcDiskBeanJson=inFromCollector.readUTF();
                        JSONArray srcDiskBeans=JSON.parseArray(srcLDiskBeanLogicalDrives);
                        List<LogicalDiskBean> dstLDiskBeans = dstDiskBean.getLogicalDrives();
                        for (int i = 0; i < srcDiskBeans.size(); i++) {
                            System.out.println(srcDiskBeans.get(i).toString() + "\n<===== 对应 =====>\n" + dstLDiskBeans.get(i).toString());
                        }
                        DiskRecover.diskRecover(srcDiskBeanJson, JSON.toJSONString(dstDiskBean),
                                "E:\\dataescape_recv_tmp\\fa6641458842b7ec9cb0ddc621cd6b2e.wim");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
