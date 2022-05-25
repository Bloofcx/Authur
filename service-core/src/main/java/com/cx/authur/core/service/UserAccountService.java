package com.cx.authur.core.service;

import com.cx.authur.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface UserAccountService extends IService<UserAccount> {

    String getFormStr(BigDecimal chargeAmt, String token);

    String notify(Map<String, Object> paramMap);

    BigDecimal getAmtByUserId(Long userId);
}
