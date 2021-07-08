/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-07-01 11:48:03
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:19:35
 */
package com.data_escape.DiskUtils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.data_escape.DiskUtils.cmds.DISM;
import com.data_escape.common.SystemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @name: 
 * @desc: 将硬盘镜像文建恢复到目的硬盘上
 */
public class DiskRecover {
    
    private static final Logger logger = LoggerFactory.getLogger(DiskZipper.class);
    
    /**
     * @name: 
     * @desc:   将硬盘镜像文建恢复到目的硬盘上
     * @param {String} srcDiskBeanJson
     * @param {String} dstDiskBeanJson
     * @return {*}
     */
    public static boolean diskRecover(String srcDiskBeanJson, String dstDiskBeanJson, String imageFilePath) {
        boolean partitionSucess = false;
        
        PhysicalDiskBean srcDiskBean = JSON.parseObject(srcDiskBeanJson, PhysicalDiskBean.class);
        PhysicalDiskBean dstDiskBean = JSON.parseObject(dstDiskBeanJson, PhysicalDiskBean.class);
        // ########## windows / linux ##########
        if (SystemInfo.isWindows()) {
            partitionSucess = applayImage(srcDiskBean, dstDiskBean, imageFilePath);
        }else if (SystemInfo.isLinux()) {
            
        }
        // ########## windows / linux ##########
        
        return partitionSucess;
    }

    private static boolean applayImage(PhysicalDiskBean srcPDiskBean, PhysicalDiskBean dstPDiskBean, String imageFilePath) {
        logger.info("[applayImage][开始恢复镜像]");
        
        List<LogicalDiskBean> srcLDiskBeans = srcPDiskBean.getLogicalDrives();
        List<LogicalDiskBean> dstLDiskBeans = dstPDiskBean.getLogicalDrives();
        // 如果分区数量不匹配
        if (srcLDiskBeans.size() != dstLDiskBeans.size()) {
            logger.info("[applayImage][分区不匹配，无法恢复]");
            return false;
        }
        // 里路上这里是要检查一下镜像文件的，可以通过 MD5 检查
        // 开始逐个分区解压镜像
        for (int i = 0; i < srcLDiskBeans.size(); i++) {
            LogicalDiskBean srcLDiskBean = srcLDiskBeans.get(i);
            LogicalDiskBean dstLDiskBean = dstLDiskBeans.get(i);
            logger.info("[applayImage][恢复分区: {}][目标分区: {}]", DiskChecker.prettyJson(srcLDiskBean), DiskChecker.prettyJson(dstLDiskBean));

            DISM.recoverDiskFromImage(imageFilePath, dstLDiskBean.getCaption(), srcLDiskBean.getCaption());
        }
        return true;
    }
}