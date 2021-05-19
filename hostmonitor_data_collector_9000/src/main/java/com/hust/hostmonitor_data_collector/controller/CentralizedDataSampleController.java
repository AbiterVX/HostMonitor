package com.hust.hostmonitor_data_collector.controller;

import com.hust.hostmonitor_data_collector.service.CentralizedDataService;
import com.hust.hostmonitor_data_collector.service.HostInfoFieldType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


@RestController
@RequestMapping("/Centralized")
public class CentralizedDataSampleController {
    //@Autowired
    //Service_Implementation service_implementation;

    @Resource
    CentralizedDataService centralizedDataService;


    //当前端口
    @Value("${server.port}")
    private String serverPort;

    //用于测试
    @GetMapping(value = "/test/{id}")
    public String forTest(@PathVariable("id") Integer id)
    {
        return "nacos registry, serverPort: "+ serverPort+"\t id"+id;
    }


    //获取Host状态
    @GetMapping(value="/getHostState")
    public String getHostState(){
        String result= centralizedDataService.getHostState();//service_implementation.getHostState();
        return result;
    }

    //获取Host IP
    @GetMapping(value = "/getHostIp")
    public String getHostIp(){
        String result = centralizedDataService.getHostIp();//service_implementation.getHostIpList();
        return result;
    }

    //获取Host 硬件 数据
    @GetMapping(value = "/getHostHardWareInfo")
    public String getHostHardwareInfo(){
        String result = centralizedDataService.getHostHardwareInfo();//service_implementation.getHostHardwareInfoListOutputData();
        return result;
    }


    //----------获取Host 数据----------
    //获取Host 数据 实时
    @GetMapping(value = "/getHostInfoRealTime")
    public String getHostInfoRealTime(){
        String result = centralizedDataService.getHostInfoRealTime();//service_implementation.getHostInfoListOutputData();
        return result;
    }

    //获取Host 数据 近段时间内
    @GetMapping(value = "/getHostInfoRecent/{index}/{dateInterval}")
    public String getHostInfoRecent(@PathVariable Map<String,String> map){
        int index= Integer.parseInt(map.get("index"));
        int minute= Integer.parseInt(map.get("dateInterval"));
        String result = centralizedDataService.getHostInfoRecent(index,minute);//service_implementation.getFullRecordsByIP(service_implementation.getHostIp(index),minute);
        return result;
    }

    //获取Host 数据 某一类别
    @GetMapping(value="/getHostInfoField/{index}/{dateInterval}/{field}")
    public String getHostInfoField(@PathVariable Map<String,String> map) {
        int index = Integer.parseInt(map.get("index"));
        int hour=Integer.parseInt(map.get("dateInterval"));
        String field = map.get("field");
        HostInfoFieldType hostInfoFieldType = HostInfoFieldType.fromString(field);
        String result = centralizedDataService.getHostInfoField(index,hour,hostInfoFieldType);//service_implementation.getRecentInfoByIp(service_implementation.getHostIp(index), hour, FieldType.fromString(field));
        return result;
    }


    //获取Host 进程 数据 实时
    @GetMapping(value="/getHostProcessInfoRealTime/{index}")
    public String getHostProcessInfoRealTime(@PathVariable Map<String,String> map) {
        int index = Integer.parseInt(map.get("index"));
        String result = centralizedDataService.getHostProcessInfoRealTime(index);
        return result;
    }
}
