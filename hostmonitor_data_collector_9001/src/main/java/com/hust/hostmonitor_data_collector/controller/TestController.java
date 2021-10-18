package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.utils.DispersedConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
