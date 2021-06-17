package com.hust.hostmonitor_data_collector.service;

import com.hust.hostmonitor_data_collector.dao.DiskFailureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

@Service
public class testService {
    @Autowired
    DiskFailureMapper diskFailureMapper;
//    public void insertTestData() {
//        System.out.println("OK");
//        Calendar calendar=Calendar.getInstance();
//        long time=calendar.getTimeInMillis();
//        diskFailureMapper.insertDiskDFPInfo("WDC-TEST10",new Timestamp(time),0.05,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("WDC-TEST10","testHost",512,true,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("WDC-TEST2",new Timestamp(time),0.85,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("WDC-TEST2","testHost",512,false,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("SEA-TEST1",new Timestamp(time),0.05,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("SEA-TEST1","testHost",512,true,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("sum-test1",new Timestamp(time),0.45,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sum-test1","testHost",512,true,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("sea-TEST8",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sea-TEST8","testHost",512,true,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("tos-TEST4",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("tos-TEST4","testHost",512,true,"testDiskModel");
//        diskFailureMapper.insertDiskDFPInfo("sec-TEST2",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sec-TEST2","testHost",512,false,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("sea-TEST2",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sea-TEST2","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("tos-TEST1",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("tos-TEST1","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("abc-TEST1",new Timestamp(time),0.55,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("abc-TEST1","testHost",512,false,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("def-TEST1",new Timestamp(time),0.04,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("def-TEST1","testHost",512,true,"testDiskModel");
//
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("fefg-TEST3",new Timestamp(time),0.025,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("fefg-TEST3","testHost",512,false,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("sea-TEST7",new Timestamp(time),0.95,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sea-TEST7","testHost",512,false,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("wdcs-TEST1",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("wdcs-TEST1","testHost",512,false,"testDiskModel");
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("sums-TEST3",new Timestamp(time),0.065,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sums-TEST3","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("sea-TEST14",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("sea-TEST14","testHost",512,true,"testDiskModel");
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("tos-TEST5",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("tos-TEST5","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("get-TEST3",new Timestamp(time),0.85,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("get-TEST3","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("seaaa-test3",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("seaaa-test3","testHost",512,true,"testDiskModel");
//
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis() ;
//        diskFailureMapper.insertDiskDFPInfo("seaaa-test4",new Timestamp(time),0.35,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("seaaa-test4","testHost",512,false,"testDiskModel");
//    }
    public void insertTestData() {
        System.out.println("OK");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        long time=calendar.getTimeInMillis();
        diskFailureMapper.insertDiskDFPInfo("suma-TEST13",new Timestamp(time),0.05,"testModel");
        diskFailureMapper.insertDiskHardwareInfo("suma-TEST13","testHost",512,false,"testDiskModel","127.0.0.1");
        System.out.println(time);
        System.out.println(new Timestamp(time));
        System.out.println(calendar);
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        time=calendar.getTimeInMillis();
//        diskFailureMapper.insertDiskDFPInfo("suma-TEST11",new Timestamp(time),0.05,"testModel");
//        diskFailureMapper.insertDiskHardwareInfo("suma-TEST11","testHost",512,false,"testDiskModel","127.0.0.1");

    }
}
