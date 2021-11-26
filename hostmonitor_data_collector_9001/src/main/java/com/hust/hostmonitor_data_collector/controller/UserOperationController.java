package com.hust.hostmonitor_data_collector.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import com.hust.hostmonitor_data_collector.service.DataCollectorService;
import com.hust.hostmonitor_data_collector.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class UserOperationController {
    @Resource
    UserService userService;
    @Resource
    DataCollectorService dataCollectorService;
    /**
     * 用于用户注册的接口
     *
     */
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

    /**
     * 用于用户登录的接口
     *
     */
    @PostMapping(value = "/SignIn")
    @ResponseBody
    public SystemUser signIn(@RequestBody Map<String,String> params) {
        String userID = params.get("userID");
        String password = params.get("password");
        SystemUser user = userService.signIn(userID,password);
        System.out.println("[SignIn]: "+userID+","+ password + "  :  "+user);
        return user;
    }

    /**
     * 用于获取所有的注册用户的相关信息
     * 返回值格式：
     * [
     *     {
     *         "admin": false,          是否为管理员
     *         "email": "222222",       邮箱
     *         "emailValidState": false,    邮箱是否验证
     *         "lastEditTime": 1624003586000,   上次编辑时间戳
     *         "password": "",      密码
     *         "phone": "112",      电话号码
     *         "phoneValidState": true,     电话号码是否验证
     *         "superAdmin": false,     是否为超级管理员
     *         "userID": "10006",       用户id
     *         "userName": "VX_1",          用户名
     *         "userType": 0,           用户类型
     *         "validState": false      用户状态
     *     },
     *     {
     *         "admin": true,
     *         "email": "576412173@qq.com",
     *         "emailValidState": false,
     *         "lastEditTime": 1622978531000,
     *         "password": "",
     *         "phone": "18332630632",
     *         "phoneValidState": false,
     *         "superAdmin": false,
     *         "userID": "10007",
     *         "userName": "AbiterVX",
     *         "userType": 1,
     *         "validState": true
     *     },...
     * ]
     */
    @GetMapping(value = "/getUsers")
    @ResponseBody
    public String getUsers(){
        JSONArray result=new JSONArray();
        List<SystemUser> userList = userService.getUsers();
        for(int i=0;i<userList.size();i++){
            result.add(userList.get(i));
            System.out.println(userList.get(i).getLastEditTime().toString());
        }
        return result.toJSONString();
    }

    /**
     * 更新用户相关信息
     * @param params 需要将更新的信息放入请求参数中
     * @return
     */
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

    /**
     * 更新用户密码的接口
     * @param params
     * @return
     */
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

    /**
     * 用于发送邮件的接口，处于测试中
     * @param map
     * @return
     */

    @GetMapping(value = "/Email/{emailAddress}")
    @ResponseBody
    public String testSendEmail(@PathVariable Map<String,String> map) {
        String emailAddress = map.get("emailAddress");
        //userService.sendEmail(emailAddress);
        return "";
    }

    /**
     * 获取用户当前的系统参数设置
     * 返回值格式：
     * {
     *     "BackupTimingInterval": 8,           备份时间周期
     *     "ReportTimingInterval": 6,           报告时间周期
     *     "ReportEmergency": false,        是否进行紧急报告
     *     "BackupTiming": false,
     *     "ReportTiming": false,
     *     "BackupEmergency": false,        是否进行紧急备份
     *     "ReportFailureRateThreshold": 7.0,       报告的磁盘警告阈值
     *     "BackupFailureRateThreshold": 9.0        备份的磁盘警告阈值
     * }
     */
    @GetMapping(value = "/SystemSetting/Get")
    @ResponseBody
    public String getSystemSetting() {
        System.out.println("[Get]/SystemSetting/Get");
        return userService.getSystemSetting();
    }

    /**
     * 用于进行用户系统设定的接口，需要将请求的参数传入
     * @param params
     * @return
     */
    @PostMapping(value = "/SystemSetting/Reset")
    @ResponseBody
    public String resetSystemSetting(@RequestBody Map<String,String> params) {
        //获取
        boolean ReportTiming = Boolean.parseBoolean(params.get("ReportTiming"));
        int ReportTimingInterval = Integer.parseInt(params.get("ReportTimingInterval"));
        boolean ReportEmergency =  Boolean.parseBoolean(params.get("ReportEmergency"));
        float ReportFailureRateThreshold = Float.parseFloat((params.get("ReportFailureRateThreshold")));
        boolean BackupTiming =  Boolean.parseBoolean(params.get("BackupTiming"));
        int BackupTimingInterval = Integer.parseInt(params.get("BackupTimingInterval"));
        boolean BackupEmergency =  Boolean.parseBoolean(params.get("BackupEmergency"));
        float BackupFailureRateThreshold = Float.parseFloat(params.get("BackupFailureRateThreshold"));
        int dataSampleInterval=Integer.parseInt(params.get("dataSampleInterval"));
        int processSampleInterval=Integer.parseInt(params.get("processSampleInterval"));

        //检验
        boolean couldUpdate = false;
        if(ReportTimingInterval>=0 && BackupTimingInterval>=0 &&
                ReportFailureRateThreshold>=0 && ReportFailureRateThreshold<=100 &&
                BackupFailureRateThreshold>=0 && BackupFailureRateThreshold<=100
                &&dataSampleInterval>=0 && processSampleInterval>=0
        ){
            couldUpdate = true;
        }

        //更新
        if(couldUpdate){
            JSONObject newSystemSetting = new JSONObject();
            newSystemSetting.put("ReportTiming",ReportTiming);
            newSystemSetting.put("ReportTimingInterval",ReportTimingInterval);
            newSystemSetting.put("ReportEmergency",ReportEmergency);
            newSystemSetting.put("ReportFailureRateThreshold",ReportFailureRateThreshold);
            newSystemSetting.put("BackupTiming",BackupTiming);
            newSystemSetting.put("BackupTimingInterval",BackupTimingInterval);
            newSystemSetting.put("BackupEmergency",BackupEmergency);
            newSystemSetting.put("BackupFailureRateThreshold",BackupFailureRateThreshold);
            newSystemSetting.put("dataSampleInterval",dataSampleInterval);
            newSystemSetting.put("processSampleInterval",processSampleInterval);
            userService.setSystemSetting(newSystemSetting);
            dataCollectorService.updateSystemSetting(newSystemSetting);
        }
        return "Complete";
    }

    @GetMapping(value = "/test")
    @ResponseBody
    public String test(){
        return userService.signUp("zhangziyuetest","hust");
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public String delete(@RequestBody Map<String,String> params) {
        String userName = params.get("userName");
        System.out.println("[删除用户] "+userName);
        return userService.deleteUser(userName);
    }


}
