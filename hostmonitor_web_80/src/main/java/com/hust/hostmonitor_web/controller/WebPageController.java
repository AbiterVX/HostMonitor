package com.hust.hostmonitor_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class WebPageController {

    //index界面/主界面
    @GetMapping(value ="/")
    public String get_HomePage(){
        return "html/HomePage.html";
    }

    //index界面/主界面
    @GetMapping(value ="/HomePage")
    public String getHomePage(){
        return "html/HomePage.html";
    }

    //[界面]主机信息
    @GetMapping(value ="/HostInfo")
    public String getHostInfoPage(){
        return "html/HostInfo.html";
    }

    //[界面]进程IO信息
    @GetMapping(value ="/ProcessIOInfo")
    public String getProcessIOInfoPage(){
        return "html/ProcessIOInfo.html";
    }

    //[界面]磁盘故障预测
    @GetMapping(value ="/DiskFailurePredict")
    public String getDiskFailurePredictPage(){
        return "html/DiskFailurePredict.html";
    }

    //[界面]IO测试
    @GetMapping(value ="/IOTest")
    public String getIOTestPage(){
        return "html/IOTest.html";
    }

    //[界面]IO测试
    @GetMapping(value ="/Test")
    public String getTestPage(@RequestParam(value = "id",required = false,defaultValue = "0") int id,
                              HttpSession session){
        System.out.println("id:"+id);
        session.setAttribute("index","111");
        session.setAttribute("dateInterval","222");


        return "html/Test.html";
    }

}
