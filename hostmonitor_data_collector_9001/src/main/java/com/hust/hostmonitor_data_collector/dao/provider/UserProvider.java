package com.hust.hostmonitor_data_collector.dao.provider;

import com.hust.hostmonitor_data_collector.utils.ConfigDataManager;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.Date;

public class UserProvider {
    public int dataSourceSelect= ConfigDataManager.getInstance().getConfigJson().getInteger("DataSourceSelect");
    public int DamengIDSeed=20003;
    public String signUp(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL = "set @new_id=0;\n " +
                    "call p_getId(@new_id);\n " +
                    "set @date=now();\n " +
                    "INSERT INTO UserTable values(@new_id,#{user_name},#{user_password},0,0,@date,'','',0,0);\n " +
                    "select @new_id; ";
        }
        else if(dataSourceSelect==1) {
            Integer newID = DamengIDSeed++;
            SQL = "INSERT INTO storagedevicemonitor.UserTable values('" + (newID.toString()) + "',#{user_name},#{user_password},0,0,#{timestamp},'','',0,0);\n " +
                    "select USERID from storagedevicemonitor.UserTable where USERID='" + newID + "'; ";

        }
        return SQL;
    }

    public String signIn(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * " +
                    "from UserTable " +
                    "where UserID=#{user_id} and Password=#{user_password}; ";
        }
        else if(dataSourceSelect==1){
            SQL="select * " +
                    "from storagedevicemonitor.UserTable " +
                    "where UserID=#{user_id} and Password=#{user_password}; ";

        }
        return SQL;
    }
    public String selectAll(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="select * from UserTable";
        }
        else if(dataSourceSelect==1){
            SQL="select * from storagedevicemonitor.UserTable";
        }
        return SQL;

    }
    public String updateUserInfo(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="UPDATE UserTable " +
                    "SET UserName=#{user_name},UserType=#{user_type},ValidState=#{valid_state},LastEditTime=now() " +
                    ",Phone=#{user_phone},Email=#{user_email} " +
                    ",PhoneValidState=#{phone_valid_state},EmailValidState=#{email_valid_state} " +
                    "WHERE UserID=#{user_id};";
        }
        else if(dataSourceSelect==1){
            SQL="UPDATE storagedevicemonitor.UserTable " +
                    "SET UserName=#{user_name},UserType=#{user_type},ValidState=#{valid_state},LastEditTime=now() " +
                    ",Phone=#{user_phone},Email=#{user_email} " +
                    ",PhoneValidState=#{phone_valid_state},EmailValidState=#{email_valid_state} " +
                    "WHERE UserID=#{user_id};";
        }
        return SQL;
    }
    public String updateUserPassword(){
        String SQL=null;
        if(dataSourceSelect==0){
            SQL="UPDATE UserTable " +
                    "SET Password=#{new_password} " +
                    "WHERE UserID=#{user_id} and Password=#{user_password}; ";
        }
        else if(dataSourceSelect==1){
            SQL="UPDATE storagedevicemonitor.UserTable " +
                    "SET Password=#{new_password} " +
                    "WHERE UserID=#{user_id} and Password=#{user_password}; ";
        }
        return SQL;
    }

}
