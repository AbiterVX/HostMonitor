/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 09:59:10
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 18:08:43
 */
/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 09:59:10
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 11:26:29
 */
package com.data_escape.DiskUtils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.data_escape.DiskUtils.cmds.CIMWin32_WMI;
import com.data_escape.common.SystemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @name: 
 * @desc: 进行磁盘检查
 */
public class DiskChecker {
    private static final Logger logger = LoggerFactory.getLogger(DiskChecker.class);
    /**
     * @name: 
     * @desc:   列举一个设备上所有硬盘的信息
     * @param {String} SN
     * @return {*}  包含所有硬盘信息的JSON
     */
    public static String diskCheck() {
        // ########## windows / linux ##########
        List<PhysicalDiskBean> physicalDiskList = null;
        if (SystemInfo.isWindows()) {
            physicalDiskList = getAllDiskInfo_win();
        } else if (SystemInfo.isLinux()) {
            
        }
        // ########## windows / linux ##########
        
        if (physicalDiskList != null) {
            return JSON.toJSONString(physicalDiskList);
        } else {
            return null;
        }
    }

    /**
     * @name: 
     * @desc:   获取设备上的全部的硬盘信息
     * @param {*}
     * @return {List<PhysicalDiskBean>}
     */
    private static List<PhysicalDiskBean> getAllDiskInfo_win() {
        logger.info("[getAllDiskInfo_win][查询所有硬盘信息]");
        List<PhysicalDiskBean> physicalDiskList = new ArrayList<>();
        PhysicalDiskBean pDiskBean = null;
        // 获取所有硬盘信息
        JSONArray jsonArray = CIMWin32_WMI.getDiskInfo();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            pDiskBean = new PhysicalDiskBean();
            pDiskBean.setIndex(jsonObject.getIntValue("Index"));
            pDiskBean.setSn(jsonObject.getString("SerialNumber").strip().trim());
            pDiskBean.setType(jsonObject.getString("Type"));
            pDiskBean.setCaption(jsonObject.getString("Caption").strip());
            pDiskBean.setDeviceID(jsonObject.getString("DeviceID").strip());
            pDiskBean.setTotal(jsonObject.getLongValue("Size"));

            JSONArray logicalDisksJArray = jsonObject.getJSONArray("Logical_Disks");
            List<LogicalDiskBean> logicalDiskList = new ArrayList<>();
            long free = 0, used = 0;
            for (int j = 0; j < logicalDisksJArray.size(); j++) {
                JSONObject partitionJObject = logicalDisksJArray.getJSONObject(j);
                LogicalDiskBean lDiskBean = new LogicalDiskBean();
                lDiskBean.setName(partitionJObject.getString("Name").strip());
                lDiskBean.setCaption(partitionJObject.getString("Caption").strip());
                lDiskBean.setBootable(partitionJObject.getBooleanValue("BootPartition"));
                lDiskBean.setBootPartition(partitionJObject.getBooleanValue("BootPartition"));
                lDiskBean.setPrimaryPartition(partitionJObject.getBooleanValue("PrimaryPartition"));
                lDiskBean.setFileSystem(partitionJObject.getString("FileSystem"));
                lDiskBean.setTotal(partitionJObject.getLongValue("Size"));
                lDiskBean.setFree(partitionJObject.getLongValue("FreeSpace"));
                lDiskBean.setUsed(lDiskBean.getTotal() - lDiskBean.getFree());
                lDiskBean.setPrecent((float)lDiskBean.getUsed() / lDiskBean.getTotal());
                logicalDiskList.add(lDiskBean);
                free += lDiskBean.getFree();
                used += lDiskBean.getUsed();
            }
            pDiskBean.setLogicalDrives(logicalDiskList);
            pDiskBean.setFree(free);
            pDiskBean.setUsed(used);
            physicalDiskList.add(pDiskBean);
        }
        return physicalDiskList;
    }
    
    public static String prettyJson(Object object) {
        return JSON.toJSONString(object,
                                SerializerFeature.PrettyFormat,
                                SerializerFeature.WriteMapNullValue,
                                SerializerFeature.WriteDateUseDateFormat);
    }
}