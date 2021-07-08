/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-07-02 10:57:37
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:27:07
 */
package com.data_escape.DiskUtils.cmds;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.data_escape.common.ExeCmd;

public class CIMWin32_WMI {
    private final static String disk_info = "Index, SerialNumber, DeviceID, Caption, Size";
    private final static String wql_disk_info = String.format("SELECT %s FROM Win32_DiskDrive", disk_info);
    private final static String filter_disk_info = "Index, SerialNumber, DeviceID, Caption, Size";
    private final static String GET_DISK_INFO = String.format("Get-WmiObject -Query '%s' | Select-Object %s | ConvertTo-Json", wql_disk_info, filter_disk_info);


    private final static String partition_info = "DiskIndex, DeviceID, Type, Bootable, BootPartition, PrimaryPartition";
    private final static String wql_partition_info = String.format("SELECT %s FROM Win32_DiskPartition", partition_info);
    private final static String filter_partition_info = "DiskIndex, DeviceID,  @{Name='Type'; Expression={if ($_.Type.StartsWith('GPT')){'GPT'}else{'MBR'}}}, Bootable, BootPartition, PrimaryPartition";
    private final static String GET_PARTITION_INFO = String.format("Get-WmiObject -Query '%s' | Select-Object %s | ConvertTo-Json", wql_partition_info, filter_partition_info);


    private final static String logical_disk_info = "Caption, Name, DeviceID, FileSystem, Size, FreeSpace";
    private final static String wql_logical_disk_info = String.format("SELECT %s FROM Win32_LogicalDisk", logical_disk_info);
    private final static String filter_logical_disk_info = "Caption, Name, DeviceID, FileSystem, Size, FreeSpace";
    private final static String GET_LOGICAL_DISK_INFO = String.format("Get-WmiObject -Query '%s' | Select-Object %s | ConvertTo-Json", wql_logical_disk_info, filter_logical_disk_info);

        
    // private final static String pdisk_to_partition_info = "Antecedent, Dependent";
    // private final static String wql_pdisk_to_partition_info = String.format("SELECT %s FROM Win32_DiskDriveToDiskPartition", pdisk_to_partition_info);
    // private final static String filter_pdisk_to_partition_info = "Antecedent, Dependent";
    // private final static String GET_PDISK_TO_PARTITION_INFO = String.format("Get-WmiObject -Query '%s' | Select-Object %s | ConvertTo-Json", wql_pdisk_to_partition_info, filter_pdisk_to_partition_info);


    private final static String ldisk_to_partition_info = "Antecedent, Dependent";
    private final static String wql_ldisk_to_partition_info = String.format("SELECT %s FROM Win32_LogicalDiskToPartition", ldisk_to_partition_info);
    private final static String filter_ldisk_to_partition_info = "Antecedent, Dependent";
    private final static String GET_lDISK_TO_PARTITION_INFO = String.format("Get-WmiObject -Query '%s' | Select-Object %s | ConvertTo-Json", wql_ldisk_to_partition_info, filter_ldisk_to_partition_info);

    /**
     * @name: 
     * @desc:           获取原始硬盘信息并以JSON格式返回
     * @param {*}
     * @return {*}
     */
    public static JSONArray getDiskInfo() {
        
        JSONArray diskInfo = execWMI(GET_DISK_INFO);
        JSONArray partitionInfo = execWMI(GET_PARTITION_INFO);
        JSONArray logicalDiskInfo = execWMI(GET_LOGICAL_DISK_INFO);
        JSONArray ldiskToPartitionInfo = execWMI(GET_lDISK_TO_PARTITION_INFO);
        
        // 首先关联逻辑硬盘与分区信息
        ldiskToPartitionInfo.forEach(jsonObject -> {
            JSONObject relate = (JSONObject) jsonObject;
            String partitionDeviceId = relate.getString("Antecedent").split("=")[1].replace("\\", "").replace("\"", "");
            String logicalDiskDeviceId = relate.getString("Dependent").split("=")[1].replace("\\", "").replace("\"", "");

            partitionInfo.stream().anyMatch(jsonObject1 -> {
                JSONObject partition = (JSONObject) jsonObject1;
                if (partitionDeviceId.equalsIgnoreCase(partition.getString("DeviceID"))) {
                    logicalDiskInfo.stream().anyMatch(jsonObject2 -> {
                        JSONObject logical = (JSONObject) jsonObject2;
                        if (logicalDiskDeviceId.equalsIgnoreCase(logical.getString("DeviceID"))) {
                            logical.put("DiskIndex", partition.getIntValue("DiskIndex"));
                            logical.put("Type", partition.getString("Type"));
                            logical.put("BootPartition", partition.getBooleanValue("BootPartition"));
                            logical.put("Bootable", partition.getBooleanValue("Bootable"));
                            logical.put("PrimaryPartition", partition.getBooleanValue("PrimaryPartition"));
                            return true;
                        } else {
                            return false;
                        }
                    });
                    return true;
                } else {
                    return false;
                }
            });
        });

        // 最后关联物理硬盘与逻辑硬盘
        diskInfo.forEach(jsonObject -> {
            JSONObject disk = (JSONObject) jsonObject;
            disk.put("Logical_Disks", new JSONArray());
            logicalDiskInfo.forEach(jsonObject1 -> {
                JSONObject logical = (JSONObject) jsonObject1;
                if (logical.containsKey("DiskIndex") && disk.getIntValue("Index") == logical.getIntValue("DiskIndex")){
                    disk.put("Type", logical.getString("Type"));
                    disk.getJSONArray("Logical_Disks").add(logical);
                }
            });
        });
        
        return diskInfo;
    }

    /**
     * @name:   
     * @desc:                   执行命令获取原始硬盘信息
     * @param {String} cmd
     * @return {*}
     */
    private static JSONArray execWMI(String cmd) {
        StringBuilder info = new StringBuilder();
        ExeCmd.executeCmd(new String[] {"powershell", cmd}, null, info, null);
        JSONArray res = null;
        try {
            res = JSON.parseArray(info.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}
