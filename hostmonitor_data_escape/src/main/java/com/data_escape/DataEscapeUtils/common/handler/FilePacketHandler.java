/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:21:51
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:16:22
 */
package com.data_escape.DataEscapeUtils.common.handler;

import com.data_escape.DataEscapeUtils.DiskEscape.DiskReciver;
import com.data_escape.DataEscapeUtils.DstNode.ServerManager;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.dispatcher.Handler;
import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;
import com.data_escape.DataEscapeUtils.common.packet.FilePacket;
import com.data_escape.common.TokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

public class FilePacketHandler implements Handler<FilePacket>{
    private static final Logger logger = LoggerFactory.getLogger(FilePacketHandler.class);

    @Override
    public void execute(FilePacket packet, ChannelContext channelContext, Manager manager) {
        // 首先验证 Token，验证通过才会接受文件
        ServerManager serverManager = (ServerManager)manager;
        if (TokenUtils.verifyToken(serverManager.getCurWorkingToken(), packet.getToken())) {
            // 验证成功
            logger.info("[execute][thread: {}][Token 验证成功，接收该文件包]", Thread.currentThread().getName()); 
            DiskReciver.store(serverManager, packet);
        } else {
            // 验证失败
            logger.error("[execute][Token 验证失败，丢弃该文件包][client: {}]", channelContext.getClientNode().toString());
            return;
        }
    }

    @Override
    public Class<? extends MyPacket> getType() {
        return FilePacket.class;
    }

}