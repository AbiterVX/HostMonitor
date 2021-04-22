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
echo "DiskTotalUsage:""$diskTotalUsage"

#[iostat]
iostatCommand=$(iostat)

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
       tempTps=$(echo "$i" | awk '{print $2}')
       tempRead=$(echo "$i" | awk '{print $3}')
       tempWrite=$(echo "$i" | awk '{print $4}')
       echo "Disk_Iops_""$tempDiskName:""$tempTps"
       echo "Disk_Read_""$tempDiskName:""$tempRead"
       echo "Disk_Write_""$tempDiskName:""$tempWrite"

       #[disk type]
              diskType=$(smartctl --all "/dev/"$tempDiskName  | grep "Device Model" | awk '{$1="";$2="";print $0}' | awk '$1=$1')
       echo "Disk_Type_""$tempDiskName"":""$diskType"

done

#[sensors]
sensorsCommand=$(sensors);
power=$(echo "$sensorsCommand" | grep '^power[0-9]')
temperature=$(echo "$sensorsCommand" | grep '^Package id [0-9]' | awk '{print $4}')
index=1
for i in "$iopsInfo"
do
       indexTxt="$index"
       echo "Temperature_"$indexTxt":""$temperature"
       index=index+1
done
echo "Power:""$power"


#[cpu type]
cpuType=$(cat /proc/cpuinfo | grep "model name" | awk '{$1="";$2="";$3="";print $0}' | awk '$1=$1' )

echo "CpuType:""$cpuType"

#[os]
osType=$(head -n 1 /etc/issue)
echo "OS:""$osType"




