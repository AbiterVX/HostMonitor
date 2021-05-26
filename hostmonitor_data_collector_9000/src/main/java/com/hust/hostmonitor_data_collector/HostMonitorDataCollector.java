package com.hust.hostmonitor_data_collector;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
/*import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;*/
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@EnableDiscoveryClient
@SpringBootApplication
public class HostMonitorDataCollector {
    public static void main(String[] args) {
        SpringApplication.run(HostMonitorDataCollector.class,args);
    }

    //获取RestTemplate
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }


}
