'''
Descripttion: 
Version: xxx
Author: WanJu
Date: 2021-07-01 16:53:46
LastEditors: WanJu
LastEditTime: 2021-07-01 23:18:19
'''
# 详情参考
# https://docs.microsoft.com/en-us/windows/win32/cimwin32prov/win32-diskdrive
# https://docs.microsoft.com/en-us/windows/win32/cimwin32prov/win32-diskpartition
import subprocess
import json

# 查询物理硬盘的信息
disk_info = "Index, SerialNumber, DeviceID, Caption, Size"
wql_disk_info = "SELECT {} FROM Win32_DiskDrive".format(disk_info);
filter_disk_info = "Index, SerialNumber, DeviceID, Caption, Size"
GET_DISK_INFO = "Get-WmiObject -Query '{}' | Select-Object {} | ConvertTo-Json".format(wql_disk_info, filter_disk_info)


partition_info = "DiskIndex, DeviceID, Type, BootPartition"
wql_partition_info = "SELECT {} FROM Win32_DiskPartition".format(partition_info);
filter_partition_info = "DiskIndex, DeviceID,  @{Name='Type'; Expression={if ($_.Type.StartsWith('GPT')){'GPT'}else{'MBR'}}}, BootPartition"
GET_PARTITION_INFO = "Get-WmiObject -Query '{}' | Select-Object {} | ConvertTo-Json".format(wql_partition_info, filter_partition_info)


logical_disk_info = "Caption, DeviceID, FileSystem, Size, FreeSpace"
wql_logical_disk_info = "SELECT {} FROM Win32_LogicalDisk".format(logical_disk_info);
filter_logical_disk_info = "Caption, DeviceID, FileSystem, Size, FreeSpace"
GET_LOGICAL_DISK_INFO = "Get-WmiObject -Query '{}' | Select-Object {} | ConvertTo-Json".format(wql_logical_disk_info, filter_logical_disk_info)

    
pdisk_to_partition_info = "Antecedent, Dependent"
wql_pdisk_to_partition_info = "SELECT {} FROM Win32_DiskDriveToDiskPartition".format(pdisk_to_partition_info);
filter_pdisk_to_partition_info = "Antecedent, Dependent"
GET_PDISKTOPARTITION_INFO = "Get-WmiObject -Query '{}' | Select-Object {} | ConvertTo-Json".format(wql_pdisk_to_partition_info, filter_pdisk_to_partition_info)


ldisk_to_partition_info = "Antecedent, Dependent"
wql_ldisk_to_partition_info = "SELECT {} FROM Win32_LogicalDiskToPartition".format(ldisk_to_partition_info);
filter_ldisk_to_partition_info = "Antecedent, Dependent"
GET_lDISK_TO_PARTITION_INFO = "Get-WmiObject -Query '{}' | Select-Object {} | ConvertTo-Json".format(wql_ldisk_to_partition_info, filter_ldisk_to_partition_info)

def get_info_by_wql(wql:str):
    p=subprocess.Popen(["powershell", wql], stdout=subprocess.PIPE)
    info = p.stdout.read().decode("gbk")
    print(info)
    info = json.loads(info)
    return info

def combine():
    disk_info = get_info_by_wql(GET_DISK_INFO)
    partition_info = get_info_by_wql(GET_PARTITION_INFO)
    logical_info = get_info_by_wql(GET_LOGICAL_DISK_INFO)
    # d_p_info = get_info_by_wql(GET_PDISKTOPARTITION_INFO)
    l_p_info = get_info_by_wql(GET_lDISK_TO_PARTITION_INFO)

    # 首先联合分区DeviceID与逻辑硬盘DeviceID
    parse = lambda s : s.split('=')[1].replace('\\', '').replace('"', '')
    for relate in l_p_info:
        partition_device_id = parse(relate['Antecedent'])
        logical_device_id = parse(relate['Dependent'])
        for partition in partition_info:
            if partition['DeviceID'] == partition_device_id:
                for logical in logical_info:
                    if logical['DeviceID'] == logical_device_id:
                        logical['DiskIndex'] = partition['DiskIndex']
                        logical['Type'] = partition['Type']
                        logical['BootPartition'] = partition['BootPartition']
    
    # 最后合并逻辑硬盘DiskIndex与物理硬盘Index
    for disk in disk_info:
        for logical in logical_info:
            if logical['DiskIndex'] == disk['Index']:
                disk['Type'] = logical['Type']
                disk.setdefault('Logical_Disks', []).append(logical)
    # 很坑的一点java的bool是首字母小写，python首字母大写，会导致JSON解析错误（true(java)/True(python)）
    return json.dumps(str(disk_info))

if __name__ == "__main__":
    print(combine())