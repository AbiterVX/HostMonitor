package com.hust.hostmonitor_client.utils;

import com.vnetpublishing.java.suapp.SuperUserApplication;

import java.io.IOException;

public class DiskPredictDataSampler extends SuperUserApplication {
    private String exeFilePath = "";//ConfigData/Client/data_collector-windows.exe";
    public DiskPredictDataSampler(){
        exeFilePath = System.getProperty("user.dir") +"/ConfigData/Client/data-collector.exe";
    }
    @Override
    public int run(String[] strings) {
        System.out.println("RUN-AS-ADMIN");
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(exeFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
