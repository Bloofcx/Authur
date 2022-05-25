package com.cx.authur.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ChenXu
 * @create 2022-02-20-20:56
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.cx.authur","com.cx.common"})
public class ServiceOssMain8130 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssMain8130.class,args);
    }
}
