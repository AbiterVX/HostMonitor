/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-07-01 22:20:35
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-01 23:15:17
 */
package com.data_escape;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class test {
    public static void main(String args[]) {
        String js = "[{'Index': 1, 'SerialNumber': 'x030', 'DeviceID': 'PHYSICALDRIVE1', 'Caption': 'Kingston DataTraveler 3.0 USB Device', 'Size': 30943503360, 'Type': 'GPT', 'Logical_Disks': [{'Caption': 'F:', 'DeviceID': 'F:', 'FileSystem': 'NTFS', 'Size': 20205510656, 'FreeSpace': 19963613184, 'DiskIndex': 1, 'Type': 'GPT', 'PrimaryPartition': true}, {'Caption': 'I:', 'DeviceID': 'I:', 'FileSystem': 'NTFS', 'Size': 10736365568, 'FreeSpace': 10692743168, 'DiskIndex': 1, 'Type': 'GPT', 'PrimaryPartition': true}]}, {'Index': 2, 'SerialNumber': '2252041026312460795', 'DeviceID': 'PHYSICALDRIVE2', 'Caption': 'VendorCo ProductCode USB Device', 'Size': 31453470720, 'Type': 'MBR', 'Logical_Disks': [{'Caption': 'G:', 'DeviceID': 'G:', 'FileSystem': 'NTFS', 'Size': 20717760512, 'FreeSpace': 20660514816, 'DiskIndex': 2, 'Type': 'MBR', 'PrimaryPartition': true}, {'Caption': 'H:', 'DeviceID': 'H:', 'FileSystem': 'NTFS', 'Size': 10737414144, 'FreeSpace': 10701332480, 'DiskIndex': 2, 'Type': 'MBR', 'PrimaryPartition': true}]}, {'Index': 0, 'SerialNumber': '     WD-WCC6Y3UDN7V5', 'DeviceID': 'HYSICALDRIVE0', 'Caption': 'WDC WD10EZEX-22MFCA0', 'Size': 1000202273280, 'Type': 'GPT', 'Logical_Disks': [{'Caption': 'C:', 'DeviceID': 'C:', 'FileSystem': 'NTFS', 'Size': 104146804736, 'FreeSpace': 39609499648, 'DiskIndex': 0, 'Type': 'GPT', 'PrimaryPartition': true}, {'Caption': 'D:', 'DeviceID': 'D:', 'FileSystem': 'NTFS', 'Size': 214748360704, 'FreeSpace': 199605452800, 'DiskIndex': 0, 'Type': 'GPT', 'PrimaryPartition': true}, {'Caption': 'E:', 'DeviceID': 'E:', 'FileSystem': 'NTFS', 'Size': 680597123072, 'FreeSpace': 597342707712, 'DiskIndex': 0, 'Type': 'GPT', 'PrimaryPartition': true}]}]";
        
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(js.substring(496 ,520));
        JSONArray jsonArray = JSON.parseArray(js);
        System.out.println(jsonArray);
    }   
}