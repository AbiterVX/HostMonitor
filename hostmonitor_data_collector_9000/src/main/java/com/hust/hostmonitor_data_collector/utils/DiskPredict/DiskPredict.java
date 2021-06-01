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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiskPredict {
    private static final String userDirPath = System.getProperty("user.dir");
    private static final String root_path = "\"" + userDirPath + "/DiskPredict" +"\"";
    private static final String diskPredictModulePath = userDirPath + "/DiskPredict/DiskPredictModule";
    private static final String trainDataPath = userDirPath + "/DiskPredict/train_data/";

    //-----对外接口
    //模型训练
    public static List<DiskPredictProgress> ModelTraining(String filePath, ModelTrainingParam param, DiskPredictProgress diskPredictProgress){
        List<DiskPredictProgress> progressList = new ArrayList<>();
        DiskPredictProgress dataPreProcessProgress = new DiskPredictProgress();
        DiskPredictProgress getTrainDataProgress = new DiskPredictProgress();
        progressList.add(dataPreProcessProgress);
        progressList.add(getTrainDataProgress);

        DataPreProcess(filePath,param.replace,dataPreProcessProgress);
        GetTrainData(filePath,param.scale, param.verifySize,getTrainDataProgress);

        List<String> modelNameList = new ArrayList<>();
        File file = new File(trainDataPath+filePath);
        for (File currentFile: Objects.requireNonNull(file.listFiles())){
            if(currentFile.isDirectory()){
                modelNameList.add(currentFile.getName());
            }
        }
        for(String modelName: modelNameList){
            DiskPredictProgress newTrainProgress = new DiskPredictProgress();
            Train(filePath,modelName,param.trainParams,newTrainProgress);
            progressList.add(newTrainProgress);
        }

        return progressList;
    }

    //故障预测
    public static DiskPredictProgress diskPredict(String filePath){
        DiskPredictProgress progress = new DiskPredictProgress();
        Predict(filePath,progress);
        return progress;
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
    public static void Predict(String filePath,DiskPredictProgress progress){
        // 需要检查参数正确性
        System.out.println("[JAVA--> ] Predict:");
        JSONObject param = new JSONObject();
        param.put("\"file_path\"", filePath);
        param.put("\"root_path\"", root_path);
        JavaExePython.execPython(diskPredictModulePath + "/disk_predict.py", param, progress);
        System.out.println("[JAVA--> ] Complete.\n");
    }
}
