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

    //登录界面
    @GetMapping(value ="/Signin")
    public String getSignInPage(){
        return "html/Signin.html";
    }

    //登录界面
    @GetMapping(value ="/Signup")
    public String getSignupPage(){
        return "html/Signup.html";
    }

    //用户界面
    @GetMapping(value ="/UserSpace")
    public String getUserSpacePage(){
        return "html/UserSpace.html";
    }
}
