/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 11:31:44
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:00:44
 */
package com.data_escape.DataEscapeUtils.common.dispatcher;

import com.data_escape.DataEscapeUtils.common.Manager;

import org.tio.core.ChannelContext;

public interface Handler<T extends MyPacket> {
    /**
     * @name:     
     * @descrb:     根据消息内容执行响应函数
     * @param {*}
     * @return {*}
     */
    void execute(T packet, ChannelContext channelContext, Manager manager);

    /**
     * @name:     
     * @descrb:     返回处理器需要处理的消息的类型
     * @param {*}
     * @return {*}
     */
    Class<? extends MyPacket> getType();
}