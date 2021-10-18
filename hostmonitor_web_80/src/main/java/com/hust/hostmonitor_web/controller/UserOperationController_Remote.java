package com.hust.hostmonitor_web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class UserOperationController_Remote {
    //RestTemplate
    @Resource
    private RestTemplate restTemplate;

    //读取配置文件字段:data_collector_9000 URL
    @Value("${service-url.data_collector_service}")
    private String dataCollectorUrl;

    @PostMapping(value = "/SignIn")
    @ResponseBody
    public String signIn(@RequestBody Map<String,String> params) {
        return restTemplate.postForObject(dataCollectorUrl + "/SignIn", params,String.class);
    }

    @PostMapping(value = "/SignUp")
    @ResponseBody
    public String signUp(@RequestBody Map<String,String> params) {
        return restTemplate.postForObject(dataCollectorUrl + "/SignUp", params,String.class);
    }

    @PostMapping(value = "/updateUserInfo")
    @ResponseBody
    public String updateUserInfo(@RequestBody Map<String,String> params) {
        return restTemplate.postForObject(dataCollectorUrl + "/updateUserInfo", params,String.class);
    }

    @PostMapping(value = "/updateUserPassword")
    @ResponseBody
    public String updateUserPassword(@RequestBody Map<String,String> params) {
        return restTemplate.postForObject(dataCollectorUrl + "/updateUserPassword", params,String.class);
    }

    @GetMapping(value = "/getUsers")
    @ResponseBody
    public String getUsers(){
        return restTemplate.getForObject(dataCollectorUrl + "/getUsers",String.class);
    }


    @GetMapping(value = "/SystemSetting/Get")
    @ResponseBody
    public String getSystemSetting() {
        return restTemplate.getForObject(dataCollectorUrl + "/SystemSetting/Get",String.class);
    }

    @PostMapping(value = "/SystemSetting/Reset")
    @ResponseBody
    public String resetSystemSetting(@RequestBody Map<String,String> params) {
        return restTemplate.postForObject(dataCollectorUrl + "/SystemSetting/Reset", params,String.class);
    }
}
