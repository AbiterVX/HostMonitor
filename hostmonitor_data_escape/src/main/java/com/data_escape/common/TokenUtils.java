/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-21 17:04:01
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 17:28:48
 */
package com.data_escape.common;

public class TokenUtils {
    public static String getToken(){
        return "1111111111";
    }

    public static boolean verifyToken(String serverToken, String clientToken) {
        return serverToken.equals(clientToken);
    }
    
}