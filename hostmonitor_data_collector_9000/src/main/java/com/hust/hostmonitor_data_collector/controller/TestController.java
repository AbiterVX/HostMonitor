package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.service.testService;
import com.hust.hostmonitor_data_collector.utils.DispersedConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/vue_web")
public class TestController {
    private DispersedConfig dispersedConfig = DispersedConfig.getInstance();
    private JSONObject vueWebConfig = dispersedConfig.getVueWebConfig();


    @GetMapping(value="/homepage/getSummary")
    @ResponseBody
    public String getSummary_Dashboard(){
        String result = vueWebConfig.getJSONObject("homeSummary").toString();
        return result;
    }

}
