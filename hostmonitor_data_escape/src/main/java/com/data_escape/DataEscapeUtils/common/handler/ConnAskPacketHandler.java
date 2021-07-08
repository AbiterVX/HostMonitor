/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:21:29
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:14:37
 */
package com.data_escape.DataEscapeUtils.common.handler;

import com.data_escape.DataEscapeUtils.DstNode.ServerManager;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.codec.NetPacket;
import com.data_escape.DataEscapeUtils.common.dispatcher.Handler;
import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;
import com.data_escape.DataEscapeUtils.common.packet.ConnAckPacket;
import com.data_escape.DataEscapeUtils.common.packet.ConnAskPacket;
import com.data_escape.common.TokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

public class ConnAskPacketHandler implements Handler<ConnAskPacket>{
    private static final Logger logger = LoggerFactory.getLogger(ConnAskPacketHandler.class);

    @Override
    public void execute(ConnAskPacket packet, ChannelContext channelContext, Manager manager) {
        logger.info("[execute][处理连接请求][client: {}]", channelContext.getClientNode().toString());
        ServerManager serverManager = (ServerManager) manager;
        // 开始处理连接请求包
        // 1、如果目前已有连接
        ConnAckPacket connAckPacket = new ConnAckPacket();
        NetPacket netPacket = new NetPacket();
        if (serverManager.getCurWorkingToken() != null) {
            connAckPacket.setConnCode(ConnAckPacket.CONN_DENY);
            connAckPacket.setMsg("已存在数据逃生业务，不允许访问");
            connAckPacket.setToken("");

            netPacket.setType(ConnAckPacket.TYPE);
            netPacket.setContent(connAckPacket);
            Tio.bSend(channelContext, netPacket);
            Tio.remove(channelContext, "remark");
        } else {
            String token = TokenUtils.getToken();
            connAckPacket.setConnCode(ConnAckPacket.CONN_ACCEPT);
            connAckPacket.setMsg("速速发送文件");
            connAckPacket.setToken(token);

            serverManager.setCurWorkingToken(token);
            serverManager.bindWithToken(channelContext, token);
            
            netPacket.setType(ConnAckPacket.TYPE);
            netPacket.setContent(connAckPacket);
            Tio.send(channelContext, netPacket);
            // 同时开始文件传输应答
            serverManager.startAckFile();
        }
    }

    @Override
    public Class<? extends MyPacket> getType() {
        return ConnAskPacket.class;
    }
    
}