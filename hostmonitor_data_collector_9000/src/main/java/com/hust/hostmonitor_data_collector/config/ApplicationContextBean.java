package com.hust.hostmonitor_data_collector.config;

import com.hust.hostmonitor_data_collector.service.DataService;
import com.hust.hostmonitor_data_collector.service.DataServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextBean {

    @Bean
    public DataService getDataService(){
        return new DataServiceImpl();
    }
}
