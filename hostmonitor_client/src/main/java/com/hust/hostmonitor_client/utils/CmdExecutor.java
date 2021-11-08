package com.hust.hostmonitor_client.utils;

import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    private class RunCmdUnderRoot extends SuperUserApplication {
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