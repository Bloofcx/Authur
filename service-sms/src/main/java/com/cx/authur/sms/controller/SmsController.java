package com.cx.authur.sms.controller;

import com.cx.common.exception.Assert;
import com.cx.common.result.R;
import com.cx.common.result.ResponseEnum;
import com.cx.common.util.RandomUtils;
import com.cx.common.util.RegexValidateUtils;
import com.cx.authur.sms.cilent.SmsFeignClient;
import com.cx.authur.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenXu
 * @create 2022-02-17-17:07
 */
@RestController
@Slf4j
//@CrossOrigin
@Api(tags = "短信发送业务")
@RequestMapping("/api/sms")
public class SmsController {

    @Resource
    private SmsService smsService;

    @Resource
    SmsFeignClient smsFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}")
    public R sendSms(@ApiParam(value = "手机号码" , required = true)
            @PathVariable String mobile){
        //先判断手机号格式是否正确以及手机号是否为空

        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        Assert.notNull(mobile,ResponseEnum.MOBILE_NULL_ERROR);

        //此处为客户端 调用服务端core的服务，检查手机号是否创建
        boolean result = smsFeignClient.checkMobile(mobile);
        //断言其为false，不是false抛异常，如果服务降级了，也是false，不抛异常
        Assert.isTrue(!result,ResponseEnum.MOBILE_EXIST_ERROR);
        //生成六位验证码
        String code = RandomUtils.getSixBitRandom();
        HashMap<String,String> map = new HashMap<>();
           map.put("0",code);
//          smsService.send(mobile, SmsProperties.TEMPLATE_ID,map);

          //存入redis
          redisTemplate.opsForValue().set("api:sms:" + mobile,code,5, TimeUnit.MINUTES);

          return R.ok().message("发送短信成功");

    }
}
