package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class TrainInfo {
    public Timestamp timestamp;
    public int PredictModel;
    public String DiskModel;
    public float FDR;
    public float FAR;
    public float AUC;
    public float FNR;
    public float Accuracy;
    public float Precision;
    public float Specificity;
    public float ErrorRate;
    public String Parameters;
    public String OperatorID;

    public TrainInfo(){}
    public TrainInfo(Timestamp timestamp, int predictModel, String diskModel, float FDR, float FAR, float AUC, float FNR, float accuracy, float precision, float specificity, float errorRate, String parameters, String operatorID) {
        this.timestamp = timestamp;
        PredictModel = predictModel;
        DiskModel = diskModel;
        this.FDR = FDR;
        this.FAR = FAR;
        this.AUC = AUC;
        this.FNR = FNR;
        Accuracy = accuracy;
        Precision = precision;
        Specificity = specificity;
        ErrorRate = errorRate;
        Parameters = parameters;
        OperatorID = operatorID;
    }

    @Override
    public String toString() {
        return '{'+
                "\"timestamp\":" + timestamp +
                ",\"PredictModel\":" + PredictModel +
                ",\"DiskModel\":\"" + DiskModel + '\"' +
                ",\"FDR\":" + FDR +
                ",\"FAR\":" + FAR +
                ",\"AUC\":" + AUC +
                ",\"FNR\":" + FNR +
                ",\"Accuracy\":" + Accuracy +
                ",\"Precision\":" + Precision +
                ",\"Specificity\":" + Specificity +
                ",\"ErrorRate\":" + ErrorRate +
                ",\"Parameters\":\"" + Parameters + '\"' +
                ",\"OperatorID\":\"" + OperatorID + '\"' +
                '}';
    }
}
