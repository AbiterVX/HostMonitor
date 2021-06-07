package com.hust.hostmonitor_data_collector.utils.DiskPredict;



public class DiskPredictProgress {
    int completedTaskCount;
    int totalTaskCount;
    boolean finished;
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

    public void setFinished(){
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }
}
