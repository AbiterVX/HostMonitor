import os
import time
import pySMART
import platform

lists = []
header = ['date','serial_number', 'model', 'capacity_bytes', 'failure', 'is_ssd','pt_d']
smartTag = "smart_"
smartTagAttributes = ["_normalized","_raw"]
smartCount = 256
for i in range(0,smartCount):
    for attribute in smartTagAttributes:
        header.append(smartTag+str(i)+attribute)

#写入表头
def save_header(file):
    for i in range(len(header)):
        file.write(header[i])
        file.write(",")
    file.write("\n")
#写入内容
def save_data(path):
    file = open(path, "w")
    save_header(file)
    for i in range(len(lists)):
        for j in range(len(header)):
            file.write(str(lists[i][j]))
            file.write(",")
        file.write("\n")

def getData():
    localtime = time.strftime("%Y/%m/%d %H:%M", time.localtime())
    pt_d = time.strftime("%Y%m%d", time.localtime())
    dev_list = pySMART.DeviceList()
    disk_number = len(dev_list.devices)
    for i in range(disk_number):
        lists.append([])

    # disk基本信息
    for i in range(disk_number):
        physical_disk = dev_list.devices[i]
        # date
        lists[i].append(localtime)
        # serial_number
        lists[i].append(physical_disk.serial)
        # model
        lists[i].append(physical_disk.model)
        # capacity_bytes
        lists[i].append(physical_disk.capacity)
        # failure
        lists[i].append(0)
        # is_ssd
        if physical_disk.is_ssd:
            lists[i].append(1)
        else:
            lists[i].append(0)
        # pt_d
        lists[i].append(pt_d)

    # disk Smart
    for i in range(disk_number):
        for attr in dev_list.devices[i].attributes:
            if attr:
                lists[i].append(attr.value)
                lists[i].append(attr.raw)
            else:
                lists[i].append("")
                lists[i].append("")
    # event
    # bsod
    # pt_d

#main
if __name__ == '__main__':
    getData()
    #print(lists)
    # 写文件路径
    saveFilePath = "./DiskPredict/client/sampleData/data.csv"
    #saveFilePath = "data.csv"
    save_data(saveFilePath)
    print("data_collector write complete")
