/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:20:34
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:48:26
 */
package com.data_escape.DataEscapeUtils.common.packet;

import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;

public class ConnAskPacket implements MyPacket{
    public static final String TYPE = "CLIENT_CONN_ASK";
    // 连接请求包可以添加附加信息
}