package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class TrainInfo {
    public int id;
    public Timestamp timestamp;
    public String predictModel;
    public String diskModel;
    public double FDR;
    public double FAR;
    public double AUC;
    public double FNR;
    public double Accuracy;
    public double Precesion;
    public double Specificity;
    public double ErrorRate;
    public String Parameters;

    public TrainInfo(int id, Timestamp timestamp, String predictModel, String diskModel, double FDR, double FAR, double AUC, double FNR, double accuracy, double precesion, double specificity, double errorRate, String parameters) {
        this.id=id;
        this.timestamp = timestamp;
        this.predictModel = predictModel;
        this.diskModel = diskModel;
        this.FDR = FDR;
        this.FAR = FAR;
        this.AUC = AUC;
        this.FNR = FNR;
        Accuracy = accuracy;
        Precesion = precesion;
        Specificity = specificity;
        ErrorRate = errorRate;
        Parameters = parameters;
    }
}
