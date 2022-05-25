package com.cx.authur.core.mapper;

import com.cx.authur.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    void updateUserAccount(@Param("amount")BigDecimal amount,
                           @Param("freezeAmount")BigDecimal freezeAmount,
                           @Param("bindCode")String bindCode);
}
