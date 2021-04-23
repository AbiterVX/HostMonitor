#!/bin/bash

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