package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.authur.core.pojo.entity.UserLoginRecord;
import com.cx.authur.core.mapper.UserLoginRecordMapper;
import com.cx.authur.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> getListTop50(Long userId) {
        QueryWrapper<UserLoginRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId)
                .orderByDesc("id")
                .last("limit 50");
        return baseMapper.selectList(queryWrapper);
    }
}
