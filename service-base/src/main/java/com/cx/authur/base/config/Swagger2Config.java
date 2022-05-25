package com.cx.authur.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ChenXu
 * @create 2022-02-08-10:45
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("adminApi").apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*"))).build();
    }

//    @Bean
//    public Docket webApiConfig(){
//        return new Docket(DocumentationType.SWAGGER_2).groupName("webApi").select()
//                .paths(Predicates.and(PathSelectors.regex("/web/.*"))).build();
//    }

    @Bean
    public Docket apiConfig(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("api").select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*"))).build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder().title("Authur后台管理系统-API文档")
                .description("本文档描述了Authur后台管理系统接口")
                .version("1.0")
                .contact(new Contact("ChenXu","","1035953360@qq.com"))
                .build();
    }
}
