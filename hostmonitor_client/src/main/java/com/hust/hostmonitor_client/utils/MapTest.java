package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class MapTest {
    public static void main(String[] args){
        HashMap<String, JSONObject> test=new HashMap<>();
        JSONObject test1=new JSONObject();
        test1.put("1","sss");
        test.put("test1",test1);
        System.out.println(test.get("test1"));
        JSONObject test2=new JSONObject();
        JSONObject test3=new JSONObject();
        test3.put("test3","666");
        test2.put("test2",test3);
        test.get("test1").putAll(test2);
        System.out.println(test.get("test1"));


    }
}
