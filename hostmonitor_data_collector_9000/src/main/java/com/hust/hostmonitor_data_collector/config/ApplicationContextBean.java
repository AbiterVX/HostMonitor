package com.hust.hostmonitor_data_collector.config;

import com.hust.hostmonitor_data_collector.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextBean {

    /*@Bean
    public CentralizedDataService getCentralizedDataService(){
        return new CentralizedDataServiceImpl();
    }*/

    @Bean
    public DispersedDataService getDispersedDataService(){
        return new DispersedDataServiceImpl();
    }

    @Bean
    public UserService getUserService(){
        return new UserService();
    }

    @Bean
    //@LoadBalanced  //若不使用Nacos需关闭ApplicationContextBean.RestTemplate.LoadBalanced
    public RestTemplate getDataCollectorRestTemplate()
    {
        return new RestTemplate();
    }
}
