package com.hust.hostmonitor_web;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class HostMonitorWeb {
    public static void main(String[] args) {
        SpringApplication.run(HostMonitorWeb.class,args);
    }
}