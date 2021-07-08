/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:11:56
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 16:59:19
 */
package com.data_escape.DataEscapeUtils.common.codec;

import java.nio.ByteBuffer;

import com.alibaba.fastjson.JSON;

import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;

public class Decoder {
    /**
     * @name:     
     * @descrb:                                 通用数据包解码
     * @param {ByteBuffer} buffer               接收到的数据
     * @param {int} limit                       数据真实长度
     * @param {int} position                    本次读取位置
     * @param {int} ableLength                  有效数据长度 (limit - position)
     * @param {ChannelContext} channelContext   连接上下文
     * @return {*}
     */
    public static NetPacket deCode(ByteBuffer buffer, int limit, int position, int ableLength, ChannelContext channelContext) throws AioDecodeException {
        // 包的长度小于基本长度：包未接收完整
        if (ableLength < NetPacket.HEADER_LENGHT) {
            return null;
        }
        // 消息长度
        int bodyLength = buffer.getInt();
        // 数据异常
        if (bodyLength < 0) {
            throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getServerNode());
        }
        // 消息总长度
        int usefulLength = NetPacket.HEADER_LENGHT + bodyLength;
        // 消息长度异常
        if (ableLength < usefulLength){
            return null;
        }
        else{
            NetPacket msgPacket = new NetPacket();
            if (bodyLength > 0){
                // 解析消息体
                byte[] body= new byte[bodyLength];
                buffer.get(body);
                msgPacket = JSON.parseObject(body, NetPacket.class);
            }
            return msgPacket;
        }
    }
}