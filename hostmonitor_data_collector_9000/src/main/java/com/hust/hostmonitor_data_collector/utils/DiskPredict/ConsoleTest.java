/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-05-24 20:34:44
 * @LastEditors: WanJu
 * @LastEditTime: 2021-05-26 17:25:25
 */
package com.hust.hostmonitor_data_collector.utils.DiskPredict;
import java.io.File;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSONObject;

public class ConsoleTest {
    public static void main(String[] args) {

        String cmd = "";
        Scanner input = new Scanner(System.in);
        while (!cmd.equalsIgnoreCase("exit")) {
            System.out.print("1: DataPreProcess\n" +
                                "2: GetTrainData\n" + 
                                "3: Train\n" +
                                "4: Predict\n" +
                                ">>> ");
            cmd = input.next();
            if (!cmd.matches("[0-9]+")) {
                continue;
            }
            switch (Integer.valueOf(cmd)){
                case 1:
                    DiskPredict.DataPreProcess("\"2016\"", 0,null);
                    break;
                case 2:
                    DiskPredict.GetTrainData("\"2016\"", 1.0f/3, 0.1f,null);
                    break;
                case 3:
                    JSONObject params = new JSONObject();
                    params.put("max_depth", new int[]{10, 20, 30});
                    params.put("max_features", new int[]{4, 7, 10});
                    params.put("n_estimators", new int[]{10, 20, 30, 40});
                    
                    DiskPredict.Train("\"2016\"", "\"ST4000DM000\"", params,null);
                    break;
                case 4:
                    DiskPredict.Predict("\""+ System.getProperty("user.dir") + "/DiskPredict/original_data/2021/6" +"\"","\""+"PC202011261410-2021-06-07.csv"+"\"",null);
                    break;
                case 5:
                    //DiskPredictProgress progress = DiskPredict.preprocess("2016", 0);
                    //DiskPredictProgress progress = DiskPredict.getTrainData("2016", 1.0f/3, 0.1f);
                    /*Thread thread = new Thread(() -> {
                        try {
                            float progressPercentage = 0;
                            while(true){
                                progressPercentage = progress.getProgressPercentage();
                                System.out.println("[进度]："+progressPercentage+"% , ");
                                if(progress.isFinished()){
                                    break;
                                }
                                Thread.sleep(2000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();*/

                    //模型参数
                    JSONObject trainParams = new JSONObject();
                    trainParams.put("max_depth", new int[]{10, 20, 30});
                    trainParams.put("max_features", new int[]{4, 7, 10});
                    trainParams.put("n_estimators", new int[]{10, 20, 30, 40});


                    try {
                        List<DiskPredictProgress> progressList = DiskPredict.train("2016", trainParams);
                        Thread thread = new Thread(() -> {
                            try {
                                int sumProgressCount = progressList.size();
                                int currentIndex = 0;
                                while(currentIndex!=sumProgressCount){
                                    float currentPercentage = progressList.get(currentIndex).getProgressPercentage();
                                    System.out.println("[进度]："+"["+(currentIndex+1)+"/"+sumProgressCount +"] "+currentPercentage+"%");
                                    if(progressList.get(currentIndex).isFinished()){
                                        System.out.println("[结果]：" + progressList.get(currentIndex).getResultData());
                                        currentIndex+=1;
                                    }
                                    else{
                                        Thread.sleep(2000);
                                    }

                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    //DiskPredictProgress progress = DiskPredict.predict(System.getProperty("user.dir") + "/DiskPredict/predict_data/2021/5");
                default:
                    System.out.println("没有该函数！");
                    break;
            }
        }
        input.close();
    }
}