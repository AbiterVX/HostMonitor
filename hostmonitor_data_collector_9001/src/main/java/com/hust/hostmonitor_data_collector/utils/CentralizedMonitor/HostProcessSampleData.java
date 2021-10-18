package com.hust.hostmonitor_data_collector.utils.CentralizedMonitor;

public class HostProcessSampleData {
    public String uid;
    public String pid;
    public String readKbps;
    public String writeKbps;
    public String command;

    public HostProcessSampleData(String _uid, String _pid, String _readKbps, String _writeKbps, String _command){
        uid=_uid;
        pid=_pid;
        readKbps=_readKbps;
        writeKbps=_writeKbps;
        command=_command;
    }
}
