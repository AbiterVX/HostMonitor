/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:17:25
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:15:18
 */
package com.data_escape.DataEscapeUtils.DstNode;

import java.nio.ByteBuffer;

import com.data_escape.DataEscapeUtils.common.codec.Decoder;
import com.data_escape.DataEscapeUtils.common.codec.Encoder;
import com.data_escape.DataEscapeUtils.common.codec.NetPacket;
import com.data_escape.DataEscapeUtils.common.dispatcher.MsgDispatcher;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

public class ServerHandler implements ServerAioHandler{
    private ServerManager serverManager;

    public ServerHandler(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    /**
     * @name:     
     * @descrb:                                 解码数据包
     * @param {ByteBuffer} buffer
     * @param {int} limit
     * @param {int} position
     * @param {int} ableLength
     * @param {ChannelContext} channelContext
     * @return {*}
     */
    public NetPacket decode(ByteBuffer buffer, int limit, int position, int ableLength, ChannelContext channelContext) throws AioDecodeException {
        return Decoder.deCode(buffer, limit, position, ableLength, channelContext);
    }

    @Override
    /**
     * @name:     
     * @descrb:                                 编码数据包
     * @param {Packet} packet
     * @param {TioConfig} tioConfig
     * @param {ChannelContext} channelContext
     * @return {*}
     */
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        return Encoder.enCode(packet, tioConfig, channelContext);
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        MsgDispatcher.msgDispatcher(packet, channelContext, serverManager);
    }
    
}