package com.cx.authur.core.service;

import com.cx.authur.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    List<UserLoginRecord> getListTop50(Long userId);
}
