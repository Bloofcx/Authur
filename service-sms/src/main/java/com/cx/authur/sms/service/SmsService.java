package com.cx.authur.sms.service;

import java.util.Map;

/**
 * @author ChenXu
 * @create 2022-02-17-16:43
 */
public interface SmsService {
    void send(String mobile, String templateCode, Map<String,String> params);
}
