package com.hust.hostmonitor_data_collector.controller;

import com.hust.hostmonitor_data_collector.service.DispersedDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class testController {
    @Resource
    DispersedDataServiceImpl dispersedDataService;
        @RequestMapping(value="/test/Dashboard")
    @ResponseBody
    public String getSummary_Dashboard(){
        String result= dispersedDataService.getHostInfoDashboardAll();
        //System.out.println("[请求][getHostState]:"+result);
        return result;
    }

}
