package com.hust.hostmonitor_data_collector.dao;

import com.hust.hostmonitor_data_collector.dao.entity.SystemUser;
import com.hust.hostmonitor_data_collector.dao.provider.UserProvider;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserDao {

    @SelectProvider(type = UserProvider.class,method = "signUp")
    String signUp(@Param("user_name") String user_name, @Param("user_password") String Password, @Param("timestamp")Timestamp timestamp);

    @SelectProvider(type = UserProvider.class,method = "signIn")

    SystemUser signIn(@Param("user_id") String UserID, @Param("user_password") String Password);

    @SelectProvider(type=UserProvider.class,method = "selectAll")
    List<SystemUser> getUsers();

    @UpdateProvider(type= UserProvider.class,method = "updateUserInfo")
    void updateUserInfo(@Param("user_name") String user_name,
                        @Param("user_type") int user_type,
                        @Param("valid_state") int valid_state,
                        @Param("user_phone") String user_phone,
                        @Param("user_email") String user_email,
                        @Param("phone_valid_state") int phone_valid_state,
                        @Param("email_valid_state") int email_valid_state,
                        @Param("user_id") String user_id);


    @UpdateProvider(type=UserProvider.class,method = "updateUserPassword")
    void updateUserPassword(@Param("user_id") String user_id, @Param("user_password") String user_password,
                            @Param("new_password") String new_password);

    @DeleteProvider(type = UserProvider.class,method = "deleteUser")
    void deleteUser(@Param("userName") String userName);

}
