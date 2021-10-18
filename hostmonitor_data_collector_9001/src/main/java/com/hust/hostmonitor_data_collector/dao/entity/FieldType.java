package com.hust.hostmonitor_data_collector.dao.entity;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {
    RECEIVEBANDWIDTH("receiveBW"),TRANSMITBANDWIDTH("transmitBW"),CPUUSAGE("cpuUsage"),MEMORYUSAGE("memoryUsage"),DISKUSAGE("diskUsage"),TEMPERATURE("temp"),
    ENERGY("energy"),INPUTNUMBER("iNumber"),OUTPUTNUMBER("oNumber"),ALLFIELDS("allFields");
    private final String value;
    private final static Map<String,FieldType> FIELD_TYPE_MAP=new HashMap<String,FieldType>();
    private FieldType(String value){
        this.value=value;
    }
    public String value(){
        return value;
    }
    static{
        for(FieldType f:values()){
            FIELD_TYPE_MAP.put(f.value(),f);
        }
    }
    public static FieldType fromString(String f){
        FieldType fieldType=FIELD_TYPE_MAP.get(f);
        return fieldType==null? ALLFIELDS:fieldType;
    }

}
