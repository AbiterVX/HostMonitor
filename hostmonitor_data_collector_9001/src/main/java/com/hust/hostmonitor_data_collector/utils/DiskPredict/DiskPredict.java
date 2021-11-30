package com.hust.hostmonitor_data_collector.utils.DiskPredict;

/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-05-24 20:33:55
 * @LastEditors: WanJu
 * @LastEditTime: 2021-05-26 11:52:56
 */

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DiskPredict {
    private static final String userDirPath = System.getProperty("user.dir");
    private static final String root_path = "\"" + userDirPath + "/DiskPredict" +"\"";
    private static final String diskPredictModulePath = userDirPath + "/DiskPredict/DiskPredictModule";
    private static final String trainDataPath = userDirPath + "/DiskPredict/train_data/";
    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
    //-----对外接口

    public static DiskPredictProgress preprocess(String filePath, int replace){
        DiskPredictProgress progress = new DiskPredictProgress();
        Thread thread = new Thread(() -> {
            DataPreProcess("\""+filePath+"\"",replace,progress);
            progress.setFinished();
        });
        thread.start();
        return progress;
    }

    public static DiskPredictProgress getTrainData(String filePath, float scale, float verifySize){
        DiskPredictProgress progress = new DiskPredictProgress();
        Thread thread = new Thread(() -> {
            GetTrainData("\""+filePath+"\"",scale,verifySize,progress);
            progress.setFinished();
        });
        thread.start();
        return progress;
    }

    public static List<DiskPredictProgress> train(String filePath,JSONObject params){
        List<DiskPredictProgress> progressList = new ArrayList<>();
        List<String> modelNameList = new ArrayList<>();
        File file = new File(trainDataPath+"/"+filePath);
        for (File currentFile: file.listFiles()){
            if(currentFile.isDirectory()){
                modelNameList.add(currentFile.getName());
                progressList.add(new DiskPredictProgress());
            }
        }
        Thread thread = new Thread(() -> {
            for(int i=0;i<modelNameList.size();i++){
                Train("\""+filePath+"\"","\""+modelNameList.get(i)+"\"",params,progressList.get(i));
                progressList.get(i).setFinished();
            }
        });
        thread.start();
        return progressList;
    }

    public static DiskPredictProgress predict(String filePath,String fileName){
        Thread thread = new Thread(() -> {
            Predict("\""+filePath+"\"","\""+fileName+"\"",null);
        });
        thread.start();
        return null;
    }
    public static void predictWithoutProgess(String filePath,String fileName){
        Predict("\""+filePath+"\"","\""+fileName+"\"",null);
    }

    //数据预处理
    public static void DataPreProcess(String filePath, int replace,DiskPredictProgress progress){
        // 需要检查参数正确性
        System.out.println("[JAVA--> ] DataPreProcess:");
        JSONObject param = new JSONObject();
        param.put("\"file_path\"", filePath);
        param.put("\"replace\"", replace);
        param.put("\"root_path\"", root_path);
        JavaExePython.execPython(diskPredictModulePath + "/preprocess.py", param, progress);
        System.out.println("[JAVA--> ] Complete.\n");
    }

    //生成模型训练数据
    public static void GetTrainData(String filePath, float scale, float verifySize,DiskPredictProgress progress){
        // 需要检查参数正确性
        System.out.println("[JAVA--> ] GetTrainData:");
        JSONObject param = new JSONObject();
        param.put("\"file_path\"", filePath);
        param.put("\"scale\"", scale);
        param.put("\"verifySize\"", verifySize);
        param.put("\"root_path\"", root_path);
        JavaExePython.execPython(diskPredictModulePath + "/get_data.py", param, progress);
        System.out.println("[JAVA--> ] Complete.\n");
    }

    //模型训练
    public static void Train(String filePath, String model, JSONObject params,DiskPredictProgress progress){
        // 需要检查参数正确性
        System.out.println("[JAVA--> ] Train:");
        JSONObject param = new JSONObject();
        param.put("\"file_path\"", filePath);
        param.put("\"model\"", model);
        param.put("\"params\"", params.toJSONString());
        param.put("\"root_path\"", root_path);
        JavaExePython.execPython(diskPredictModulePath + "/traditional.py", param, progress);
        System.out.println("[JAVA--> ] Complete.\n");
    }

    //故障预测
    public static void Predict(String filePath,String fileName,DiskPredictProgress progress){
        // 需要检查参数正确性
        System.out.println("[JAVA--> ] Predict:");
        JSONObject param = new JSONObject();
        param.put("\"file_path\"", filePath);
        param.put("\"root_path\"", root_path);
        param.put("\"file_name\"", fileName);
        JavaExePython.execPython(diskPredictModulePath + "/disk_predict.py", param, progress);
        System.out.println("[JAVA--> ] Complete.\n");
    }
    public static List<JSONObject> getDiskPredictResult(String readFileName){
        JSONObject jsonObject;
        ArrayList<JSONObject> result=new ArrayList<>();

            String projectPath = System.getProperty("user.dir");
            try {
                CsvReader reader = new CsvReader(projectPath + readFileName, ',', StandardCharsets.UTF_8);
                reader.readHeaders();
                while (reader.readRecord()) {
                    jsonObject=new JSONObject();
                    String[] currentRow = reader.getValues();
                    jsonObject.put("diskSerial",currentRow[1]);
                    jsonObject.put("modelName",currentRow[5]);
                    try {

                        jsonObject.put("timestamp",new Timestamp(sdf.parse(currentRow[0]).getTime()));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        jsonObject.put("timestamp",new Timestamp(System.currentTimeMillis()));
                    }
                    //jsonObject.put("timestamp",new Timestamp(System.currentTimeMillis()));
                    jsonObject.put("predictProbability",Float.parseFloat(currentRow[4]));
                    result.add(jsonObject);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Yesterday has no update records,so the diskPredict() will do nothing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        return result;
    }

    public static long getRecordTime(String readFileName,String hostName,String diskSerial){
        String projectPath = System.getProperty("user.dir");
        try {
            CsvReader reader = new CsvReader(projectPath + readFileName, ',', StandardCharsets.UTF_8);
            reader.readHeaders();
            while (reader.readRecord()) {

                String[] currentRow = reader.getValues();
                if(!diskSerial.equals(currentRow[1])){
                    continue;
                }
                try {
                    return sdf.parse(currentRow[0]).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean checkLatestRecordExists(String readFileName,String hostName,String diskSerial) {
        String projectPath = System.getProperty("user.dir");
        try {
            CsvReader reader = new CsvReader(projectPath + readFileName, ',', StandardCharsets.UTF_8);
            reader.readHeaders();
            while (reader.readRecord()) {
                String[] currentRow = reader.getValues();
                if(!diskSerial.equals(currentRow[1])){
                    return true;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void diskSampleDataIntegration(String integratedFileName,String newFile,String remoteIp){
        String projectPath = System.getProperty("user.dir");
        HashSet<String> rows=new HashSet<>();
        try {
            FileReader integratedFileReader=new FileReader(integratedFileName);
            BufferedReader ibr=new BufferedReader(integratedFileReader);
            String records=null;
            while((records=ibr.readLine())!=null){
                String[] tokens=records.split(",");
                rows.add(tokens[0]+":"+tokens[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter writer;
            writer = new FileWriter(integratedFileName,true);
            FileReader reader;
            reader = new FileReader(newFile);
            BufferedReader br=new BufferedReader(reader);
            boolean firstline=true;
            String str;
            while((str=br.readLine())!=null){
                if(firstline){
                    firstline=false;
                    continue;
                }
                String[] rowTokens=str.split(",");
                if(!rows.contains(rowTokens[0]+":"+rowTokens[1])) {

                    int thirdComma=str.indexOf(",");
                    System.out.println(thirdComma);
                    thirdComma=str.indexOf(",",thirdComma+1);
                    System.out.println(thirdComma);
                    thirdComma=str.indexOf(",",thirdComma+1);
                    System.out.println(thirdComma);
                    String partOne=str.substring(0,thirdComma+1);
                    String partTwo=str.substring(thirdComma+1,str.length());
                    System.out.println(partOne);
                    System.out.println(remoteIp);
                    System.out.println(partTwo);
                    str=partOne+remoteIp+partTwo;
                    rows.add(rowTokens[0]+":"+rowTokens[1]);
                    writer.write(str + "\n");
                }
            }
            writer.flush();
            writer.close();
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addRemoteIp(String integratedFileName,String newFile, String remoteIp) {
        try {
            FileWriter writer;
            writer = new FileWriter(integratedFileName,true);
            FileReader reader;
            reader = new FileReader(newFile);
            BufferedReader br=new BufferedReader(reader);
            boolean firstline=true;
            String str;
            while((str=br.readLine())!=null){
                if(firstline){
                    firstline=false;
                    writer.write(str+"\n");
                    continue;
                }
                int thirdComma=str.indexOf(",");
                System.out.println("-----"+thirdComma);
                thirdComma=str.indexOf(",",thirdComma+1);
                System.out.println("-----"+thirdComma);
                thirdComma=str.indexOf(",",thirdComma+1);
                System.out.println("-----"+thirdComma);
                String partOne=str.substring(0,thirdComma+1);
                String partTwo=str.substring(thirdComma+1,str.length());
                str=partOne+remoteIp+partTwo;
                writer.write(str + "\n");
            }
            writer.flush();
            writer.close();
            br.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
