/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-24 09:53:45
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:36:05
 */
package com.data_escape.DataEscapeUtils.DiskEscape;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Paths;
import java.util.Arrays;

import com.data_escape.DataEscapeUtils.DstNode.ServerManager;
import com.data_escape.DataEscapeUtils.common.packet.FilePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskReciver {
    private final static Logger logger = LoggerFactory.getLogger(DiskReciver.class);
    // 默认的临时文件保存目录
    private final static String TMP_SAVE_PATH = "dataescape_recv_tmp";
    // 默认的临时文件后缀
    private final static String TMP_STUFFIX = ".tmp";
    // 已经收到并保存的文件index
    private static boolean storedFileShard[] = new boolean[0];

    private static String savedTmpFilePath = "";
    
    /**
     * @name:     
     * @descrb:                         将传输过来的镜像文件还原到目标路径
     * @param {FilePacket} filePacket
     * @param {DiskBean} diskBean
     * @return {*}
     */
    public static void store(ServerManager serverManager, FilePacket filePacket) {
        if (storedFileShard.length > 0 && storedFileShard[filePacket.getIndex()] == true) return;
        String thisThreadNmae = Thread.currentThread().getName();
        // 组成新的文件保存路径
        String savePath = Paths.get(serverManager.getFileSavePath(), TMP_SAVE_PATH).toString();
        String fileName = filePacket.getMD5();
        String tmpFilePath = Paths.get(savePath, fileName + TMP_STUFFIX).toString();
        // 创建一个空白文件，如果不存在则创建
        File tmpFile = creatEmptFile(tmpFilePath, filePacket.getTotalLegth());
        logger.info("[store][写入文件碎片][thread :{}][filePath: {}]\n[fileShardInfo: \n{}]", thisThreadNmae, tmpFilePath, filePacket.toString());
        // 往文件的指定位置写数据
        RandomAccessFile rFile = null;
        MappedByteBuffer mBuffer = null;
        FileLock fileLock = null;
        FileChannel fileChannel = null;
        final long fileStart = filePacket.getStart();
        final long fileLength = filePacket.getLenght();

        try {
            // 获取随机读写文件
            rFile = new RandomAccessFile(tmpFile, "rw");
            fileChannel = rFile.getChannel();
            // 映射到需要写入的区域
            mBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileStart, fileLength);
            // 锁住该区域
            fileLock = fileChannel.lock(fileStart, fileLength, true);
            while (fileLock == null || !fileLock.isValid()) {
                // 不知道这里会不会死锁
                fileLock = fileChannel.lock(fileStart, fileLength, true);
            }
            logger.info("[store][成功获取文件锁，开始写入...][thread :{}]", thisThreadNmae);
            mBuffer.put(filePacket.getFile());
            mBuffer.force();
        } catch (IOException e) {
            logger.error("[store][thread :{}][IOException info : {}]", thisThreadNmae, e.toString());
            e.printStackTrace();
        } catch (OverlappingFileLockException e1) {
            logger.error("[store][不合法的分片，分片区域重复][thread :{}][error info: {}]", thisThreadNmae, e1.toString());
        } catch (Exception e2) {
            logger.error("[store][thread :{}][Exception info: {}]", thisThreadNmae, e2.toString());
            e2.printStackTrace();
        } finally {
            // 释放相关资源
            releaseFileLock(fileLock);
            forceClose(mBuffer);
            close(fileChannel, rFile);
        }
        // 写入完成后修改 storedFileShard，需要上锁
        synchronized(storedFileShard) {
            if (storedFileShard.length <= 0) {
                storedFileShard = new boolean[filePacket.getTotalCounts()];
            }
            storedFileShard[filePacket.getIndex()] = true;
            logger.info("[store][文件片保存完成][thread :{}][storedFileShard: {}]", thisThreadNmae, Arrays.toString(storedFileShard));
        }
    }

    private static File creatEmptFile(String filePath, long length) {
        File tmpFile = new File(filePath);
        if (!tmpFile.getParentFile().exists()) {
            tmpFile.getParentFile().mkdirs();
        }
        if (!tmpFile.exists()) {
            savedTmpFilePath = filePath;
            logger.info("[creatEmptFile][创建空的文件: {}]", filePath);
            RandomAccessFile rFile = null;
            try {
                rFile = new RandomAccessFile(tmpFile, "rw");
                rFile.setLength(length);
            } catch (IOException e) {
                logger.info("[creatEmptFile][创建空的文件错误]", e.getMessage());
            } finally {
                if (rFile != null) {
                    try {
                        rFile.close();
                    } catch (IOException e) {
                        logger.info("[creatEmptFile][关闭空的文件]", e.getMessage());
                    }
                }
            }
        }
        return tmpFile;
    }

    private static void releaseFileLock(FileLock fileLock){
        if (fileLock == null) {
            return;
        }
        try {
            fileLock.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void close(Closeable... closeables) {
        if (closeables == null || closeables.length == 0) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void forceClose(MappedByteBuffer mBuffer){
        Class<?> unsafeClass;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            invokeCleaner.invoke(unsafe, mBuffer);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void name() {
        
    }
    
    public static boolean[] getStoredFileShard() {
        return storedFileShard;
    }
}
