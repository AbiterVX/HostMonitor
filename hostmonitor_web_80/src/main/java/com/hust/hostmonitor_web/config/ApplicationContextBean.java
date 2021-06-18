package com.hust.hostmonitor_web.config;


//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class ApplicationContextBean {
    @Bean
    //@LoadBalanced  //若不使用Nacos需关闭ApplicationContextBean.RestTemplate.LoadBalanced
    public RestTemplate getRestTemplate()
    {
        String url = "222.20.95.235";
        int port = 9001;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        //Timeout
        factory.setReadTimeout(10 * 1000);
        factory.setConnectTimeout(30 * 1000);

        //代理url网址/ip, port端口
        InetSocketAddress address = new InetSocketAddress(url, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        factory.setProxy(proxy);


        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.setRequestFactory(factory);

        return restTemplate;
    }
}
