package com.hust.hostmonitor_data_collector.controller;

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
@RequestMapping("/Dispersed")
public class UserOperationController {
    @Resource
    UserService userService;

    @PostMapping(value = "/SignUp")
    @ResponseBody
    public String signUp(@RequestBody Map<String,String> params) {
        String userName = params.get("userName");
        String password = params.get("password");
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
}
