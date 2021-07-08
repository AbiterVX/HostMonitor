/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:16:32
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:12:15
 */
package com.data_escape.DataEscapeUtils.SrcNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

public class ClientListener implements ClientAioListener {
    private static final Logger logger = LoggerFactory.getLogger(ClientListener.class);

    private ClientManager clientManager;

    public ClientListener(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    /**
     * @name:                                   连接检测
     * @descrb:   
     * @param {ChannelContext} channelContext   
     * @param {boolean} isConnected             true：连接成功；false：连接失败
     * @param {boolean} isReconnect             true：是重新连接；false：是首次连接
     * @return {*}
     */
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
        if (isConnected) {
            logger.info("[ClientListener][连接至服务器成功][server: {}][client: {}]", channelContext.getServerNode().toString(), channelContext.getClientNode().toString());
            clientManager.connAsk();
        }
    }

    @Override
    /**
     * @name:                                   解码数据后
     * @descrb:   
     * @param {ChannelContext} channelContext
     * @param {Packet} packet                   数据包
     * @param {int} packetSize                  包的大小
     * @return {*}
     */
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {
        
    }

    @Override
    /**
     * @name:                                   接收TCP层传来的数据后
     * @descrb:   
     * @param {ChannelContext} channelContext   
     * @param {int} receivedBytes               接收到的字节
     * @return {*}
     */
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {
        
    }
    
    @Override
    /**
     * @name:                                   消息处理后触发
     * @descrb:   
     * @param {ChannelContext} channelContext
     * @param {Packet} packet
     * @param {long} cost                       处理所花费的时间（ms）
     * @return {*}
     */
    public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
        
    }

    @Override
    /**
     * @name:                                   发送一个消息包后
     * @descrb:     
     * @param {ChannelContext} channelContext
     * @param {Packet} packet
     * @param {boolean} isSendSuccess           true：发送成功；false：发送失败
     * @return {*}
     */
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSendSuccess) throws Exception {
        // NetPacket netPacket = (NetPacket) packet;
        // if (netPacket.getType() != null && netPacket.getType().equals(FilePacket.TYPE) && isSendSuccess) {
        //     FilePacket filePacket = JSON.parseObject(netPacket.getContent(), FilePacket.class);
        //     DiskEscape.sentFileShard[filePacket.getIndex()] = true;
        // }
    }

    @Override
    /**
     * @name:                                   关闭连接前触发
     * @descrb:   
     * @param {ChannelContext} channelContext   
     * @param {Throwable} throwable             （可能为空）
     * @param {String} remark                   （可能为空）
     * @param {boolean} isRemove
     * @return {*}
     */
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
    }
    
}