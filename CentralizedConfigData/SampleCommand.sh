#!/bin/bash


#[net io]
# remove color and header space
netIO=$(dstat --nocolor -n 1 2 | sed -n 4p | sed "s,\x1B\[[0-9;]*[a-zA-Z],,g" | awk '$1=$1')
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
iopsInfo=$(echo "$iostatCommand" | sed -e '1,6d' | sed '/loop/d')
iostatTitle=$(echo "$iostatCommand" | sed -n '6p')

#get title index
tempReadIOPSIndex=0
tempWriteIOPSIndex=0
tempReadIndex=0
tempWriteIndex=0
tempUtilIndex=0
currentIndex=0
for i in $iostatTitle
do
  let currentIndex+=1

  if [[ $i =~ "r/s" ]]; then
    tempReadIOPSIndex=$currentIndex
  elif [[ $i =~ "w/s" ]]; then
    tempWriteIOPSIndex=$currentIndex
  elif [[ $i =~ "rkB/s" ]]; then
    tempReadIndex=$currentIndex
  elif [[ $i =~ "wkB/s" ]]; then
    tempWriteIndex=$currentIndex
  elif [[ $i =~ "util" ]]; then
    tempUtilIndex=$currentIndex
  fi

done

IFS=$(echo -en "\n\b")
for i in $iopsInfo
do
       #[iops]
       #echo $i
       tempDiskName=$(echo "$i" | awk '{print $1}')
       tempReadIOPS=$(echo "$i" | awk -v n1=$tempReadIOPSIndex '{print $n1}')
       tempWriteIOPS=$(echo "$i" | awk -v n1=$tempWriteIOPSIndex '{print $n1}')
       tempTps=$(echo "$tempReadIOPS+$tempWriteIOPS" | bc -l )
       tempRead=$(echo "$i" | awk -v n1=$tempReadIndex '{print $n1}')
       tempWrite=$(echo "$i" | awk -v n1=$tempWriteIndex '{print $n1}')
       tempUtil=$(echo "$i" | awk -v n1=$tempUtilIndex '{print $n1}')

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
cname=$( awk -F: '/model name/ {name=$2} END {print name}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//' )
cores=$( awk -F: '/model name/ {core++} END {print core}' /proc/cpuinfo )
cpuType=$cname" , with "$cores" cores"

echo "CpuType:""$cpuType"

#[os]
osType=$(head -n 1 /etc/issue)
echo "OS:""$osType"




