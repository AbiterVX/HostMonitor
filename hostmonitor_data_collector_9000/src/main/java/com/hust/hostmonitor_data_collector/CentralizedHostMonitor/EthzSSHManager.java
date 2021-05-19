package com.hust.hostmonitor_data_collector.CentralizedHostMonitor;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SSHManager 的 ch.Ethz.ssh2实现方式
 */
public class EthzSSHManager implements SSHManager {
    //字符集
    private final String chartset = "UTF-8";
    //Ethz.ssh2  连接 List
    private Map<String, Connection> connectionMap;

    public EthzSSHManager(){
        connectionMap =new HashMap<>();
    }

    //获取Session
    private Session getEthzSession(HostConfigInfo hostConfigInfo){
        boolean sessionExist = connectionMap.containsKey(hostConfigInfo.ip);
        Connection newConnection = null;
        Session newSession = null;

        try {
            if(sessionExist){
                newConnection = connectionMap.get(hostConfigInfo.ip);
                newSession = newConnection.openSession();
            }
            else{
                newConnection = new Connection(hostConfigInfo.ip);
                newConnection.connect();

                boolean isAuthenticated = newConnection.authenticateWithPassword(hostConfigInfo.username, hostConfigInfo.password);
                if(isAuthenticated){
                    newSession = newConnection.openSession();
                    connectionMap.put(hostConfigInfo.ip,newConnection);
                }
                else{
                    //连接失败
                    return null;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (newSession != null) {
                newSession.close();
            }
            if (newConnection != null) {
                newConnection.close();
            }
            if(connectionMap.containsKey(hostConfigInfo.ip)){
                connectionMap.remove(hostConfigInfo.ip);
            }
        }
        return newSession;
    }
    //执行ch.ethz.ssh2指令
    @Override
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo) {
        List<String> result = new ArrayList<String>();

        //远程调用
        Session session = getEthzSession(hostConfigInfo);
        if(session != null){
            try {
                //执行指令
                session.execCommand(command);
                //输出
                InputStream is = new StreamGobbler(session.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is, chartset));
                //逐行获取输出结果
                for (String line = brs.readLine(); line != null; line = brs.readLine()) {
                    result.add(line);
                }
                //session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 1000 * 3600);
                //System.out.println("ExitCode: " + session.getExitStatus()); //得到脚本运行成功与否的标志 ：0 成功,非0 失败
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (session != null) {
                    session.close();
                }
            }
        }
        else{
            System.out.println("NULL");
        }
        return result;
    }
}
