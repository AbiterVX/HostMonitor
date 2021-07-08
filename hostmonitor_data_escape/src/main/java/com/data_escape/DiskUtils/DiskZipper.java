/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 11:25:21
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 15:36:48
 */
package com.data_escape.DiskUtils;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.data_escape.DiskUtils.beans.ImageFileBean;
import com.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.data_escape.DiskUtils.cmds.DISM;
import com.data_escape.common.MyMD5;
import com.data_escape.common.SystemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @name: 
 * @desc: 进行磁盘压缩与解压
 */
public class DiskZipper {
    private static final String DISK_ZIP_TMP_PATH = "dataescape_send_tmp";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    
    private static final Logger logger = LoggerFactory.getLogger(DiskZipper.class);

    /**
     * @name: 
     * @desc:                           压缩硬盘
     * @param {String} diskBeanJson     JSON格式的硬盘信息
     * @param {String} savePath
     * @return {*}
     */
    public static String diskZip(String diskBeanJson, String savePath) {
        // 将 JSON 数据转换为 PhysicalDiskBean
        PhysicalDiskBean pDiskBean = JSON.parseObject(diskBeanJson, PhysicalDiskBean.class);
        ImageFileBean imageFileBean = null;
        // ########## windows / linux ##########
        if (SystemInfo.isWindows()) {
            imageFileBean = diskZip_win(pDiskBean, savePath);
        }else if (SystemInfo.isLinux()) {
            
        }
        // ########## windows / linux ##########
        if (imageFileBean != null) {
            return JSON.toJSONString(imageFileBean);
        }
        return null;
    }
    
    /**
     * @name: 
     * @desc:                               压缩一块硬盘
     * @param {PhysicalDiskBean} pDiskBean  硬盘对象，包含了硬盘的各种信息
     * @param {String} savePath             压缩文件的保存位置
     * @return {*}
     */
    private static ImageFileBean diskZip_win(PhysicalDiskBean pDiskBean, String savePath) {
        logger.info("[DiskZip_win][开始压缩硬盘][Disk: {}]", DiskChecker.prettyJson(pDiskBean));
        // 通过 时间 + sn码 + 压缩文件后缀 组成文件名
        String imageFilePath = Paths.get(savePath, DISK_ZIP_TMP_PATH, format.format(new Date()) + "-" + pDiskBean.getSn() + DISM.Stuffix).toString();
        // 新建临时目录
        File file = new File(imageFilePath);
        if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        
        List<LogicalDiskBean> logicalDiskList= pDiskBean.getLogicalDrives();
        for (int i = 0; i < logicalDiskList.size(); i++) {
            DISM.makeImageFromDisk(
                i == 0, 
                imageFilePath, 
                logicalDiskList.get(i).getCaption(),    // 需要压缩的盘符
                logicalDiskList.get(i).getCaption(),    // 以盘符作为镜像名
                format.format(new Date()));
        }

        File imageFile = new File(imageFilePath);
        // 如果压缩之后该文件不存在，则表示压缩失败
        if (!imageFile.exists() || !imageFile.isFile()) {
            logger.error("[DiskZip_win][压缩硬盘失败]");
            return null;
        }
        
        ImageFileBean imageFileBean = new ImageFileBean();
        imageFileBean.setPath(imageFilePath);
        imageFileBean.setSize(imageFile.length());
        // 将文件的大小转化为 MD5 码，可以更改
        imageFileBean.setMd5(MyMD5.getMD5(String.valueOf(imageFileBean.getSize())));

        return imageFileBean;
    }
}