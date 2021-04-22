package com.hust.hostmonitor_data_collector.utils;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SSHManager 的 Jsch实现方式
 * 与Ethz相比具有更强的稳定性
 */
public class JschSSHManager implements SSHManager{
    //JSCH
    JSch mainJSCH;
    //JSCH ssh Session List
    private Map<String, Session> sessionMap;

    public JschSSHManager(){
        mainJSCH = new JSch();
        sessionMap= new HashMap<>();
    }

    //获得Session
    private Session getJSCHSession(HostConfigInfo hostConfigInfo){
        boolean sessionExist = sessionMap.containsKey(hostConfigInfo.ip);
        Session currentSession = null;
        try {
            if(sessionExist){
                currentSession = sessionMap.get(hostConfigInfo.ip);
            }
            else{
                //创建session并且打开连接，因为创建session之后要主动打开连接
                currentSession = mainJSCH.getSession(hostConfigInfo.username, hostConfigInfo.ip, 22);
                currentSession.setPassword(hostConfigInfo.password);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                currentSession.setConfig(config);



                //设置代理
                if(hostConfigInfo.proxy){
                    ProxyHTTP proxyHTTP = new ProxyHTTP(hostConfigInfo.proxyIp,hostConfigInfo.proxyPort);
                    currentSession.setProxy(proxyHTTP);
                }

                currentSession.connect(1000);
                sessionMap.put(hostConfigInfo.ip,currentSession);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("JSCHSession Error, IP:"+ hostConfigInfo.ip);
            handleException(currentSession,hostConfigInfo.ip);
            currentSession = null;
        }
        return currentSession;
    }

    //执行JSCH指令
    @Override
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo){
        List<String> result = new ArrayList<String>();
        int returnCode = 0;
        Session session = getJSCHSession(hostConfigInfo);
        ChannelExec channelExec = null;
        if(session == null){
            System.out.println("Session null: " + hostConfigInfo.ip);
            return result;
        }
        try {
            //打开通道，设置通道类型，和执行的命令
            Channel channel = session.openChannel("exec");
            channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            channelExec.connect();

            //接收远程服务器执行命令的结果
            String line;
            while ((line = input.readLine()) != null) {
                result.add(line);
            }
            input.close();
            // 得到returnCode
            if (channelExec.isClosed()) {
                returnCode = channelExec.getExitStatus();
            }
            // 关闭通道
            channelExec.disconnect();
            //关闭session
            //session.disconnect();
        }
        catch (JSchException e) {
            e.printStackTrace();
            //处理异常，关闭连接
            handleException(session,hostConfigInfo.ip);
            channelExec.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            //处理异常，关闭连接
            handleException(session,hostConfigInfo.ip);
            channelExec.disconnect();

        }
        return result;
    }

    //处理异常
    public void handleException(Session currentSession, String _ip){
        if (currentSession != null) {
            currentSession.disconnect();
        }
        if(sessionMap.containsKey(_ip)){
            sessionMap.remove(_ip);
        }
    }
}
