/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 19:59:44
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 18:23:25
 */
package com.data_escape.DiskUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.data_escape.common.ExeCmd;
import com.data_escape.common.SystemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @name:   进行目的硬盘的分区，以及在目的硬盘上恢复源硬盘
 * @desc: 
 */
public class DiskPartition {
    private static final String DISKPART_BAT = "ruabit\\src\\main\\java\\com\\data_escape\\DiskUtils\\bat\\RunDiskPart.bat";
    private static final String DISKPART_CONFIG_PATH = "ruabit\\src\\main\\java\\com\\data_escape\\DiskUtils\\bat\\";
    private static final String DISKPART_CONFIG_STUFFIX = ".txt";
    
    private static final Logger logger = LoggerFactory.getLogger(DiskZipper.class);
    /**
     * @name: 
     * @desc:           按比例对目的硬盘进行分区
     * @param {String} srcDiskBeanJson
     * @param {String} dstDiskBeanJson
     * @return {*}
     */
    public static boolean diskPartition(String pDiskBeanJson) {
        boolean partitionSucess = false;
        
        PhysicalDiskBean pDiskBean = JSON.parseObject(pDiskBeanJson, PhysicalDiskBean.class);
        // ########## windows / linux ##########
        if (SystemInfo.isWindows()) {
            partitionSucess = diskPartition_win(pDiskBean);
        }else if (SystemInfo.isLinux()) {
            
        }
        // ########## windows / linux ##########
        
        return partitionSucess;
    }
    
    /**
     * @name: 
     * @desc:   按比例对目的硬盘进行分区
     * @param {PhysicalDiskBean} srcPDiskBean
     * @param {PhysicalDiskBean} dstPDiskBean
     * @return {*}
     */
    private static boolean diskPartition_win(PhysicalDiskBean pDiskBean) {
        logger.info("[diskPartition_win][执行硬盘分区][disk: \n{}]", DiskChecker.prettyJson(pDiskBean));
        // 根据disk对象创建分区配置文件
        makeDiskPartConfig_win(pDiskBean);
        // 检查配置文件是否创建成功
        File file = new File(Paths.get(DISKPART_CONFIG_PATH, pDiskBean.getSn() + DISKPART_CONFIG_STUFFIX).toString());
        if (!file.exists() || !file.isFile()) {
            logger.error("[diskPartition_win][生成分区配置文件失败]");
            return false;
        } else {
            logger.info("[diskPartition_win][生成分区配置文件成功]");
        }
        // 开始分区，操作 .bat 批处理实现
        StringBuilder info = new StringBuilder();
        StringBuilder error = new StringBuilder();
        ExeCmd.executeCmd(new String[]{DISKPART_BAT, file.getAbsolutePath()}, null, info, error);
        // 根据返回信息判断是否分区完成
        // 。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
        return true;
    }

    /**
     * @name: 
     * @desc:   根据传进的参数配置分区信息
     * @param {PhysicalDiskBean} pDiskBean
     * @return {*}
     */
    private static void makeDiskPartConfig_win(PhysicalDiskBean pDiskBean){
        logger.info("[makeDiskPartConfig_win][生成分区配置]");
        // 将硬盘的各个分区信息按一定的规则排序，主分区放在最前面，其它按分区的盘符排序
        List<LogicalDiskBean> lDiskList = pDiskBean.getLogicalDrives();
        lDiskList.sort(new Comparator<LogicalDiskBean>(){
            public int compare(LogicalDiskBean o1, LogicalDiskBean o2) {
                if (o1.isBootPartition()) {
                    return 1;
                } else if (o2.isBootPartition()) {
                    return -1;
                } else if (o1.isPrimaryPartition()) {
                    return 1;
                } else if (o2.isPrimaryPartition()) {
                    return -1;
                }else {
                    return o1.getCaption().compareTo(o2.getCaption());
                }
            };
        });
        String config = "";
        // 首先选中目标硬盘
        int diskIndex = pDiskBean.getIndex();
        config += String.format("Rem Select Disk %d\n", diskIndex);
        config += String.format("Select Disk %d\n", diskIndex);
        config += "list partition\n\n";
        
        // 将选中磁盘格式化
        config += String.format("Rem Clean Disk %d\n", diskIndex);
        config += "clean\n";
        config += "list partition\n\n";

        // 转换分区格式
        config += String.format("convert %s noerr\n\n", pDiskBean.getType());
        
        lDiskList.forEach((pre) -> {
            String caption = pre.getCaption();
            long total = pre.getTotal();
            pre.setCaption(caption.substring(0, caption.length() - 1));
            pre.setTotal((long)(total / 1024 / 1024));
        });

        // 此时启动分区在最前方，主分区其次，扩展分区最后
        // 如果有主分区，怎先创建主分区
        int partitionIndex = 0;
        for (int i = partitionIndex; i < lDiskList.size(); i++){
            LogicalDiskBean lDiskBean = lDiskList.get(i);
            if (!lDiskBean.isPrimaryPartition()) break;
            // 创建主分区
            config += "Rem Create the primary partition on the disk\n";
            config += String.format("Create partition primary size=%s\n", lDiskBean.getTotal());                      // 创建主分区并设置大小
            config += String.format("assign letter=%s\n", lDiskBean.getCaption());                                    // 设置盘符
            config += String.format("format quick fs=%s label='%s'\n", lDiskBean.getFileSystem(), lDiskBean.getName());  // 格式化分区并设置文件系统格式和分区标签
            config += "list partition\n\n";
            
            partitionIndex ++;
        }
        if (partitionIndex < lDiskList.size()) {
            // 如果还有分区没划分完成，此时只剩下逻辑分区
            // 创建扩展分区
            config += String.format("Rem Create extended partition with %s logical divers\n", lDiskList.size() - partitionIndex);
            config += "Create partition extended\n";  // 剩下的区域全是扩展分区
            config += "list partition\n\n";
        }

        // 在扩展分区下划分逻辑分区
        for (int i = partitionIndex; i < lDiskList.size(); i++){
            LogicalDiskBean lDiskBean = lDiskList.get(i);
            config += String.format("   Rem s% logical diver\n", i);
            if (i == (lDiskList.size() - 1)) {
                // 如果是最后一个分区，则使用全部剩余空间
                config += String.format("   Create partition logical\n");
            } else {
                config += String.format("   Create partition logical size=%s\n", lDiskBean.getTotal());
            }
            config += String.format("   assign letter=%s\n", lDiskBean.getCaption());
            config += String.format("   format quick fs=%s label='%s'\n", lDiskBean.getFileSystem(), lDiskBean.getName());
            config += "\nlist partition\n\n";
        }
        // 配置文件编写完成，写入到.txt文档中
        File file = new File(Paths.get(DISKPART_CONFIG_PATH, pDiskBean.getSn() + DISKPART_CONFIG_STUFFIX).toString());
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, false);
            fw.write(config);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
