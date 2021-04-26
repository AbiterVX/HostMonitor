package com.hust.hostmonitor_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.util.Map;


@RestController
public class DataSampleController_Remote {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/remote_test/{id}")
    public String remote_test(@PathVariable("id") Long id){
        return restTemplate.getForObject(dataCollectorUrl+"/test/"+id,String.class);
    }


    //RestTemplate
    @Resource
    private RestTemplate restTemplate;

    //读取配置文件字段:data_collector_9000 URL
    @Value("${service-url.data_collector_service}")
    private String dataCollectorUrl;



    //获取Host状态
    @GetMapping(value="/getHostState")
    @ResponseBody
    public String getHostState(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostState",String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //获取Host IP
    @GetMapping(value = "/getHostIp")
    @ResponseBody
    public String getHostIp(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostIp",String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //获取Host 硬件 数据
    @GetMapping(value = "/getHostHardWareInfo")
    public String getHostHardwareInfo(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostHardWareInfo",String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //----------获取Host 数据----------
    //获取Host 数据
    @GetMapping(value = "/getHostInfoRealTime")
    public String getHostInfoRealTime(){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostInfoRealTime",String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //获取Host 数据 近段时间内
    @GetMapping(value = "/getHostInfoRecent/{index}/{dateInterval}")
    public String getHostInfoRecent(@PathVariable Map<String,String> map){
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostInfoRecent/" + map.get("index") + "/" + map.get("dateInterval"),String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //获取Host 数据 某一类别
    @GetMapping(value="/getHostInfoField/{index}/{dateInterval}/{field}")
    public String getHostInfoField(@PathVariable Map<String,String> map) {
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostInfoRecent/" + map.get("index") + "/" + map.get("dateInterval") + "/" + map.get("field"),String.class);
        System.out.println("【请求】:"+result);
        return result;
    }

    //获取Host 进程 数据 实时
    @GetMapping(value="/getHostProcessInfoRealTime/{index}")
    public String getHostProcessInfoRealTime(@PathVariable Map<String,String> map) {
        String result= restTemplate.getForObject(dataCollectorUrl+ "/getHostProcessInfoRealTime/" + map.get("index") ,String.class);
        System.out.println("【请求】:"+result);
        return result;
    }
}

    /*
    // get方法，路径传参
    @GetMapping(value = "/getHostInfoList/Recent/{index}/{dateInterval}")
    public String getHostInfoList_Recent(@PathVariable Map<String,String> map){
        String index = map.get("index");
        String dateInterval = map.get("dateInterval");
        String result ="index:" + index + "dateInterval" + dateInterval;
        System.out.println("Variable:"+result);

        //, HttpSession session
        //session.setAttribute("index",index);

        return "redirect:/Test/11/88";
    }
    */