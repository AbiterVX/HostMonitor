package com.hust.hostmonitor_data_collector;


import com.hust.hostmonitor_data_collector.config.ApplicationContextBean;
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
}

/*
echo "Stopping storage_device_monitor-0.0.1-SNAPSHOT"
        pid=`ps -ef | grep 'java -jar ./storage_device_monitor-0.0.1-SNAPSHOT.jar' | grep -v grep | awk '{print $2}'`
        if [ -n "$pid" ]
        then
        kill -9 $pid
        fi
        cp /root/.jenkins/workspace/Spirit/spirits/target/spirits-0.0.1-SNAPSHOT.jar /AbiterVX_APP/Spirits
        DontKillMe nohup java -jar ./storage_device_monitor-0.0.1-SNAPSHOT.jar &
        */

//grep 'java -jar ./storage_device_monitor-0.0.1-SNAPSHOT.jar'
//kill -9 $pid
//nohup java -jar ./HostMonitor_Web.jar
