package com.cx.authur.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.cx.authur.oss.properties.OssProperties;
import com.cx.authur.oss.service.OssService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author ChenXu
 * @create 2022-02-20-20:58
 */
@Service
public class OssServiceImpl implements OssService {

    @Override
    public String upload(String module, InputStream file,String fileName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);
        if (!ossClient.doesBucketExist(OssProperties.BUCKET_NAME)){
            ossClient.createBucket(OssProperties.BUCKET_NAME);
            ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }
        String uuid = UUID.randomUUID().toString();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        //"module/yyyy/MM/dd/UUID..."
        String dateTime = new DateTime().toString("/yyyy/MM/dd/");
        String key = module + dateTime + uuid  + suffix;
        ossClient.putObject(OssProperties.BUCKET_NAME, key, file);

// 关闭OSSClient。
        ossClient.shutdown();

        // url : https://elden.oss-cn-beijing.aliyuncs.com/authur/55710535_p0_master1200.jpg


        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT.substring(OssProperties.ENDPOINT.indexOf("//")+2) + "/" + key;
    }

    @Override
    public void remove(String url) {
        OSS ossClient = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.ACCESS_KEY_ID, OssProperties.ACCESS_KEY_SECRET);

        String key = url.substring(url.indexOf("com") + 4);
        ossClient.deleteObject(OssProperties.BUCKET_NAME,key);
// 关闭OSSClient。
        ossClient.shutdown();
    }
}
