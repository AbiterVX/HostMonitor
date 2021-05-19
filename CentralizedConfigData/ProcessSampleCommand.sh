#!/bin/bash

#iotop -oP
#pidstat -d 1 2 | sed -n '/Average/p' | sed -e '1d'
pidstat -d 1 2
