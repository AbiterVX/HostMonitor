/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 12:03:50
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:04:48
 */
package com.data_escape.DataEscapeUtils.common.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.data_escape.DataEscapeUtils.common.handler.ConnAckPacketHandler;
import com.data_escape.DataEscapeUtils.common.handler.ConnAskPacketHandler;
import com.data_escape.DataEscapeUtils.common.handler.FileAckPacketHandler;
import com.data_escape.DataEscapeUtils.common.handler.FilePacketHandler;
import com.data_escape.DataEscapeUtils.common.packet.ConnAckPacket;
import com.data_escape.DataEscapeUtils.common.packet.ConnAskPacket;
import com.data_escape.DataEscapeUtils.common.packet.FileAckPacket;
import com.data_escape.DataEscapeUtils.common.packet.FilePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgHandlerContainer {
    private static final Logger logger = LoggerFactory.getLogger(MsgHandlerContainer.class);
    
    // 统一管理所有消息处理器
    private final Map<String, Class<? extends Handler<? extends MyPacket>>> handlerContainer= new HashMap<>();
    // 初始化消息处理器
    public MsgHandlerContainer(){
        handlerContainer.put(ConnAskPacket.TYPE, ConnAskPacketHandler.class);
        handlerContainer.put(ConnAckPacket.TYPE, ConnAckPacketHandler.class);
        handlerContainer.put(FileAckPacket.TYPE, FileAckPacketHandler.class);
        handlerContainer.put(FilePacket.TYPE, FilePacketHandler.class);
        
        logger.info("[handlerContainerInit][消息处理器容器初始化完成][处理器数量: {}]", handlerContainer.size());
    }
    
    public Object getHandler(String msgType) {
        Class<? extends Handler<? extends MyPacket>> handlerClass = null;
        Object handler = null;
        if (handlerContainer.containsKey(msgType)) {
            handlerClass = handlerContainer.get(msgType);
            String errorInfo = "";
            try {
                handler = handlerClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                errorInfo += e.getMessage() + "\n";
            } catch (IllegalAccessException e) {
                errorInfo += e.getMessage() + "\n";
            } catch (IllegalArgumentException e) {
                errorInfo += e.getMessage() + "\n";
            } catch (InvocationTargetException e) {
                errorInfo += e.getMessage() + "\n";
            } catch (NoSuchMethodException e) {
                errorInfo += e.getMessage() + "\n";
            } catch (SecurityException e) {
                errorInfo += e.getMessage() + "\n";
            }
            if (errorInfo.length() > 0) {
                logger.error("[getHandler][获取处理器实例失败][error info: {}]", errorInfo);
                return null;
            }
        }
        return handler;
    }

    public static MyPacket getMyPacket(Handler<?> handler, byte[] content) {
        return JSON.parseObject(content, handler.getType());
    }
}