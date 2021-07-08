/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 10:08:57
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 18:08:02
 */
package com.data_escape.DiskUtils.beans;

public class LogicalDiskBean {
    private String name = "";
    private String caption = "";
    private String fileSystem = "";
    private boolean bootable = false;
    private boolean bootPartition = false;
    private boolean primaryPartition = false;
    private long total = 0;        // 单位byte
    private long free = 0;         // 单位byte
    private long used = 0;         // 单位byte
    private float precent = 0;

    @Override
    public String toString() {
        return String.format("\nLogicalDiskBean:\n{caption: %s\nfileSystem: %s\nbootable: %s\nbootPartition: %s\nprimaryPartition: %s\ntotal: %s\nfree: %s\nused: %s\nprecent: %s}",
                                                    caption, fileSystem, bootable, bootPartition, primaryPartition, total, free, used, precent);
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

    public float getPrecent() {
        return precent;
    }

    public void setPrecent(float precent) {
        this.precent = precent;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean isBootable() {
        return bootable;
    }

    public void setBootable(boolean bootable) {
        this.bootable = bootable;
    }

    public boolean isBootPartition() {
        return bootPartition;
    }

    public void setBootPartition(boolean bootPartition) {
        this.bootPartition = bootPartition;
    }

    public boolean isPrimaryPartition() {
        return primaryPartition;
    }

    public void setPrimaryPartition(boolean primaryPartition) {
        this.primaryPartition = primaryPartition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}