package com.hust.hostmonitor_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class DataSampleController_Remote {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/test/{id}")
    public String getPayment(@PathVariable("id") Integer id)
    {
        return "nacos registry, serverPort: "+ serverPort+"\t id"+id;
    }

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.data_collector_service}")
    private String dataCollectorUrl;

    @GetMapping(value = "/remote_test/{id}")
    public String remote_test(@PathVariable("id") Long id){
        return restTemplate.getForObject(dataCollectorUrl+"/test/"+id,String.class);
    }

    @GetMapping(value = "/remote_test_2")
    public String remote_test_2(){
        return restTemplate.getForObject(dataCollectorUrl+ "/test_2",String.class);
    }
}
