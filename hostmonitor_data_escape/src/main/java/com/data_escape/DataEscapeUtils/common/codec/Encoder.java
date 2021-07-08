/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:12:29
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-21 11:12:03
 */
package com.data_escape.DataEscapeUtils.common.codec;

import java.nio.ByteBuffer;

import com.alibaba.fastjson.JSON;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;

public class Encoder {
    /**
     * @name:     
     * @descrb:                                 通用数据包编码
     * @param {Packet} packet                   数据包
     * @param {TioConfig} tioConfig             Tio连接配置
     * @param {ChannelContext} channelContext   连接上下文
     * @return {*}
     */
    public static ByteBuffer enCode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        NetPacket msgPacket = (NetPacket) packet;
        byte[] body = JSON.toJSONBytes(msgPacket);
        int bodyLength = 0;
        
        if (body != null) bodyLength = body.length;
        // 计算消息总长度
        int totalLength = NetPacket.HEADER_LENGHT + bodyLength;
        // 新建 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        // 设置 ByteBuffer 字节序
        buffer.order(tioConfig.getByteOrder());
        // 写入消息头
        buffer.putInt(bodyLength);
        // 写入消息体
        if (body != null){
            buffer.put(body);
        }
        return buffer;
    }
}
