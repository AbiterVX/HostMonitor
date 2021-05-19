package com.hust.hostmonitor_data_collector.CentralizedHostMonitor;

import java.util.List;

/*
    SSHManager:通过SSH远程执行Shell的接口
    实现方式：Ethz.ssh2   JSCH
 */
public interface SSHManager {
    //执行指令
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo);
}
