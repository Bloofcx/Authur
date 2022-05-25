package com.cx.authur.sms.cilent;

import com.cx.authur.sms.fallback.CoreUserInfoClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ChenXu
 * @create 2022-02-25-17:52
 */

@FeignClient(value = "service-core",fallback = CoreUserInfoClientFallBack.class)
public interface SmsFeignClient {
    @GetMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobile(@PathVariable("mobile")String mobile);
}
