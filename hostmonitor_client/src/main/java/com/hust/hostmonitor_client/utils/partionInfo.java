package com.hust.hostmonitor_client.utils;

import java.util.Map;

public class partionInfo {
    public String volumn;
    public long total;


    public partionInfo(String volumn, long total, long usable) {
        this.volumn = volumn;
        this.total = total;
        this.usable = usable;
    }

    public long usable;
}
