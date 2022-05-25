package com.cx.authur.sms.service.impl;

import com.cx.common.exception.BusinessException;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.sms.properties.SmsProperties;
import com.cx.authur.sms.service.SmsService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author ChenXu
 * @create 2022-02-17-16:45
 */

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public void send(String mobile, String templateId, Map<String, String> params) {


        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SmsProperties.SECRET_ID, SmsProperties.SECRET_KEY);


            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, SmsProperties.REGION);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String [] phoneNumbers = {mobile};

            req.setPhoneNumberSet(phoneNumbers);

            req.setSmsSdkAppId(SmsProperties.SMS_SDK_APP_ID);
            req.setSignName(SmsProperties.SIGN_NAME);
            req.setTemplateId(templateId);
             String[] templateParamSet = {params.get("0")};

            req.setTemplateParamSet(templateParamSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);


//            String responseJson = SendSmsResponse.toJsonString(resp);

//            String code = (String) map.get("Code");
//            Assert.isNotEquals(code,"Ok",ResponseEnum.ALIYUN_SMS_RESPONSE_ERROR);
//            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送SDK调用失败");
            log.error(e.toString());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR,e);
        }
    }
}
