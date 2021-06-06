package com.hust.hostmonitor_data_collector.service;

import com.hust.hostmonitor_data_collector.dao.UserDao;
import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class UserService {
    @Autowired
    UserDao userDao;

    public String signUp(String userName,String password){
        System.out.println(userName);
        System.out.println(password);
        return userDao.signUp(userName,password);
    }

    public SystemUser signIn(String userID,String password){
        return userDao.signIn(userID,password);
    }

    public List<SystemUser> getUsers(){
        List<SystemUser> userList = userDao.getUsers();
        for (SystemUser currentUser:userList){
            currentUser.clearPassword();
        }
        return userList;
    }

    public void updateUserInfo(String operateUserID,String operateUserPassword,String userName,int userType,int validState, String userPhone,String userEmail,
                        int phoneValidState,int emailValidState, String userID){
        SystemUser operateUser = userDao.signIn(operateUserID,operateUserPassword);
        if(operateUser!=null){
            System.out.println("[updateUserInfo:]" + operateUser.isSuperAdmin());

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

    public void updateUserPassword(String userID,String password,String newPassword){
        userDao.updateUserPassword(userID,password,newPassword);
    }


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
    public String sendSMS(String phoneNumber,String diskName){
        try {
            String url = String.format(smsUrl, phoneNumber, tplId1, "%23code%23%3D"+URLEncoder.encode(diskName, "utf-8"), key);
            return restTemplate.getForObject(url,String.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String userName;


    public void sendEmail(String emailAddress){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(userName);//发送方
        message.setTo(emailAddress);//接收方
        message.setSubject("主题：简单邮件");//标题
        message.setText("测试邮件内容");//内容
        mailSender.send(message);
    }
}
