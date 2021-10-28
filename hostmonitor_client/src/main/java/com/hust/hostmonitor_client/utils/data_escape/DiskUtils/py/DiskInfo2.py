'''
Descripttion: 
Version: xxx
Author: WanJu
Date: 2021-06-28 16:42:49
LastEditors: WanJu
LastEditTime: 2021-07-01 22:19:28
'''
import wmi
import subprocess

def get_disk_info() -> dict:
    scaner = wmi.WMI()
    Byte2GB = lambda x: int(x)
        #  / 1024 / 1024 / 1024 不做转换
    result = []
    for physical_disk in scaner.Win32_DiskDrive():
        disk_info = {}
        disk_info["DeviceID"] = physical_disk.DeviceID;
        disk_info["Index"] = int(physical_disk.Index)
        disk_info["Caption"] = physical_disk.Caption.strip()
        disk_info["SN"] = physical_disk.SerialNumber.strip()
        disk_info["Total"] = Byte2GB(physical_disk.Size)
        disk_info["Partitions"] = []
        # print("P\n", physical_disk)
        for partition in physical_disk.associators("Win32_DiskDriveToDiskPartition"):
            for logical_disk in partition.associators("Win32_LogicalDiskToPartition"):
                # print("L\n", logical_disk)
                partition_info = {}
                partition_info["Caption"] = logical_disk.Caption.strip()
                # partition_info["BootVolume"] = bool(logical_disk.BootVolume)
                partition_info["FileSystem"] = logical_disk.FileSystem.strip()
                partition_info["Total"] = Byte2GB(logical_disk.Size)
                partition_info["Free"] = Byte2GB(logical_disk.FreeSpace)
                partition_info["Used"] = partition_info["Total"] - partition_info["Free"]
                partition_info["Precent"] = partition_info["Used"] / partition_info["Total"]
                
                disk_info["Partitions"].append(partition_info)
        result.append(disk_info)
    return result

if __name__ == "__main__":
    print(get_disk_info())