import os
import time
import pySMART
import platform

disk_number = 0
attr_number = 134
event_number = 28
bsod_number = 46
lists = []
header = ['oper_id',
          'oper_occur_time',
          'device_name',
          'imei', 'model', 'interface', 'firmware', 'isssd', 'disksize',
          'maxtransfermode', 'currenttransfermode',
          'smart1', 'smart2', 'smart3', 'smart4', 'smart5', 'smart6', 'smart7', 'smart8', 'smart9', 'smart10',
          'smart11', 'smart12', 'smart13', 'smart14', 'smart15', 'smart16', 'smart17', 'smart18', 'smart19', 'smart20',
          'smart21', 'smart22', 'smart23', 'smart24', 'smart25', 'smart26', 'smart27', 'smart28', 'smart29', 'smart30',
          'smart31', 'smart32', 'smart33', 'smart34', 'smart35', 'smart36', 'smart37', 'smart38', 'smart39', 'smart40',
          'smart41', 'smart42', 'smart43', 'smart44', 'smart45', 'smart46', 'smart47', 'smart48',
          'event7', 'event7_total', 'event11', 'event11_total', 'event15', 'event15_total', 'event49', 'event49_total',
          'event51', 'event51_total', 'event52', 'event52_total', 'event153', 'event153_total', 'event154',
          'event154_total', 'event157', 'event157_total', 'event161', 'event161_total', 'event504', 'event504_total',
          'event505', 'event505_total', 'event506', 'event506_total', 'event507', 'event507_total',
          'bsod1', 'bsod1_total', 'bsod2', 'bsod2_total', 'bsod3', 'bsod3_total', 'bsod4', 'bsod4_total', 'bsod5',
          'bsod5_total', 'bsod6', 'bsod6_total', 'bsod7', 'bsod7_total', 'bsod8', 'bsod8_total', 'bsod9', 'bsod9_total',
          'bsod10', 'bsod10_total', 'bsod11', 'bsod11_total', 'bsod12', 'bsod12_total', 'bsod13', 'bsod13_total',
          'bsod14', 'bsod14_total', 'bsod15', 'bsod15_total', 'bsod16', 'bsod16_total', 'bsod17', 'bsod17_total',
          'bsod18', 'bsod18_total', 'bsod19', 'bsod19_total', 'bsod20', 'bsod20_total', 'bsod21', 'bsod21_total',
          'bsod22', 'bsod22_total', 'bsod23', 'bsod23_total',
          'pt_d']
save_file = "./ConfigData/Client/data.csv"


def get_disk_count():
    dev_list = pySMART.DeviceList()
    return len(dev_list.devices)


def get_oper_id():
    for i in range(disk_number):
        lists[i].append(19980803)


def get_dev_name():
    for i in range(disk_number):
        print(platform.node())
        lists[i].append(platform.node())


def get_occur_time():
    localtime = time.strftime("%Y/%m/%d %H:%M", time.localtime())
    for i in range(disk_number):
        lists[i].append(localtime)
        print("occur_time: ", localtime)


def get_transfer_mode():
    for i in range(disk_number):
        lists[i].append("")
        lists[i].append("")


def get_disk_info():
    dev_list = pySMART.DeviceList()
    for index in range(disk_number):
        physical_disk = dev_list.devices[index]
        print("\nIndex: ", index)
        print(physical_disk.name)

        print("SerialNumber: ", physical_disk.serial)
        lists[index].append(physical_disk.serial)

        print("Model: ", physical_disk.model)
        lists[index].append(physical_disk.model)

        print("InterfaceType: ", physical_disk.interface)
        lists[index].append(str(physical_disk.interface).upper())

        print("FirmwareRevision: ", physical_disk.firmware)
        lists[index].append(physical_disk.firmware)

        print("MediaType: ", physical_disk.is_ssd)
        if physical_disk.is_ssd:
            lists[index].append(1)
        else:
            lists[index].append(0)

        print("Size: ", physical_disk.capacity)
        lists[index].append(physical_disk.capacity)


def get_disk_smart():
    dev_list = pySMART.DeviceList()
    print(dev_list)
    print(len(dev_list.devices))

    for i in range(disk_number):
        for attr in dev_list.devices[i].attributes:
            if attr:
                print(attr)
                lists[i].append(attr.value)
        for j in range(59 - len(lists[i])):
            lists[i].append("")


def get_events():
    for i in range(disk_number):
        for j in range(event_number):
            lists[i].append("")


def get_bsod():
    for i in range(disk_number):
        for j in range(bsod_number):
            lists[i].append("")


def get_pt_d():
    localtime = time.strftime("%Y%m%d", time.localtime())
    for i in range(disk_number):
        lists[i].append(localtime)
        print("pt_d: ", localtime)


def save_header(file):
    for i in range(attr_number):
        file.write(header[i])
        file.write(",")
    file.write("\n")


def save_data(path):
    file = open(path, "w")
    # save_header(file)

    for i in range(disk_number):
        for j in range(attr_number):
            file.write(str(lists[i][j]))
            file.write(",")
        file.write("\n")

    # save_header(file)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    disk_number = get_disk_count()
    for i in range(disk_number):
        lists.append([])

    get_oper_id()
    get_occur_time()
    get_dev_name()
    get_disk_info()
    get_transfer_mode()
    get_disk_smart()
    get_events()
    get_bsod()
    get_pt_d()

    print(len(lists[0]))
    # self_path = os.path.abspath(__file__)
    # dir_path, filename = os.path.split(self_path)
    # save_path = os.path.join(dir_path, save_file)
    # print(self_path)
    print(save_file)

    save_data(save_file)
