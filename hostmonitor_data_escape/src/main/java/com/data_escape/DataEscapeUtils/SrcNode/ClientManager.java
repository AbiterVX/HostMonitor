/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:17:00
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:21:06
 */
package com.data_escape.DataEscapeUtils.SrcNode;

import com.alibaba.fastjson.JSON;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.codec.NetPacket;
import com.data_escape.DataEscapeUtils.common.packet.ConnAskPacket;
import com.data_escape.DataEscapeUtils.common.packet.FilePacket;
import com.data_escape.DiskUtils.beans.ImageFileBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;

public class ClientManager implements Manager{
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    // 消息处理器
    private ClientAioHandler clientAioHandler = new ClientHandler(this);
    // 事件监听器
    private ClientAioListener clientAioListener = new ClientListener(this);
    // 连接上下文
    private ClientTioConfig clientTioConfig = new ClientTioConfig(clientAioHandler, clientAioListener);

    private TioClient tioClient = null;
    private ClientChannelContext clientChannelContext = null;
    
    // 当前的连接，同一时刻只允许一个数据逃生业务
    private String curWorkingToken = null;
    
    // ---------------------------需要传入的参数---------------------------
    // 目标节点的ip:port
    private String serverIP;
    private int serverPort;
    // 需要传输的硬盘镜像文件
    private ImageFileBean imageFileBean;
    // 每次传输的文件片大小（单位: M）
    private int fileShardSize;


    public ClientManager(String serverIP, int serverPort, String imageFileJosn, int fileShardSize) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.fileShardSize = fileShardSize;
        this.imageFileBean = JSON.parseObject(imageFileJosn, ImageFileBean.class);
    }

    /**
     * @name:       客户端初始化
     * @descrb:   
     * @param {*}
     * @return {*}
     */
    public void clientStart(){
        logger.info("[clientStart][创建数据逃生连接][长连接]");
        // 断线后自动重连 5秒重连一次，共重连n次
        ReconnConf reconnConf = new ReconnConf(5000L, 5);
        clientTioConfig.setReconnConf(reconnConf);
        // 设置心跳超时检查
        clientTioConfig.setHeartbeatTimeout(5000L);
        Node serverNode = new Node(serverIP, serverPort);
        try {
            tioClient = new TioClient(clientTioConfig);
            clientChannelContext = tioClient.connect(serverNode);
        } catch (Exception e) {
            logger.error("[clientStart][连接至服务器失败][server: {}][error info: {}]", serverNode, e.toString());
        }
    }

    /**
     * @name:     
     * @descrb:             发送链接请求，开始数据逃生
     * @param {String} msg  待发送的附加信息，这里是目标硬盘位置
     * @return {*}
     */
    public void connAsk(){
        logger.info("[connAsk][尝试建立数据逃生连接...][server: {}]", clientChannelContext.getServerNode().toString());
        // 创建连接申请包
        ConnAskPacket connAskPacket = new ConnAskPacket();
        // 设置连接请求附加信息=
        // 用网络传输包 NetPacket 包裹请求包
        NetPacket netPacket = new NetPacket();
        netPacket.setType(ConnAskPacket.TYPE);
        netPacket.setContent(connAskPacket);
        // 发送
        Tio.send(clientChannelContext, netPacket);
    }

    /**
     * @name:     
     * @descrb:             发送文件，仅当type = CLIENT_FILE_SENDER时起效
     * @param {FilePacket} filePacket
     * @return {*}
     */
    public void sendFile(FilePacket filePacket) throws InterruptedException {
        // 用网络传输包 NetPacket 包裹文件包
        NetPacket netPacket = new NetPacket();
        netPacket.setType(FilePacket.TYPE);
        netPacket.setContent(filePacket);
        // 异步发送
        Tio.send(clientChannelContext, netPacket);
    }

    /**
     * @name:     
     * @descrb:             关闭连接
     * @param {boolean} over
     * @return {*}
     */
    private void closeConn(){
        logger.info("[closeConn][准备关闭客户端连接]");
        Tio.close(clientChannelContext, "业务结束");
        clientTioConfig.setStopped(true);
        clientChannelContext.setClosed(true);
        clientChannelContext.setRemoved(true);
        clientChannelContext.setReconnect(false);
        tioClient.stop();
    }
    
    /**
     * @name:     
     * @descrb:                 关闭客户端
     * @param {boolean} over    是否通知服务端关闭
     * @return {*}
     */
    public void shutdown() {
        logger.info("[shutdown][准备关闭客户端: {}][释放连接资源...]", clientChannelContext.getClientNode().toString());
        if (!clientChannelContext.isClosed) {
            closeConn();
        }
        tioClient = null;
        clientAioHandler = null;
        clientAioListener = null;
        clientTioConfig = null;
        clientChannelContext = null;
    }

    public String getCurWorkingToken() {
        return curWorkingToken;
    }

    public void setCurWorkingToken(String curWorkingToken) {
        this.curWorkingToken = curWorkingToken;
    }

    public ImageFileBean getImageFileBean() {
        return imageFileBean;
    }

    public void setImageFileBean(ImageFileBean imageFileBean) {
        this.imageFileBean = imageFileBean;
    }

    public int getFileShardSize() {
        return fileShardSize;
    }

    public void setFileShardSize(int fileShardSize) {
        this.fileShardSize = fileShardSize;
    }
    
}