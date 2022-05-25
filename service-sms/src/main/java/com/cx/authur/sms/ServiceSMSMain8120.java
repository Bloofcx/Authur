package com.cx.authur.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ChenXu
 * @create 2022-02-17-16:27
 */
@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.cx.authur","com.cx.common"})
public class ServiceSMSMain8120 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSMSMain8120.class);
    }
}
