package com.hust.hostmonitor_data_collector.dao.entity;

import java.sql.Timestamp;

public class SystemUser {
    private String userID;
    private String userName;
    private String password;
    private int userType;
    private boolean validState;
    private Timestamp lastEditTime;
    private String phone;
    private String email;
    private boolean phoneValidState;
    private boolean emailValidState;

    public SystemUser(){}
    public SystemUser(String userID, String userName, String password, Integer userType, boolean validState, Timestamp lastEditTime, String phone, String email, boolean phoneValidState, boolean emailValidState) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.validState = validState;
        this.lastEditTime = lastEditTime;
        this.phone = phone;
        this.email = email;
        this.phoneValidState = phoneValidState;
        this.emailValidState = emailValidState;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getUserType() {
        return userType;
    }

    public boolean isValidState() {
        return validState;
    }

    public Timestamp getLastEditTime() {
        return lastEditTime;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPhoneValidState() {
        return phoneValidState;
    }

    public boolean isEmailValidState() {
        return emailValidState;
    }

    public boolean isSuperAdmin(){
        return (userType == 2);
    }

    public boolean isAdmin(){
        return (userType == 1);
    }

    public void clearPassword(){
        password = "";
    }

    @Override
    public String toString() {

        return "SystemUser{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", validState=" + validState +
                ", lastEditTime=" + lastEditTime.getTime() +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", phoneValidState=" + phoneValidState +
                ", emailValidState=" + emailValidState +
                '}';
    }


}
