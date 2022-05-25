import com.cx.authur.sms.ServiceSMSMain8120;
import com.cx.authur.sms.properties.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ChenXu
 * @create 2022-02-17-16:34
 */

@SpringBootTest(classes = ServiceSMSMain8120.class)
@RunWith(SpringRunner.class)
public class SmsTest {

    @Test
    public void testSmsProperties(){
        System.out.println(SmsProperties.SIGN_NAME);
        System.out.println(SmsProperties.REGION);
        System.out.println(SmsProperties.SECRET_KEY);
    }


    @Test
    public void testSend(){
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SmsProperties.SECRET_ID, SmsProperties.SECRET_KEY);


            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou");
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"17689252133"};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId(SmsProperties.SMS_SDK_APP_ID);
            req.setSignName(SmsProperties.SIGN_NAME);
            req.setTemplateId(SmsProperties.TEMPLATE_ID);

            String[] templateParamSet1 = {"454878"};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
