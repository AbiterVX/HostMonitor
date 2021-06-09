package com.hust.hostmonitor_data_collector.utils;

import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class FileReceiver {
    private DispersedHostMonitor parent;
    private ServerSocket server;
    private String fileRepository;
    public final SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
    public FileReceiver(DispersedHostMonitor parent){
        fileRepository = System.getProperty("user.dir") +"/DiskPredict/";
        this.parent=parent;
        try {
            this.server=new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListening(){
        System.out.println("[FileReceiver]Start listening for disk data");
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                System.out.println("===========Server Listening============");
                while (true){
                    Socket socket = server.accept();
                    // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
                    StringTokenizer stringTokenizer=new StringTokenizer(socket.getRemoteSocketAddress().toString(),"/:");
                    String remoteIp=stringTokenizer.nextToken();
                    String remotePort=stringTokenizer.nextToken();
                    Thread processingThread=new Thread(new FileProcessor(socket,remoteIp,remotePort,parent));
                    processingThread.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class FileProcessor implements Runnable{
        private Socket socket;
        private DataInputStream inFromNode;
        private String remoteIp;
        private String remotePort;
        private DispersedHostMonitor parent;
        private String hostName;
        public FileProcessor(Socket socket, String remoteIp, String remotePort, DispersedHostMonitor parent) {
            this.socket=socket;
            try {
                this.inFromNode = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.remoteIp = remoteIp;
            this.remotePort = remotePort;
            this.parent = parent;

        }

        @Override
        public void run() {
            try {
                hostName=inFromNode.readUTF();
                long filelength=inFromNode.readLong();
                Calendar calendar= Calendar.getInstance();
                String path=fileRepository+"original_data/"+calendar.get(Calendar.YEAR);
                File file=new File(path);
                if(!file.exists()){
                    file.mkdir();
                }
                path=path+"/"+(calendar.get(Calendar.MONTH)+1);
                file=new File(path);
                if(!file.exists()){
                    file.mkdir();
                }
                path=path+"/";
                file=new File(path+hostName+".csv");

                FileOutputStream fos=new FileOutputStream(file);
                byte[] bytes=new byte[1024];
                int length=0;
                while((length=inFromNode.read(bytes,0,bytes.length))!=-1){
                    fos.write(bytes,0,length);
                    fos.flush();
                }
                fos.close();
                inFromNode.close();
                socket.close();
                System.out.println("[File]Receive "+path);
                File integratedFile=new File(path+sdf.format(calendar.getTime())+".csv");
                if(integratedFile.exists()){
                    DiskPredict.diskSampleDataIntegration(path+sdf.format(calendar.getTime())+".csv",path+hostName+".csv");
                    System.out.println("[File]New file data has been added to integrated file.");
                    System.out.println("[File]Temp file delete status:"+file.delete());
                }else {
                    System.out.println("[File]New file rename status:"+file.renameTo(integratedFile));
                }
//                File file2;
//                path=fileRepository+"predict_data";
//                file2=new File(path);
//                if(!file2.exists()){
//                    file2.mkdir();
//                }
//                path=path+"/"+hostName;
//                file2=new File(path);
//                if(!file2.exists()){
//                    file2.mkdir();
//                }
//                path=path+"/"+hostName+".csv";
//                file2=new File(path);
//                Files.copy(file.toPath(),file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("[File]And a copy has been put into: "+path);
                synchronized (parent.hostInfoMap) {
                    parent.setAllDiskDFPState(hostName, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
