/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 12:10:22
 * @LastEditors: WanJu
 * @LastEditTime: 2021-06-30 12:11:13
 */
package com.data_escape.common;

import java.security.MessageDigest;

public class MyMD5 {
    private static final String slat = "&%5123***&&%%$$#@";
    
    public static String getMD5(String dataStr) {
		try {
			dataStr = dataStr + slat;
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(dataStr.getBytes("UTF8"));
			byte s[] = m.digest();
			String result = "";
			for (int i = 0; i < s.length; i++) {
				result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}