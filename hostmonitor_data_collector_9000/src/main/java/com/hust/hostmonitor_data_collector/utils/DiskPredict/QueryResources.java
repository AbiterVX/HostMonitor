package com.hust.hostmonitor_data_collector.utils.DiskPredict;

import com.hust.hostmonitor_data_collector.service.HostInfoFieldType;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QueryResources {
   static {
      modelToManufacturer=new HashMap<>();
      //从静态文件里面读取进来

   }
   public static String[] modelNames={
           "RandomForest"
   };
   public static String[] CNmodelNames={
           "随机森林"
   };
   public static HashMap<String,String> modelToManufacturer;

   //暂时这么写
   public static int queryDiskIndex(String diskSerial){
      if(diskSerial.substring(0,3).toLowerCase().contains("wdc")||diskSerial.substring(0,2).toLowerCase().contains("wd")){
         return 0;
      }
      else if(diskSerial.substring(0,3).toLowerCase().contains("sea")){
         return 1;
      }
      else if(diskSerial.substring(0,3).toLowerCase().contains("tos")){
         return 2;
      }
      else if(diskSerial.substring(0,3).toLowerCase().contains("sum")){
         return 3;
      }
      else {
         return 4;
      }
   }
}
