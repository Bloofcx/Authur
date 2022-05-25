package com.cx.authur.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.query.UserinfoQuery;
import com.cx.authur.core.pojo.vo.LoginVO;
import com.cx.authur.core.pojo.vo.RegisterVO;
import com.cx.authur.core.pojo.vo.UserinfoVO;

import java.math.BigDecimal;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface UserInfoService extends IService<UserInfo> {
   void register(RegisterVO registerVO);

    UserinfoVO login(LoginVO loginVO,String ip);

    Page<UserInfo> listByCondition(UserinfoQuery userinfoQuery, Page<UserInfo> userInfoPage);

    void lock(Long id, Integer status);

    boolean checkMobileExist(String mobile);

 BigDecimal getBorrowAmountByUserId(Long userId);
}
