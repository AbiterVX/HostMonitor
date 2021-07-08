/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:17:42
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:32:43
 */
package com.data_escape.DataEscapeUtils.DstNode;

import com.data_escape.common.TokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

public class ServerListener implements ServerAioListener{
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);

    private ServerManager serverManager;

    public ServerListener(ServerManager serverManager) {
        this.serverManager = serverManager;
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
        logger.info("[onAfterConnected][连接成功: {}][client: {}]", isConnected, channelContext.getClientNode().toString());
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
        logger.info("[onBeforeClose][对方关闭了连接，数据逃生结束][channelContext :{}][remark :{}][closeCode: {}]", channelContext, remark, channelContext.getCloseCode().toString());
        if (isRemove && channelContext.getCloseCode().equals(ServerManager.CLOSE_CODE) && TokenUtils.verifyToken(serverManager.getCurWorkingToken(), channelContext.getToken())){
            // 在这里需要把文件的后缀进行一个更改
            
            serverManager.unBindWithToken(channelContext);
            serverManager.shutdown();
        }
    }

    @Override
    /**
     * @name:                                   心跳监测机制
     * @descrb:   
     * @param {ChannelContext} channelContext   
     * @param {Long} interval                   消息间隔
     * @param {int} timeout                     心跳超时次数
     * @return {*}
     */
    public boolean onHeartbeatTimeout(ChannelContext channelContext, Long interval, int timeoutCount) {
        
        return false;
    }
    
}