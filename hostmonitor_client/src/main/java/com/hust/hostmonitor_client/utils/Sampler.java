package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_client.utils.KylinEntity.KylinGPU;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Sampler {


    /*
        参考文档：http://oshi.github.io/oshi/oshi-core/apidocs/oshi/hardware/package-summary.html

        cpu利用率：两次间隔采样，并计算idle比例
        memory利用率：globalMemory.getAvailable() / getTotal
        磁盘占用率：计算每个盘的单独容量以及当前使用量。
        磁盘iops：两次采样，io个数/时间段=iops
        磁盘io速率：两次采样，磁盘读写数据量/时间段
        网络：两次采样，计算收发速率

    硬件：
        CPU：类型，核数，温度
        磁盘：类型，总量
        GPU：类型
        操作系统：类型，版本

    */

        public String hostName();
        public String OSName();
        public void hardWareSample();


        public void processInfoSample(int period,int processFrequency);

        public String outputSampleData(boolean insertProcessOrNot);
    }

