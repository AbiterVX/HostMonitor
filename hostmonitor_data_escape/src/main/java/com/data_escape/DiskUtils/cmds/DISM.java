/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-25 17:58:30
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:24:03
 */
package com.data_escape.DiskUtils.cmds;

import java.io.File;
import com.data_escape.common.ExeCmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DISM {
    private final static Logger logger = LoggerFactory.getLogger(DISM.class);
    // 具体命令参见：https://docs.microsoft.com/zh-cn/windows-hardware/manufacture/desktop/dism-image-management-command-line-options-s14
    public final static String Stuffix = ".wim";
    private final static String Dism = "dism";                      // 主命令
    private final static String CaptureImage = "/Capture-Image";    // 子命令——制作硬盘镜像
    private final static String AppendImage = "/Append-Image";      // 子命令——追加硬盘镜像
    private final static String ApplyImage = "/Apply-Image";        // 子命令——还原硬盘镜像
    private final static String GetImageInfo = "/Get-ImageInfo";    // 子命令——获取镜像文件所有信息

    /**
     * @name:     
     * @descrb:                         制作分区的镜像（理论上应该是要整盘都做成一个镜像，后续可以使用增量镜像，把一个盘上的所有盘符都备份到一个镜像中）
     * @param {boolean} first           是否是第一个压缩的硬盘分区
     * @param {String} imageFilePath    镜像文件的保存路径
     * @param {String} captureFilePath  需要压缩的盘的路径
     * @param {String} name             镜像的名称（一个wim文件中可以存放多个镜像，镜像名称绝不能重复）
     * @param {String} desc             镜像的描述
     * @return {*}
     */
    public static boolean makeImageFromDisk(boolean first, String imageFilePath, String captureDir, String name, String desc) {
        logger.info("[makeImageFromDisk][imageFilePath: {}][captureDir: {}][name: {}][desc: {}]", imageFilePath, captureDir, name, desc);
        String cmdStr = null;
        if (first) {
            cmdStr = CaptureImage;
        } else {
            cmdStr = AppendImage;
        }
        String cmd[] = new String[] {
            Dism,
            cmdStr,
            String.format("/ImageFile:%s", imageFilePath),
            String.format("/CaptureDir:%s", captureDir),
            String.format("/Name:%s", name),
            String.format("/Description:%s", desc),
        };
        ExeCmd.executeCmd(cmd, null, null, null);
        return true;
    }

    /**
     * @name:     
     * @descrb:                         读取镜像文件中所有的镜像信息
     * @param {String} imageFilePath
     * @return {*}
     */
    public static String getInfoFromImage(String imageFilePath) {
        logger.info("[getInfoFromImage][imageFilePath: {}]", imageFilePath);
        File file = new File(imageFilePath);
        if (!file.exists()) {
            logger.error("[makeImageFromDisk][error: {}]", "要压缩的路径不存在");
            return null;
        }
        String cmd[] = new String[] {
            Dism,
            GetImageInfo,
            String.format("/ImageFile:%s", imageFilePath),
        };
        StringBuilder info = new StringBuilder();
        ExeCmd.executeCmd(cmd, null, info, null);
        // 解析输出，获取硬盘实例
        
        return info.toString();
    }

    /**
     * @name:     
     * @descrb:                         从镜像文件wim中还原一个镜像
     * @param {String} imageFilePath    镜像文件路径
     * @param {String} applyDiskPath    需要还原到的目的硬盘路径
     * @param {String} name             需要还原ed镜像的NAME，唯一标识
     * @return {*}
     */
    public static boolean recoverDiskFromImage(String imageFilePath, String applyDir, String Name) {
        logger.info("[recoverDiskFromImage][imageFilePath: {}][applyDir: {}]", imageFilePath, applyDir);
        String cmd[] = new String[] {
            Dism,
            ApplyImage,
            String.format("/ImageFile:%s", imageFilePath),
            String.format("/ApplyDir:%s", applyDir),
            String.format("/Name:%s", Name),
        };
        ExeCmd.executeCmd(cmd, null, null, null);
        return true;
    }
}