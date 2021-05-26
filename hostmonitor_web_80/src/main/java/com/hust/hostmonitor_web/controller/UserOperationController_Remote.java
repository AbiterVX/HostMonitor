package com.hust.hostmonitor_web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Dispersed")
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
        return restTemplate.postForObject(dataCollectorUrl + "/Dispersed/SignIn", params,String.class);
    }
}
