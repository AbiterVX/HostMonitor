/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 16:51:00
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:55:17
 */
package com.data_escape.DataEscapeUtils.common.packet;

import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;

public class ConnAckPacket implements MyPacket{
    public static final String TYPE = "SERVER_CONN_ACK";
    
    public final static int CONN_ACCEPT = 0;      // 连接接受
    public final static int CONN_DENY = 1;        // 连接拒绝-TOKEN已存在，已有业务连接
    
    private int connCode;   // 连接状态代码
    private String token;   // 连接 Token
    private String msg;     // 返回信息

    public int getConnCode() {
        return connCode;
    }

    public void setConnCode(int connCode) {
        this.connCode = connCode;
    }

    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    
}