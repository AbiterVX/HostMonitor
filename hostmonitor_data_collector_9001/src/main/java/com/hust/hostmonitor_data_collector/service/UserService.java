package com.hust.hostmonitor_data_collector.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import com.hust.hostmonitor_data_collector.dao.UserDao;
import com.hust.hostmonitor_data_collector.dao.entity.DiskHardWareInfo;
import com.hust.hostmonitor_data_collector.dao.entity.HardWithDFPRecord;
import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//用户相关的服务类
public class UserService {
    //User数据库操作
    @Autowired
    UserDao userDao;
    //磁盘数据库操作，仅用于邮件提醒
    @Autowired
    DiskFailureMapper diskFailureMapper;
    //配置类
    private ConfigDataManager configDataManager = ConfigDataManager.getInstance();
    private final double highRiskThreshold=0.20f;
    private final double lowRiskThreshold=0.70f;
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
            mailTaskFunction();
        }
    };
    private void mailTaskFunction(){
        String Title="磁盘故障可能性警告(Disk Failure Warning)";
        List<HardWithDFPRecord> hardWithDFPRecordList=diskFailureMapper.selectLatestDFPWithHardwareRecordList();
        String Text="本邮件将提醒您，下列磁盘有损坏风险：\n";
        String[] levels=new String[3];
        levels[0]="[高风险]:\n";
        levels[1]="[中风险]:\n";
        levels[2]="[低风险]:\n";
        for(HardWithDFPRecord hardWithDFPRecord:hardWithDFPRecordList){
            String temp="["+(hardWithDFPRecord.isSSd?"SSD":"HDD")+"]";
            temp+=hardWithDFPRecord.diskSerial+" in ";
            temp+=hardWithDFPRecord.hostName+"@"+hardWithDFPRecord.hostName+"\n";
            if(hardWithDFPRecord.predictProbability<highRiskThreshold){
                levels[0]+=temp;
            }
            else if(hardWithDFPRecord.predictProbability>lowRiskThreshold){
                levels[2]+=temp;
            }
            else {
                levels[1]+=temp;
            }
        }

    }
    //Init
    public UserService(){}


    //-----用户
    //注册
    public String signUp(String userName,String password){
        //System.out.println(userName);
        //System.out.println(password);
        Timestamp timestamp=new Timestamp(new Date().getTime());
        return userDao.signUp(userName,password,timestamp);
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
            if(operateUserID.equals(userID)){
                //无法修改自己用户类型
                int originValidState = operateUser.isValidState()?1:0;
                userDao.updateUserInfo(userName,operateUser.getUserType(),originValidState,userPhone,userEmail, phoneValidState,emailValidState,userID);
            }
            else{
                if(operateUser.isSuperAdmin()){
                    userDao.updateUserInfo(userName,userType,validState,userPhone,userEmail, phoneValidState,emailValidState,userID);
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
        //发送方,接收方,标题,内容
        message.setFrom(userName);
        message.setTo(emailAddress);
        message.setSubject(mailTitle);
        message.setText(mailText);
        mailSender.send(message);
    }

    //-----系统设置
    //获取系统设置
    public String getSystemSetting(){
        return configDataManager.getSystemSetting().toJSONString();
    }
    //更新系统设置
    public void setSystemSetting(JSONObject currentSystemSetting){
        configDataManager.updateSystemSetting(currentSystemSetting);
    }

    public String deleteUser(String userName) {
        userDao.deleteUser(userName);
        return "successful";
    }
}
