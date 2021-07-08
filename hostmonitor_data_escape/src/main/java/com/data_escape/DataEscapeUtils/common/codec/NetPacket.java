/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:15:37
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:00:06
 */
package com.data_escape.DataEscapeUtils.common.codec;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;

public class NetPacket extends Packet{
    private static final Logger logger = LoggerFactory.getLogger(NetPacket.class);
    
    private static final long serialVersionUID = -172060606924066412L;
    public static final int HEADER_LENGHT = 4;      // 头的长度
    public static final String CHARSRT = "utf-8";   // 字符集

    private String type;        // 消息类型
    private byte[] content;     // 消息体

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setContent(String content) {
        try {
            this.content = content.getBytes(CHARSRT);
        } catch (UnsupportedEncodingException e) {
            logger.error("[setContent][消息体编码失败][char set: {}]", CHARSRT);
        }
    }

    public void setContent(MyPacket myPacket) {
        if (myPacket != null) {
            this.content = JSON.toJSONBytes(myPacket);
        } else {
            logger.error("[setContent][消息体为空][MyPacket is null]");
        }
    }
    
}