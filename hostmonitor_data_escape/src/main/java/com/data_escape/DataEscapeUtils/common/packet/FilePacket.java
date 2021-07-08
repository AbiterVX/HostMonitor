/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 10:21:04
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:09:04
 */
package com.data_escape.DataEscapeUtils.common.packet;

import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;

public class FilePacket implements MyPacket{
    public final static String TYPE = "CLIENT_FILE";

    private int totalCounts;
    private int index;
    private long totalLegth;
    private long start;
    private long lenght;
    private String stuffix;
    private String MD5;
    private String token;
    private byte[] file;

    @Override
    public String toString() {
        return String.format("MD5: %s\nToken: %s\nSthuffix: %s\nTotal Counts: %d\nTotal Length: %d\nIndex: %d\nStart: %d\nLength: %d",
                            MD5, token, stuffix, totalCounts, totalLegth, index, start, lenght);
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLenght() {
        return lenght;
    }

    public void setLenght(long lenght) {
        this.lenght = lenght;
    }

    public byte[] getFile() {
        return file;
    }
    
    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String mD5) {
        MD5 = mD5;
    }

    public String getStuffix() {
        return stuffix;
    }

    public void setStuffix(String stuffix) {
        this.stuffix = stuffix;
    }

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTotalLegth() {
        return totalLegth;
    }

    public void setTotalLegth(long totalLegth) {
        this.totalLegth = totalLegth;
    }
    
}