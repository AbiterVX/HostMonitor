package com.hust.hostmonitor_web.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextBean {
    @Bean
    //@LoadBalanced  //若不使用Nacos需关闭ApplicationContextBean.RestTemplate.LoadBalanced
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }
}
