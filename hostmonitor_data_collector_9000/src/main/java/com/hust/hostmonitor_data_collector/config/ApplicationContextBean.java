package com.hust.hostmonitor_data_collector.config;

import com.hust.hostmonitor_data_collector.service.CentralizedDataService;
import com.hust.hostmonitor_data_collector.service.CentralizedDataServiceImpl;
import com.hust.hostmonitor_data_collector.service.DispersedDataService;
import com.hust.hostmonitor_data_collector.service.DispersedDataServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
