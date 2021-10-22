package com.hust.hostmonitor_client.utils;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import oshi.SystemInfo;
//import oshi.hardware.*;
//import oshi.hardware.CentralProcessor.TickType;
//import oshi.software.os.*;
//import oshi.software.os.OperatingSystem.ProcessSort;
//import oshi.util.FormatUtil;
//import oshi.util.Util;
//
//import java.util.Arrays;
//import java.util.List;
//

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.software.os.OSProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Test  {
    public static class TestDiskSampler extends SuperUserApplication{
        String sampleFilePath = System.getProperty("user.dir") +"/DiskPredict/client/data_collector.py"; //E:/Code/HostMonitor
        @Override
        public int run(String[] strings) {
            System.out.println("RUN-AS-ADMIN");
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("python " + sampleFilePath);
                System.out.println(new Date());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        HashMap<String,JSONObject> test=new HashMap<>();
        JSONObject initObject=new JSONObject();
        initObject.put("test","aaa");
        test.put("object1",initObject);
        System.out.println(test.get("object1"));
        initObject.put("test2","bbb");
        System.out.println(test.get("object1"));
    }
}
