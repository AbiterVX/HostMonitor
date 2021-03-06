package com.hust.hostmonitor_data_collector.utils.SocketConnect;

import com.hust.hostmonitor_data_collector.service.DataCollectorService;
import com.hust.hostmonitor_data_collector.utils.DiskPredict.DiskPredict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class SpecialProcessor {
    private Logger logger= LoggerFactory.getLogger(SpecialProcessor.class);
    private DataCollectorService parent;
    private ServerSocket server;
    private String fileRepository;
    private int specialPort=7001;
    public final SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
    public SpecialProcessor(DataCollectorService parent){
        fileRepository = System.getProperty("user.dir") +"/DiskPredict/";
        this.parent=parent;
        try {
            this.server=new ServerSocket(specialPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListening(){
        Thread listeningThread=new ThreadListening();
        listeningThread.start();
    }
    public class ThreadListening extends Thread{
        public void run(){
            try {
                logger.info("[SpecialProcesser]===========Server Listening on "+specialPort+"============");
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
        private DataCollectorService parent;
        private String hostName;
        public FileProcessor(Socket socket, String remoteIp, String remotePort, DataCollectorService parent) {
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
            //从接收方获取int即确认执行何种任务
            try {
                int choice=inFromNode.readInt();
                if(choice==1){
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
                    DiskPredict.diskSampleDataIntegration(path+sdf.format(calendar.getTime())+".csv",path+hostName+".csv",remoteIp);
                    System.out.println("[File]New file data has been added to integrated file.");
                    System.out.println("[File]Temp file delete status:"+file.delete());
                }else {
                    DiskPredict.addRemoteIp(path+sdf.format(calendar.getTime())+".csv",path+hostName+".csv",remoteIp);
                    file.delete();
                    System.out.println("[File]New file renamed");
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
                synchronized (parent.getSocketMap()) {
                    parent.setAllDiskDFPState(remoteIp, false);
                }
                }
                else{

                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
