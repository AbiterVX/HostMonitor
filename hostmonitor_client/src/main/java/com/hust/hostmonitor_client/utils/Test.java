package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;



class CmdExecutor{
    public String cmd;
    public List<String> cmdResult;
    public CmdExecutor(String currentCmd){
        cmd = currentCmd;
        cmdResult = new ArrayList<>();
        RunCmdUnderRoot runCmdUnderRoot = new RunCmdUnderRoot(this);
        SU.setDaemon(true);
        SU.run(runCmdUnderRoot);
    }

    private class RunCmdUnderRoot extends SuperUserApplication{
        public CmdExecutor cmdExecutor;
        public RunCmdUnderRoot(CmdExecutor cmdExecutor){
            this.cmdExecutor = cmdExecutor;
        }
        @Override
        public int run(String[] strings) {
            Runtime runtime = Runtime.getRuntime();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(runtime.exec(cmdExecutor.cmd).getInputStream(), "GB2312"));
                String line;
                while ((line = br.readLine()) != null) {
                    cmdExecutor.cmdResult.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}

public class Test  {
    public static void main(String[] args) {
//        //判断OS类型
//        SystemInfo systemInfo = new SystemInfo();
//        OperatingSystem os = systemInfo.getOperatingSystem();
//        String osName = os.toString();
//
//        //输入的指令
//        String speedTestCmd;
//        if(osName.contains("Microsoft Windows")){
//            speedTestCmd = "winsat disk";
//        }
//        else{
//            String cmdFilePath=System.getProperty("user.dir")+"/SpeedTest.sh";
//
//            speedTestCmd = cmdFilePath;
//
//                    //"(LANG=C dd if=/dev/zero of=benchtest_$$ bs=64k count=16k conv=fdatasync ) 2>&1 | awk -F, '{io=$NF} END { print io}' | sed 's/^[ \\t]*//;s/[ \\t]*$//'  && " +
//                    //"(LANG=C dd if=benchtest_$$ of=/dev/null bs=64k count=16k conv=fdatasync) 2>&1 | awk -F, '{io=$NF} END { print io}' | sed 's/^[ \\t]*//;s/[ \\t]*$//'   && " +
//                    //"rm -f benchtest_$$ ";
//        }
//        //输出
//        String readSpeed = "";
//        String writeSpeed = "";
//
//        //处理输出数据
//        List<String> cmdResult = new CmdExecutor(speedTestCmd).cmdResult;
//        if(cmdResult!=null){
//            if(osName.contains("Microsoft Windows")){
//                for(String currentOutput: cmdResult){
//                    String[] rawData = currentOutput.split("\\s+");
//                    if(currentOutput.contains("Disk  Sequential 64.0 Read")){
//                        readSpeed = rawData[5] +" "+ rawData[6];
//                    }
//                    else if(currentOutput.contains("Disk  Sequential 64.0 Write")){
//                        writeSpeed = rawData[5] +" "+ rawData[6];
//                    }
//                }
//            }
//            else{
//                writeSpeed = cmdResult.get(0);
//                readSpeed = cmdResult.get(1);
//            }
//        }
//
//        //结果
//        System.out.println(readSpeed);
//        System.out.println(writeSpeed);
    }
}
