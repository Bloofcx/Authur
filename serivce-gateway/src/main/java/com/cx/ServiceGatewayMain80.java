package com.cx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Chen
 * @create 2022-04-03-23:35
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceGatewayMain80 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayMain80.class,args);
    }
}
