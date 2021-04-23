#!/bin/bash


#[net io]
netIO=$(dstat -n 1 1 | tail -n 1)
netIO=${netIO//B/}
netReceive=$(echo "$netIO" |  awk '{print $1}')
netSend=$(echo "$netIO" |  awk '{print $2}')
echo "NetReceive:""$netReceive"
echo "NetSend:""$netSend"

#[memory]
memoryCommand=$(head -n 5 /proc/meminfo)
memoryInfo=${memoryCommand//kB/}
memoryInfo=${memoryInfo// /}
echo "$memoryInfo";

#[net Concurrent number]
tcpEstablished=$(netstat -nat|grep ESTABLISHED|wc -l  )
echo "TcpEstablished:""$tcpEstablished "

#[disk]
diskInfo=$(df -m --total)
diskTotalInfo=$(echo "$diskInfo" | grep "total")
diskTotalSize=$(echo "$diskTotalInfo" | awk '{print $2}')
diskTotalUsage=$(echo "$diskTotalInfo" | awk '{print $5}')
diskTotalUsage=${diskTotalUsage//%/}
echo "DiskTotalSize:""$diskTotalSize"
echo "DiskOccupancyUsage:""$diskTotalUsage"

#[iostat]
iostatCommand=$(iostat -x)

#[cpu]
cpuIdle=$(echo "$iostatCommand" | sed -n '4p' | awk '{print $6}')
echo -e "CpuIdle:""$cpuIdle"

#[iops][disk type]
iopsInfo=$(echo "$iostatCommand" | sed -e '1,6d' )
#echo "$iopsInfo"
for i in "$iopsInfo"
do
       #[iops]
       tempDiskName=$(echo "$i" | awk '{print $1}') 
       tempReadIOPS=$(echo "$i" | awk '{print $2}')
       tempWriteIOPS=$(echo "$i" | awk '{print $8}')
       tempTps=$(echo "$tempReadIOPS+$tempWriteIOPS" | bc -l )

       tempRead=$(echo "$i" | awk '{print $3}')
       tempWrite=$(echo "$i" | awk '{print $9}')
       tempUtil=$(echo "$i" | awk '{print $21}')

       echo "Disk_Iops_""$tempDiskName:""$tempTps"
       echo "Disk_Read_""$tempDiskName:""$tempRead"
       echo "Disk_Write_""$tempDiskName:""$tempWrite"
       echo "Disk_Util_""$tempDiskName:""$tempUtil"

       #[disk type]
              diskType=$(smartctl --all "/dev/"$tempDiskName  | grep "Device Model" | awk '{$1="";$2="";print $0}' | awk '$1=$1')
       echo "Disk_Type_""$tempDiskName"":""$diskType"

done

#[sensors]
sensorsCommand=$(sensors);
power=$(echo "$sensorsCommand" | grep '^power[0-9]' | awk '{print $2}')
temperature=$(echo "$sensorsCommand" | grep '^Package id [0-9]'| awk '{print $3$4}' ) #
for i in $temperature
do
       data=(${i//:/ })
       packageId=${data[0]}
       currentTemperature=$(echo ${data[1]%??})
       echo "Temperature_"$packageId":""$currentTemperature"
done
echo "Power:""$power"


#[cpu type]
cpuType=$(cat /proc/cpuinfo | grep "model name" | awk '{$1="";$2="";$3="";print $0}' | awk '$1=$1' | head -n 1  )

echo "CpuType:""$cpuType"

#[os]
osType=$(head -n 1 /etc/issue)
echo "OS:""$osType"




