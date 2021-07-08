/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:21:29
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:05:10
 */
package com.data_escape.DataEscapeUtils.common.handler;

import com.data_escape.DataEscapeUtils.DiskEscape.DiskSender;
import com.data_escape.DataEscapeUtils.SrcNode.ClientManager;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.dispatcher.Handler;
import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;
import com.data_escape.DataEscapeUtils.common.packet.ConnAckPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

public class ConnAckPacketHandler implements Handler<ConnAckPacket>{
    private static final Logger logger = LoggerFactory.getLogger(ConnAckPacketHandler.class);

    @Override
    public void execute(ConnAckPacket packet, ChannelContext channelContext, Manager manager) {
        ClientManager clientManager = (ClientManager) manager;
        int ackCode = packet.getConnCode();
        switch (ackCode) {
            case ConnAckPacket.CONN_ACCEPT:
                logger.info("[ConnAckPacketHandler][数据逃生连接建立，开始传输硬盘镜像][error info :{}]", packet.getMsg());
                clientManager.setCurWorkingToken(packet.getToken());
                DiskSender.diskEscape(clientManager, false, -1);
                break;
        
            case ConnAckPacket.CONN_DENY:
                logger.info("[ConnAckPacketHandler][业务已存在，连接被拒绝][error info :{}]", packet.getMsg());
                clientManager.shutdown();
                break;
            default:
                logger.info("[ConnAckPacketHandler][未知响应，尝试重新连接][error info :{}]", packet.getMsg());
                clientManager.connAsk();
                break;
        }
    }

    @Override
    public Class<? extends MyPacket> getType() {
        return ConnAckPacket.class;
    }
    
}