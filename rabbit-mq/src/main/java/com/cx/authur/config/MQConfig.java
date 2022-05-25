package com.cx.authur.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chen
 * @create 2022-05-25-14:27
 */
@Configuration
public class MQConfig {
    @Bean

    public MessageConverter messageConverter(){

        //json字符串转换器
        return new Jackson2JsonMessageConverter();
    }
}
