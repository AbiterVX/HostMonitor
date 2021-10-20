package com.hust.hostmonitor_data_collector.dao.provider;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import org.python.antlr.ast.Str;

public class ProcessProvider {
    public int dataSourceSelect= ConfigDataManager.getInstance().getConfigJson().getInteger("DataSourceSelect");
    public String insertProcessRecord(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="insert into ProcessData values(" +
                    "#{ip},#{timestamp},#{pid},#{uid},#{readKbps},#{writeKbps},#{command})";
        }
        else if (dataSourceSelect==1){
            SQL="insert into storagedevicemonitor.ProcessData values(" +
                    "#{ip},#{timestamp},#{pid},#{uid},#{readKbps},#{writeKbps},#{command})";
        }
        return SQL;
    }

}
