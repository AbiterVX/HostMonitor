package com.hust.hostmonitor_data_collector.utils.DiskPredict;

public class DiskPredictProgress {
    int completedTaskCount;
    int totalTaskCount;
    public DiskPredictProgress(){
        completedTaskCount = 0;
        totalTaskCount = 0;
    }
    public DiskPredictProgress(int _totalTaskCount){
        completedTaskCount = 0;
        totalTaskCount = _totalTaskCount;
    }
    public float getProgressPercentage(){
        return (float)completedTaskCount/(float)totalTaskCount;
    }
}
