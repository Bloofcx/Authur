package com.cx.authur.sms.properties;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ChenXu
 * @create 2022-02-17-16:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms.txyun")
public class SmsProperties implements InitializingBean {
    private String region;
    private String signName;
    private String templateId;
    private String smsSdkAppId;
    private String secretId;
    private String secretKey;

    public static String REGION;
    public static String SIGN_NAME;
    public static String TEMPLATE_ID;
    public static String SMS_SDK_APP_ID;
    public static String SECRET_ID;
    public static String SECRET_KEY;

    @Override
    public void afterPropertiesSet() throws Exception {
        //在SPRING环境配置好的时候执行
        REGION = this.getRegion();
        SIGN_NAME = this.getSignName();
        TEMPLATE_ID = this.getTemplateId();
        SMS_SDK_APP_ID = this.getSmsSdkAppId();
        SECRET_KEY = this.getSecretKey();
        SECRET_ID = this.getSecretId();
    }
}
