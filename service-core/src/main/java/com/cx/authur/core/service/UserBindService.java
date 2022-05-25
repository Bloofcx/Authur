package com.cx.authur.core.service;

import com.cx.authur.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> paramsMap);

    String getBindCodeByUserId(Long investUserId);
}
