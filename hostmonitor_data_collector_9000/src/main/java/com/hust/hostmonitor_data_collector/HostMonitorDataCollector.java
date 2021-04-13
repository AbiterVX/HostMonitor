package com.hust.hostmonitor_data_collector;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HostMonitorDataCollector {
    public static void main(String[] args) {
        SpringApplication.run(HostMonitorDataCollector.class,args);
    }
}
