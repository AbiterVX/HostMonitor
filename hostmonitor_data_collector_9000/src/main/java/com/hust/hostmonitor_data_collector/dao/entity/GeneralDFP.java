package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public interface GeneralDFP {

    public String diskSerial = null;
    public String hostName = null;
    public Boolean isSSd = null;
    public Timestamp timestamp=null;
    public Double predictProbability = null;
    public String modelName=null;
}
