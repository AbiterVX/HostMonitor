package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import com.hust.hostmonitor_data_collector.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class UserOperationController {
    @Resource
    UserService userService;

    @PostMapping(value = "/SignUp")
    @ResponseBody
    public String signUp(@RequestBody Map<String,String> params) {
        String userName = params.get("userName");
        String password = params.get("password");
        System.out.println("[注册] "+params);
        if(!userName.equals("") && !password.equals("")){
            return userService.signUp(userName,password);
        }
        else{
            return "";
        }
    }

    @PostMapping(value = "/SignIn")
    @ResponseBody
    public SystemUser signIn(@RequestBody Map<String,String> params) {
        String userID = params.get("userID");
        String password = params.get("password");
        SystemUser user = userService.signIn(userID,password);
        System.out.println("[SignIn]: "+userID+","+ password + "  :  "+user);
        return user;
    }

    @GetMapping(value = "/getUsers")
    @ResponseBody
    public List<SystemUser> getUsers(){
        return userService.getUsers();
    }

    @PostMapping(value = "/updateUserInfo")
    @ResponseBody
    public String updateUserInfo(@RequestBody Map<String,String> params) {
        System.out.println(params);

        String userName = params.get("userName");
        int userType = Integer.parseInt(params.get("userType"));
        int validState = Boolean.parseBoolean(params.get("validState"))?1:0;
        String phone = params.get("phone");
        String email = params.get("email");
        int phoneValidState = Boolean.parseBoolean(params.get("phoneValidState"))?1:0;
        int emailValidState = Boolean.parseBoolean(params.get("emailValidState"))?1:0;
        String userID = params.get("userID");
        String operateUserID = params.get("operateUserID");
        String operateUserPassword = params.get("operateUserPassword");
        userService.updateUserInfo(operateUserID,operateUserPassword,userName,userType,validState,phone,email,phoneValidState,emailValidState,userID);
        return "Complete";
    }

    @PostMapping(value = "/updateUserPassword")
    @ResponseBody
    public String updateUserPassword(@RequestBody Map<String,String> params) {
        String userID = params.get("userID");
        String password = params.get("password");
        String newPassword = params.get("newPassword");
        System.out.println("[更新密码]:"+params);
        userService.updateUserPassword(userID,password,newPassword);
        return "";
    }

    /*
    @GetMapping(value = "/sendSMS/{phoneNumber}/{diskName}")
    @ResponseBody
    public String testSendSMS(@PathVariable Map<String,String> map) {
        String phoneNumber = map.get("phoneNumber");
        String diskName = map.get("diskName");
        return userService.sendSMS(phoneNumber,diskName);
    }*/


    @GetMapping(value = "/Email/{emailAddress}")
    @ResponseBody
    public String testSendEmail(@PathVariable Map<String,String> map) {
        String emailAddress = map.get("emailAddress");
        userService.sendEmail(emailAddress);
        return "";
    }


    @GetMapping(value = "/SystemSetting/Get")
    @ResponseBody
    public String getSystemSetting() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ReportTiming",false);
        jsonObject.put("ReportTimingInterval",0);
        jsonObject.put("ReportEmergency",true);
        jsonObject.put("ReportFailureRateThreshold",70);

        jsonObject.put("BackupTiming",false);
        jsonObject.put("BackupTimingInterval",10);
        jsonObject.put("BackupEmergency",true);
        jsonObject.put("BackupFailureRateThreshold",90);

        return jsonObject.toJSONString();
    }

    @PostMapping(value = "/SystemSetting/Reset")
    @ResponseBody
    public String resetSystemSetting(@RequestBody Map<String,String> params) {
        boolean ReportTiming = Boolean.parseBoolean(params.get("ReportTiming"));
        int ReportTimingInterval = Integer.parseInt(params.get("ReportTimingInterval"));
        boolean ReportEmergency =  Boolean.parseBoolean(params.get("ReportEmergency"));
        int ReportFailureRateThreshold = Integer.parseInt(params.get("ReportFailureRateThreshold"));

        boolean BackupTiming =  Boolean.parseBoolean(params.get("BackupTiming"));
        int BackupTimingInterval = Integer.parseInt(params.get("BackupTimingInterval"));
        boolean BackupEmergency =  Boolean.parseBoolean(params.get("BackupEmergency"));
        int BackupFailureRateThreshold = Integer.parseInt(params.get("BackupFailureRateThreshold"));

        System.out.println("[SystemSetting] "+params);
        return "Complete";
    }

}
