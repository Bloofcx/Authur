package com.cx.authur.oss.properties;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ChenXu
 * @create 2022-02-20-20:59
 */
@ConfigurationProperties(prefix = "oss.aliyun")
@Component
@Data
public class OssProperties implements InitializingBean {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public static String ENDPOINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;


    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = this.getEndpoint();
        ACCESS_KEY_ID = this.getAccessKeyId();
        ACCESS_KEY_SECRET = this.getAccessKeySecret();
        BUCKET_NAME = this.getBucketName();
    }
}
