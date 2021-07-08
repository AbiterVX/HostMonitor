/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-24 20:36:57
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:06:51
 */
package com.data_escape.DataEscapeUtils.common.handler;

import java.util.Arrays;

import com.data_escape.DataEscapeUtils.DiskEscape.DiskSender;
import com.data_escape.DataEscapeUtils.SrcNode.ClientManager;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.dispatcher.Handler;
import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;
import com.data_escape.DataEscapeUtils.common.packet.FileAckPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Node;

public class FileAckPacketHandler implements Handler<FileAckPacket>{
    private static final Logger logger = LoggerFactory.getLogger(FileAckPacketHandler.class);
    private static final int WAIT = -2;         // 传输未开始
    private static final int COMPLETE = -1;     // 传输完成

    @Override
    public void execute(FileAckPacket packet, ChannelContext channelContext, Manager manager) {
        ClientManager clientManager = (ClientManager) manager;
        Node serverNode = channelContext.getServerNode();
        Node clientNode = channelContext.getClientNode();
        
        // 当发送方接收到该请求后
        boolean recv[] = packet.getSavedFileShard();
        // boolean send[] = DiskEscape.sentFileShard;
        
        logger.info("[execute][接收方文件确认]\n[received: {}]", Arrays.toString(recv));
        int curAckIndex = fielShardAck(recv);
        if (curAckIndex == COMPLETE) {
            logger.info("[execute][**************** 文件传输已全部结束，结束本次流程 ****************][server: {}][client: {}]", serverNode.toString(), clientNode.toString());
            clientManager.shutdown();
            // 准备结束整个流程
        } else if (curAckIndex != WAIT){
            int reSendIndex = isNeedReSend(curAckIndex);
            // 需要启动重传业务
            if (reSendIndex >= 0) {
                // 开启额外的线程进行重传
                logger.info("[execute][**************** 需要断点重传 ****************][server: {}][client: {}][reSend: {}]", serverNode.toString(), clientNode.toString(), reSendIndex);
                DiskSender.diskEscape((ClientManager)manager, true, reSendIndex);
            }
        }
    }

    /**
     * @name:     
     * @descrb:         返回服务器想要接受的文件片序号，防TCP快速重传，若多次返回同一序号，则重传该序号的文件
     * @param nbooleano recv
     * @return n*o
     */
    public int fielShardAck(boolean recv[]){
        if (recv.length <= 0) return WAIT;
        for (int i = 0; i < recv.length; i++) {
            if (!recv[i]) {
                return i;
            }
        }
        return COMPLETE;
    }

    /**
     * @name:     
     * @descrb:   
     * @param nbooleano recv
     * @param nbooleano send
     * @return n*o
     */
    public int isNeedReSend(int fileShardAckIndex){
        if (fileShardAckIndex == DiskSender.curFileShardIndex) {
            DiskSender.curFileShardIndexAckCount ++;
            // 如果重复ack次数超过max_count，且客户端判断包已发送成功，则需要重传这个包
            if (DiskSender.curFileShardIndexAckCount >= DiskSender.MAX_ACK_COUNT) {
                DiskSender.curFileShardIndexAckCount = 0;   // 重置计数
                return fileShardAckIndex;
            }
        } else {
            DiskSender.curFileShardIndex = fileShardAckIndex;
            DiskSender.curFileShardIndexAckCount = 1;
        }
        return -1;
    }

    @Override
    public Class<? extends MyPacket> getType() {
        return FileAckPacket.class;
    }
    
}