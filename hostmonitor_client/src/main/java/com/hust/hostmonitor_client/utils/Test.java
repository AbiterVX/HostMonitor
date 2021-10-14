package com.hust.hostmonitor_client.utils;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;



class CmdExecutor{
    public String cmd;
    public List<String> cmdResult;
    public CmdExecutor(String currentCmd){
        cmd = currentCmd;
        cmdResult = new ArrayList<>();
        RunCmdUnderRoot runCmdUnderRoot = new RunCmdUnderRoot(this);
        SU.setDaemon(true);
        SU.run(runCmdUnderRoot);
    }

    private class RunCmdUnderRoot extends SuperUserApplication{
        public CmdExecutor cmdExecutor;
        public RunCmdUnderRoot(CmdExecutor cmdExecutor){
            this.cmdExecutor = cmdExecutor;
        }
        @Override
        public int run(String[] strings) {
            Runtime runtime = Runtime.getRuntime();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(runtime.exec(cmdExecutor.cmd).getInputStream(), "GB2312"));
                String line;
                while ((line = br.readLine()) != null) {
                    cmdExecutor.cmdResult.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}

public class Test  {
    public static void main(String[] args) {
        //判断OS类型
        String osName = "";
        {
            {
                List<String> cmdResult = new CmdExecutor("cmd /c ver").cmdResult;
                if(cmdResult.size() == 2){
                    osName = cmdResult.get(1);
                }
            }
            if(osName.equals("")){
                List<String> cmdResult = new CmdExecutor("cat /proc/version").cmdResult;
                osName = cmdResult.get(0);
            }
        }

        //获取硬盘smart数据
        JSONObject diskData = new JSONObject();
        {
            //获取硬盘名
            List<String> diskList = new ArrayList<>();
            {
                String getDiskListCmd = "";
                if(osName.contains("Microsoft Windows")){
                    getDiskListCmd = "wmic logicaldisk get deviceid";
                    List<String> cmdResult = new CmdExecutor(getDiskListCmd).cmdResult;
                    cmdResult.remove(0);
                    for(String currentStr:cmdResult){
                        if(!currentStr.equals("")){
                            diskList.add(currentStr.trim());
                        }
                    }
                }
                else{
                    getDiskListCmd = "lsblk -bnd";
                    List<String> cmdResult = new CmdExecutor(getDiskListCmd).cmdResult;
                    for(String currentStr:cmdResult) {
                        if(currentStr.startsWith("Disk")){
                            String[] rawData = currentStr.split("\\s+");
                            diskList.add(rawData[1].substring(0, rawData[1].length()-1));
                        }
                    }
                }
            }

            //以Json格式存数据
            String smartDiskInfoCmd = "smartctl -i ";
            String smartDataSampleCmd = "smartctl -A ";
            for(String currentDiskName: diskList){
                JSONObject currentDiskData = new JSONObject();
                {
                    List<String> cmdResult = new CmdExecutor(smartDiskInfoCmd + currentDiskName).cmdResult;
                    for (int i = 0; i < 4; i++) {
                        cmdResult.remove(0);
                    }
                    cmdResult.remove(cmdResult.size() - 1);
                    for(String currentOutput: cmdResult){
                        String[] rawData = currentOutput.split(":\\s+");
                        currentDiskData.put(rawData[0],rawData[1]);
                    }
                }

                String serialNumber = currentDiskData.getString("Serial Number");
                if(!diskData.containsKey(serialNumber)){
                    diskData.put(serialNumber,currentDiskData);
                    JSONObject smartData = new JSONObject();
                    {
                        List<String> cmdResult = new CmdExecutor(smartDataSampleCmd + currentDiskName).cmdResult;
                        for (int i = 0; i < 7; i++) {
                            cmdResult.remove(0);
                        }
                        for(String currentOutput: cmdResult){
                            if(!currentOutput.equals("")){
                                currentOutput = currentOutput.trim();
                                String[] rawData = currentOutput.split("\\s+");
                                JSONObject currentSmart = new JSONObject();
                                currentSmart.put("ATTRIBUTE_NAME",rawData[1]);
                                currentSmart.put("VALUE",rawData[3]);
                                currentSmart.put("RAW_VALUE",rawData[9]);
                                smartData.put(rawData[0],currentSmart);
                            }
                        }
                        currentDiskData.put("SmartData",smartData);
                    }
                }
            }
        }
        System.out.println(diskData.toString());

        //获取当前时间:
        String currentDate = "";
        String pt_d = "";
        {
            Date date = new Date();
            {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy/MM/dd HH:mm");
                currentDate = sdf.format(date);
            }
            {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyyMMdd");
                pt_d = sdf.format(date);
            }
        }

        //写文件
        String sampleDataFilePath=System.getProperty("user.dir")+"/DiskPredict/client/data.csv";
        CsvWriter csvWriter = new CsvWriter(sampleDataFilePath,',', Charset.forName("GBK"));
        try {
            //Smart属性个数
            int smartCount = 256;
            //获取表头
            List<String> headers = new ArrayList<>();
            {
                String[] staticHeaders = {"date", "serial_number", "model", "serialAlternative", "failure", "is_ssd", "pt_d"};
                for(String staticHeader:staticHeaders){
                    headers.add(staticHeader);
                }

                String[] smartTagAttributes = {"_normalized","_raw"};
                for(int i=0;i<smartCount;i++){
                    for(String attribute: smartTagAttributes){
                        headers.add("smart_"+ Integer.toString(i) + attribute);
                    }
                }
            }

            //写入header头
            csvWriter.writeRecord(headers.toArray(new String[0]));

            //写入数据
            Set<String> serialNumberList = diskData.keySet();
            for(String serialNumber:serialNumberList){
                JSONObject currentDiskData = diskData.getJSONObject(serialNumber);
                List<String> rowData = new ArrayList<>();
                {
                    rowData.add(currentDate);
                    rowData.add(serialNumber);
                    rowData.add(currentDiskData.getString("Device Model"));
                    rowData.add("");
                    rowData.add("0");
                    rowData.add("0");
                    rowData.add(pt_d);
                    JSONObject smartData = currentDiskData.getJSONObject("SmartData");
                    //@Todo 0到255
                    for(int i=0;i<smartCount;i++){
                        JSONObject currentSmart = smartData.getJSONObject(Integer.toString(i));
                        if(currentSmart != null){
                            rowData.add(currentSmart.getString("VALUE"));
                            rowData.add(currentSmart.getString("RAW_VALUE"));
                        }
                        else{
                            rowData.add("");
                            rowData.add("");
                        }
                    }
                }
                csvWriter.writeRecord(rowData.toArray(new String[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }
    }
}
