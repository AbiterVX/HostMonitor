package com.hust.hostmonitor_data_collector.dao.entity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StatisRecord {
    public Double FDR;
    public Double FAR;
    public Double AUC;
    public Double FNR;
    public Double Accuracy;
    public Double Precision;
    public Double Specificity;
    public Double ErrorRate;
    public StatisRecord(Double FDR, Double FAR, Double AUC, Double FNR, Double accuracy, Double precision, Double specificity, Double errorRate) {
        this.FDR = FDR;
        this.FAR = FAR;
        this.AUC = AUC;
        this.FNR = FNR;
        Accuracy = accuracy;
        Precision = precision;
        Specificity = specificity;
        ErrorRate = errorRate;
    }
    public StatisRecord(Float FDR, Float FAR, Float AUC, Float FNR, Float accuracy, Float precision, Float specificity, Float errorRate) {
        this.FDR = FDR.doubleValue();
        this.FAR = FAR.doubleValue();
        this.AUC = AUC.doubleValue();
        this.FNR = FNR.doubleValue();
        Accuracy = accuracy.doubleValue();
        Precision = precision.doubleValue();
        Specificity = specificity.doubleValue();
        ErrorRate = errorRate.doubleValue();
    }

}
