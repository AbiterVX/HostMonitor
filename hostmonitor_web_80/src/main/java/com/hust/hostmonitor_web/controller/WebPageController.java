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
}
