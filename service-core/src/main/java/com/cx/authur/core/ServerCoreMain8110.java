package com.cx.authur.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ChenXu
 * @create 2022-02-07-16:34
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.cx.authur","com.cx.common.exception"})
public class ServerCoreMain8110 {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ServerCoreMain8110.class,args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
