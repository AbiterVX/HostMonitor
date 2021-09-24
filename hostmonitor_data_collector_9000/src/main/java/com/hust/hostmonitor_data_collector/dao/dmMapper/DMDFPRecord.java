package com.hust.hostmonitor_data_collector.dao.dmMapper;

import com.hust.hostmonitor_data_collector.dao.entity.DFPRecord;
import com.hust.hostmonitor_data_collector.dao.entity.GeneralDFP;

import java.sql.Timestamp;

public class DMDFPRecord implements GeneralDFP {

    public Byte isSSdBYte;
    public String diskSerial;
    public String hostName;
    public Boolean isSSd;
    public Timestamp timestamp;
    public Double predictProbability;
    public String modelName;

    public DMDFPRecord(String diskSerial, String hostName,Byte isSSdBYte,Timestamp timestamp, Double predictProbability, String modelName) {
        this.isSSdBYte = isSSdBYte;
        this.diskSerial = diskSerial;
        this.hostName = hostName;
        this.isSSd = (int)this.isSSdBYte>0? true:false;
        this.timestamp = timestamp;
        this.predictProbability = predictProbability;
        this.modelName = modelName;
    }


}
