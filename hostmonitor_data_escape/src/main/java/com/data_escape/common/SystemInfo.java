/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 11:36:04
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 11:37:56
 */
package com.data_escape.common;

public class SystemInfo {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows () {
        return OS.indexOf("windows") >= 0;
    }
    
    public static boolean isLinux () {
        return OS.indexOf("linux") >= 0;
    }
}