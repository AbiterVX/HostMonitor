/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 10:04:35
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-01 21:17:47
 */
package com.data_escape.DiskUtils.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicalDiskBean {
    private int index = 0;
    private String sn = "";
    private String type = "";      // GPT or MBR
    private String caption = "";
    private String deviceID = "";
    private long total = 0;        // 单位byte
    private long free = 0;         // 单位byte
    private long used = 0;         // 单位byte
    private List<LogicalDiskBean> logicalDrives = new ArrayList<>();
    
    @Override
    public String toString() {
        return String.format("\nPhysicalDiskBean:\n{sn: %s\ntype: %s\ndeviceID: %s\nindex: %s\ncaption: %s\ntotal: %s\nfree: %s\nused: %s\nlogicalDrives: %s}",
                                sn, type, deviceID, index, caption, total, free, used, Arrays.toString(logicalDrives.toArray()));
    }
    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public List<LogicalDiskBean> getLogicalDrives() {
        return logicalDrives;
    }

    public void setLogicalDrives(List<LogicalDiskBean> logicalDrives) {
        this.logicalDrives = logicalDrives;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}