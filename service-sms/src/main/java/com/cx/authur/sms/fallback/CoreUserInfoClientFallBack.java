package com.cx.authur.sms.fallback;

import com.cx.authur.sms.cilent.SmsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @create 2022-04-03-23:20
 */
@Service
@Slf4j
public class CoreUserInfoClientFallBack implements SmsFeignClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.debug("远程服务调用失败，服务熔断");
        return false;

    }
}
