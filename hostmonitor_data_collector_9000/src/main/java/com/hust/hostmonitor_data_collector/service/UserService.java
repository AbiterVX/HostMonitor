package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import com.hust.hostmonitor_data_collector.dao.UserDao;
import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import com.hust.hostmonitor_data_collector.utils.DispersedConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserService {
    //User数据库操作
    @Autowired
    UserDao userDao;
    @Autowired
    DiskFailureMapper diskFailureMapper;
    //配置类
    private DispersedConfig dispersedConfig = DispersedConfig.getInstance();
    private JSONObject systemSetting;

    //RestTemplate
    @Resource
    private RestTemplate restTemplate;

    //读取配置文件字段:
    @Value("${sms_config.sms-url}")
    private String smsUrl;
    @Value("${sms_config.tpl-id-1}")
    private String tplId1;
    @Value("${sms_config.key}")
    private String key;
    @Value("${spring.mail.username}")
    private String userName;
    //邮件发送
    @Autowired
    private JavaMailSender mailSender;
    private Timer mailTimer=new Timer();

    private TimerTask mailTask=new TimerTask() {
        @Override
        public void run() {

        }
    };
    //Init
    public UserService(){
        systemSetting = dispersedConfig.getSystemSetting();
    }


    //----------对外接口

    //-----用户
    //注册
    public String signUp(String userName,String password){
        //System.out.println(userName);
        //System.out.println(password);
        return userDao.signUp(userName,password);
    }
    //登录
    public SystemUser signIn(String userID,String password){
        return userDao.signIn(userID,password);
    }
    //获取所有用户
    public List<SystemUser> getUsers(){
        List<SystemUser> userList = userDao.getUsers();
        for (SystemUser currentUser:userList){
            currentUser.clearPassword();
        }
        return userList;
    }
    //更新用户信息
    public void updateUserInfo(String operateUserID,String operateUserPassword,String userName,int userType,int validState, String userPhone,String userEmail,
                        int phoneValidState,int emailValidState, String userID){
        SystemUser operateUser = userDao.signIn(operateUserID,operateUserPassword);
        if(operateUser!=null){
            //System.out.println("[updateUserInfo:]" + operateUser.isSuperAdmin());

            if(operateUserID.equals(userID)){
                //无法修改自己用户类型
                int originValidState = operateUser.isValidState()?1:0;
                userDao.updateUserInfo(userName,operateUser.getUserType(),originValidState,userPhone,userEmail,
                        phoneValidState,emailValidState,userID);
            }
            else{
                if(operateUser.isSuperAdmin()){
                    userDao.updateUserInfo(userName,userType,validState,userPhone,userEmail,
                            phoneValidState,emailValidState,userID);
                }
            }
        }


    }
    //更新用户密码
    public void updateUserPassword(String userID,String password,String newPassword){
        userDao.updateUserPassword(userID,password,newPassword);
    }
    //发送短信
    public String sendSMS(String phoneNumber,String diskName){
        try {
            String url = String.format(smsUrl, phoneNumber, tplId1, "%23code%23%3D"+URLEncoder.encode(diskName, "utf-8"), key);
            return restTemplate.getForObject(url,String.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    //发送邮件
    public void sendEmail(String emailAddress,String mailTitle,String mailText){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(userName);//发送方
        message.setTo(emailAddress);//接收方
        message.setSubject("主题：简单邮件");//标题
        message.setText("测试邮件内容");//内容
        mailSender.send(message);
    }


    //-----系统设置
    //获取系统设置
    public String getSystemSetting(){
        return systemSetting.toJSONString();
    }
    //更新系统设置
    public void setSystemSetting(JSONObject _systemSetting){
        systemSetting = dispersedConfig.updateSystemSetting(_systemSetting);
    }




}
