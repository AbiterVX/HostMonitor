package com.hust.hostmonitor_data_collector.utils;

import com.hust.hostmonitor_data_collector.utils.SSHConnect.EthzSSHManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.hust.hostmonitor_data_collector.utils.SSHConnect.JschSSHManager;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.SSHManager;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;


//本地执行Cmd指令
class CmdLocalExecutor{
    public String cmd;
    public List<String> cmdResult;
    public CmdLocalExecutor(String currentCmd){
        cmd = currentCmd;
        cmdResult = new ArrayList<>();
        RunCmdUnderRoot runCmdUnderRoot = new RunCmdUnderRoot(this);
        SU.setDaemon(true);
        SU.run(runCmdUnderRoot);
    }

    private class RunCmdUnderRoot extends SuperUserApplication{
        public CmdLocalExecutor cmdLocalExecutor;
        public RunCmdUnderRoot(CmdLocalExecutor cmdLocalExecutor){
            this.cmdLocalExecutor = cmdLocalExecutor;
        }
        @Override
        public int run(String[] strings) {
            Runtime runtime = Runtime.getRuntime();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(runtime.exec(cmdLocalExecutor.cmd).getInputStream(), "GB2312"));
                String line;
                while ((line = br.readLine()) != null) {
                    cmdLocalExecutor.cmdResult.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}

//执行Cmd指令
public class CmdExecutor {
    //ssh连接管理
    private SSHManager sshManager;
    public CmdExecutor(){
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();
    }
    public List<String> runCommand(String cmd, HostConfigData hostConfigData,boolean isSudo) {
        if(hostConfigData == null){
            //本地执行
            List<String> cmdResult = new CmdLocalExecutor("cmd /c "+cmd).cmdResult;
            return cmdResult;
        }
        else{
            //远程执行
            List<String> cmdResult = sshManager.runCommand(cmd,hostConfigData,isSudo);
            return cmdResult;
        }
    }
}
