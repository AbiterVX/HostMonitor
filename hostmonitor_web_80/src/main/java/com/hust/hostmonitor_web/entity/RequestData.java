package com.hust.hostmonitor_web.entity;

public class RequestData {
    public String data;
    public long timeStamp;
    public RequestData(String _data){
        data =_data;
        timeStamp = System.currentTimeMillis();
    }

}
