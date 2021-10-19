package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao {
    // user
    @Select("set @new_id=0;\n " +
            "call p_getId(@new_id);\n " +
            "set @date=now();\n " +
            "INSERT INTO UserTable values(@new_id,#{user_name},#{user_password},0,0,@date,'','',0,0);\n " +
            "select @new_id; ")
    String signUp(@Param("user_name") String user_name, @Param("user_password") String Password);


    @Select("select * " +
            "from UserTable " +
            "where UserID=#{user_id} and Password=#{user_password}; ")
    SystemUser signIn(@Param("user_id") String UserID, @Param("user_password") String Password);

    @Select("select * from UserTable")
    List<SystemUser> getUsers();

    @Select("UPDATE UserTable " +
            "SET UserName=#{user_name},UserType=#{user_type},ValidState=#{valid_state},LastEditTime=now() " +
            ",Phone=#{user_phone},Email=#{user_email} " +
            ",PhoneValidState=#{phone_valid_state},EmailValidState=#{email_valid_state} " +
            "WHERE UserID=#{user_id};")
    void updateUserInfo(@Param("user_name") String user_name,
                        @Param("user_type") int user_type,
                        @Param("valid_state") int valid_state,
                        @Param("user_phone") String user_phone,
                        @Param("user_email") String user_email,
                        @Param("phone_valid_state") int phone_valid_state,
                        @Param("email_valid_state") int email_valid_state,
                        @Param("user_id") String user_id);


    @Select("UPDATE UserTable " +
            "SET Password=#{new_password} " +
            "WHERE UserID=#{user_id} and Password=#{user_password}; ")
    void updateUserPassword(@Param("user_id") String user_id, @Param("user_password") String user_password,
                            @Param("new_password") String new_password);

}
