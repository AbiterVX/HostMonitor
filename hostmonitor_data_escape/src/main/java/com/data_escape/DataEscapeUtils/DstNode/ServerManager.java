/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:18:02
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:24:23
 */
package com.data_escape.DataEscapeUtils.DstNode;

import java.io.IOException;

import com.data_escape.DataEscapeUtils.DiskEscape.DiskReciver;
import com.data_escape.DataEscapeUtils.common.Manager;
import com.data_escape.DataEscapeUtils.common.codec.NetPacket;
import com.data_escape.DataEscapeUtils.common.packet.FileAckPacket;
import com.data_escape.common.TokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.ChannelContext.CloseCode;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

public class ServerManager implements Manager{
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    // 消息处理器
    private ServerAioHandler serverAioHandler = new ServerHandler(this);
    // 监听器
    private ServerAioListener serverAioListener = new ServerListener(this);
    // 服务器配置
    private ServerTioConfig serverTioConfig = new ServerTioConfig("DataEscape Center Server", serverAioHandler, serverAioListener);
    // 服务器对象
    private TioServer tioServer = new TioServer(serverTioConfig);
    
    // 当前的连接，同一时刻只允许一个数据逃生业务
    private String curWorkingToken = null;
    // 若是发送方主动关闭连接，则表明流程已结束
    public final static CloseCode CLOSE_CODE = CloseCode.CLOSED_BY_PEER;
    // ---------------------------需要传入的参数---------------------------
    // 服务器的 ip:port
    private String serverIP;
    private int port;
    // 文件片的大小，以此来设定接收缓冲区的大小，单位: M
    private int fileShardSize;
    // 硬盘镜像文件保存的位置，盘符或根目录
    private String fileSavePath;

    /**
     * @name: 
     * @desc:                   构造函数
     * @param {String} serverIP
     * @param {int} port
     * @param {int} fileShardSize
     * @return {*}
     */
    public ServerManager(String serverIP, int port, int fileShardSize, String fileSavePath) {
        if (serverIP == null) {
            this.serverIP = "127.0.0.1";
        } else {
            this.serverIP = serverIP;
        }
        this.port = port;
        this.fileShardSize = fileShardSize;
        this.fileSavePath = fileSavePath;
    }

    /**
     * @name:       启动服务
     * @descrb:   
     * @param {*}
     * @return {*}
     */
    public void serverStart(){
        // 启动服务器
        // 这个地方是文件传输最关键的地方最关键，要接收文件必须有足够大的缓存区，这里设置为每个文件碎片的2倍
        serverTioConfig.setReadBufferSize(fileShardSize * 2 * 1024 * 1024);
        serverTioConfig.setHeartbeatTimeout(5000);
        try {
            tioServer.start(serverIP, port);
        } catch (IOException e) {
            logger.error("[serverStart][服务器启动失败][error info: {}]", e.getMessage());
            return;
        }
        logger.info("[serverStart][服务器启动成功]");
    }

    /**
     * @name:     
     * @descrb:                         绑定并设置当前连接Token
     * @param {ChannelContext} client
     * @return {*}
     */
    public void bindWithToken(ChannelContext client, String token) {
        Tio.bindToken(client, token);
        setCurWorkingToken(token);
        logger.info("[bindWithToken][数据逃生业务开始，绑定 Token][client: {}]", client.getClientNode().toString());
    }

    /**
     * @name: 
     * @desc:       响应客户端发送的文件包
     * @param {*}
     * @return {*}
     */
    public void startAckFile(){
        // 每隔5秒向文件发送方确认一次
        Thread ackFile = new Thread() {
            @Override
            public void run() {
                while (true) {
                // while (!isComplete()) { // 发送方可能由于网络原因接收不到完成这次确认信息，所以还是一直循环等待发送方确认传输结束并断开连接
                    FileAckPacket fileAckPacket = new FileAckPacket();
                    fileAckPacket.setSavedFileShard(DiskReciver.getStoredFileShard());
                    
                    NetPacket netPacket = new NetPacket();
                    netPacket.setType(FileAckPacket.TYPE);
                    netPacket.setContent(fileAckPacket);
                    Tio.sendToToken(serverTioConfig, curWorkingToken, netPacket);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }  
        };
        ackFile.start();
    }

    /**
     * @name:     
     * @descrb:                         当连接断开时检查是否是绑定了token
     * @param {ChannelContext} client
     * @return {*}
     */
    public void unBindWithToken(ChannelContext client) {
        if (curWorkingToken != null && TokenUtils.verifyToken(curWorkingToken, client.getToken())){
            logger.info("[unBindWithToken][数据逃生业务结束，解绑 Token][client: {}]", client.getClientNode().toString());
            curWorkingToken = null;
            Tio.unbindToken(client);
        }
    }

    /**
     * @name:     
     * @descrb:                 关闭服务器
     * @param {boolean} over    在服务端无效
     * @return {*}
     */
    public void shutdown() {
        if (!tioServer.isWaitingStop()) {
            logger.info("[shutdown][准备关闭服务器][等待10s释放资源...]");
            serverTioConfig.setStopped(true);
            tioServer.setWaitingStop(true);
            tioServer.stop();

            serverAioHandler = null;
            serverAioListener = null;
            serverTioConfig = null;
        }
    }

    public String getCurWorkingToken() {
        return curWorkingToken;
    }

    public void setCurWorkingToken(String curWorkingToken) {
        this.curWorkingToken = curWorkingToken;
    }

    public int getFileShardSize() {
        return fileShardSize;
    }

    public void setFileShardSize(int fileShardSize) {
        this.fileShardSize = fileShardSize;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }
    
}