/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 18:13:23
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 18:11:24
 */
package com.data_escape.DataEscapeUtils.DiskEscape;
import java.io.File;
import java.io.RandomAccessFile;

import com.data_escape.DataEscapeUtils.SrcNode.ClientManager;
import com.data_escape.DataEscapeUtils.common.packet.FilePacket;
import com.data_escape.DiskUtils.beans.ImageFileBean;

import org.apache.logging.log4j.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskSender {
    private static final Logger logger = LoggerFactory.getLogger(DiskSender.class);
    public static final int MAX_ACK_COUNT = 5; // 最大的ack次数，超过这个次数就立刻重传，大概需要5*5s
    
    // 已经确认发送的文件index、不管你接受到没有，连接断开就判断为发送完成
    // public static boolean sentFileShard[];
    public static int curFileShardIndex;
    public static int curFileShardIndexAckCount;
    
    /**
     * @name:     
     * @descrb:     开始数据逃生工作
     * @param {*}
     * @return {*}
     */
    public static void diskEscape(ClientManager clientManager, boolean isReSend, int reSendIndex) {
        // 开始传输压缩后的文件
        if (diskImageSend(clientManager, isReSend, reSendIndex)) {
            if (!isReSend)
                logger.info("[diskEscape][发送完毕，等待服务器响应]");
            else
                logger.info("[diskEscape][重传完毕，等待服务器响应]");
        } else {
            logger.error("[diskEscape][文件发送失败]");
        }
    }

    private static boolean diskImageSend(final ClientManager clientManager, boolean isReSend, int reSendIndex) {
        logger.info("[diskTransport][正在发送硬盘压缩文件...]");
        // 启用多线程 + 大文件分片 + 断点续传
        // 获取分片大小，单位(byte)
        final int shardSize = clientManager.getFileShardSize() * 1024 * 1024;
        // 检查文件是否存在
        final ImageFileBean imageFileBean = clientManager.getImageFileBean();
        final File file = new File(imageFileBean.getPath());
        if (!file.exists()) {
            logger.error("[diskImageSend][文件{}不存在]", imageFileBean.getPath());
            return false;
        }
        
        // 准备分片传输
        final long fileSize = file.length();
        final String fileStuffix = FileUtils.getFileExtension(file);                    // 获取文件后缀
        final int shardCounts = (int) Math.ceil(fileSize / (float) shardSize);          // 计算分片的总数量
        final int lastShardSize = (int) (fileSize - shardSize * (shardCounts - 1));     // 计算最后一个分片的大小
        final String MD5 = imageFileBean.getMd5();                                      // 设置文件的MD5
        // 开始分片传输
        for (int i = 0; i < shardCounts; i++) {
            // 如果当前是重传，则跳过不需要重传的线程
            final int shardIndex = i;
            if (isReSend && shardIndex != reSendIndex) continue;
            try {
                // 设置本次读取的文件长度
                int curShardSize = (shardIndex == (shardCounts - 1)) ? lastShardSize : shardSize;
                // 以随机读取的方式读取文件
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                // 定位到本次读取的位置
                randomAccessFile.seek(shardIndex * shardSize);
                // 读取文件数据
                byte[] buf = new byte[curShardSize];
                int readSize = randomAccessFile.read(buf);  // 真实读取的大小
                // 打包数据
                FilePacket filePacket = new FilePacket();
                filePacket.setFile(buf);
                filePacket.setIndex(shardIndex);
                filePacket.setLenght(readSize);
                filePacket.setMD5(MD5);
                filePacket.setStart(shardIndex * shardSize);
                filePacket.setStuffix(fileStuffix);
                filePacket.setTotalCounts(shardCounts);
                filePacket.setTotalLegth(fileSize);
                filePacket.setToken(clientManager.getCurWorkingToken());
                // 发送数据，阻塞式发送
                clientManager.sendFile(filePacket);
                logger.info("[diskImageSend][文件片发送完成，释放文件资源][index: {}]", shardIndex);
                // 发送完成后释放资源
                randomAccessFile.close();
            } catch (Exception e) {
                logger.error("[diskImageSend][文件发送错误][error info: {}]", e.toString());
            }
            if (isReSend && shardIndex == reSendIndex) return true;
        }
        return true;
    }
}