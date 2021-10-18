package com.hust.hostmonitor_data_collector.utils.DiskPredict;


import com.alibaba.fastjson.JSONObject;

public class DiskPredictProgress {
    public static final int trainTotalTaskCount = 7;
    //已完成的主要任务数
    int completedTaskCount;
    //总共需完成的主要任务数
    int totalTaskCount;
    //整体是否完成（主要任务占整体的大部分，主要任务完成时可能还需进行少量的后续处理）
    boolean finished;
    String resultData;
    public DiskPredictProgress(){
        finished = false;
    }
    public void setCurrentProgress(int _completedTaskCount,int _totalTaskCount){
        completedTaskCount = _completedTaskCount;
        totalTaskCount = _totalTaskCount;
    }
    public float getProgressPercentage(){
        if(totalTaskCount==0){
            return 0;
        }
        else if(completedTaskCount == totalTaskCount){
            return 100;
        }
        else{
            float result = (float)completedTaskCount/(float)totalTaskCount*100;
            result = (float)(Math.round(result*100))/100;
            return result;
        }
    }

    //简单判断主要任务完成数是否等于总主要任务数
    public boolean isTaskCountEqual(){
        return (completedTaskCount==totalTaskCount);
    }

    public void setFinished(){
        finished = true;
    }

    //整体是否全部完成，包括主要任务
    public boolean isFinished() {
        return finished;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    public int getTotalTaskCount() {
        return totalTaskCount;
    }

    public static JSONObject parsingTrainResultData(String tempResultData){
        JSONObject reuslt = new JSONObject();
        String[] pairs = tempResultData.split(",");
        for(String currentPair:pairs){
            String[] pairData = currentPair.split(":");
            reuslt.put(pairData[0],pairData[1]);
        }
        return reuslt;
    }
}
