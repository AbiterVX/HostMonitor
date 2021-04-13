package com.hust.hostmonitor_web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebPageController {

    //index界面/主界面
    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String get_HomePage(){
        return "html/HomePage.html";
    }

    //index界面/主界面
    @RequestMapping(value ="/HomePage", method = RequestMethod.GET)
    public String getHomePage(){
        return "html/HomePage.html";
    }

    //[界面]主机信息
    @RequestMapping(value ="/HostInfo", method = RequestMethod.GET)
    public String getHostInfoPage(){
        return "html/HostInfo.html";
    }

    //[界面]进程IO信息
    @RequestMapping(value ="/ProcessIOInfo", method = RequestMethod.GET)
    public String getProcessIOInfoPage(){
        return "html/ProcessIOInfo.html";
    }

    //[界面]磁盘故障预测
    @RequestMapping(value ="/DiskFailurePredict", method = RequestMethod.GET)
    public String getDiskFailurePredictPage(){
        return "html/DiskFailurePredict.html";
    }

    //[界面]IO测试
    @RequestMapping(value ="/IOTest", method = RequestMethod.GET)
    public String getIOTestPage(){
        return "html/IOTest.html";
    }

}
