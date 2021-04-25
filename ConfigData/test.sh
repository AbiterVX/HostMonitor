#!/bin/bash

#[iostat]
iostatCommand=$(iostat -x)

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
