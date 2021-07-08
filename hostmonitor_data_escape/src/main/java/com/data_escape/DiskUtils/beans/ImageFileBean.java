/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 12:00:29
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 15:25:04
 */
package com.data_escape.DiskUtils.beans;

public class ImageFileBean {
    private String path;
    private long size;
    private String md5;

    @Override
    public String toString() {
        return "ImageFileBean [md5=" + md5 + ", path=" + path + ", size=" + size + "]";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }
    
    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
