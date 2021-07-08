/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-24 20:15:01
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:03:55
 */
package com.data_escape.DataEscapeUtils.common.packet;

import com.data_escape.DataEscapeUtils.common.dispatcher.MyPacket;

public class FileAckPacket implements MyPacket{
    public static final String TYPE = "SERVER_FILE_ACK";

    private boolean savedFileShard[];

    public boolean[] getSavedFileShard() {
        return savedFileShard;
    }

    public void setSavedFileShard(boolean[] savedFileShard) {
        this.savedFileShard = savedFileShard;
    }
    
}
