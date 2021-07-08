/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 11:43:41
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:02:40
 */
package com.data_escape.DataEscapeUtils.common.dispatcher;

import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.codec.NetPacket;

import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

public class MsgDispatcher {
    private static MsgHandlerContainer handlerContainer = new MsgHandlerContainer();

    @SuppressWarnings("unchecked")
    public static void msgDispatcher (Packet packet, ChannelContext channelContext, Manager manager){
        NetPacket netPacket = (NetPacket) packet;
        Handler<MyPacket> handler = (Handler<MyPacket>) handlerContainer.getHandler(netPacket.getType());
        if (handler != null) {
            // 若消息类型未定义则不处理，例如心跳包，就不处理了
            MyPacket myPacket = MsgHandlerContainer.getMyPacket(handler, netPacket.getContent());
            handler.execute(myPacket, channelContext, manager);
        }
    }
}