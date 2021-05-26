package com.hust.hostmonitor_data_collector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/Dispersed")
public class UserOperationController {


    @PostMapping(value = "/SignIn")
    @ResponseBody
    public String signIn(@RequestBody Map<String,String> params) {
        String UserID = params.get("UserID");
        String Password = params.get("Password");
        if(UserID.equals("hust") && Password.equals("hust")){
            return UserID;
        }
        else{
            return "null";
        }
    }


}
