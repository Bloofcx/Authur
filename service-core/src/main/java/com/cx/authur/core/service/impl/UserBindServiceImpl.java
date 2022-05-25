package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.common.exception.Assert;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.core.UserBindEnum;
import com.cx.authur.core.hfb.FormHelper;
import com.cx.authur.core.hfb.HfbConst;
import com.cx.authur.core.hfb.RequestHelper;
import com.cx.authur.core.mapper.UserBindMapper;
import com.cx.authur.core.mapper.UserInfoMapper;
import com.cx.authur.core.pojo.entity.UserBind;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.vo.UserBindVO;
import com.cx.authur.core.service.UserBindService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Resource
    private UserBindMapper userBindMapper;

    @Resource
    private UserInfoMapper userInfoMapper;
    /**
     * 动态组装表单提交
     * @param userBindVO
     * @param userId
     */
    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        UserBind userBind = null;
        //如果身份证号已经被注册过了，且用户id不同，不可以进行注册
        //这里没有索引并且每次都要进行，需要用索引进行优化？
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card",userBindVO.getIdCard()).ne("user_id",userId);
        UserBind bind = userBindMapper.selectOne(queryWrapper);
        Assert.isNull(bind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);
        //如果用户之前已经绑定过了 ，那么可以更改他的信息
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        userBind = userBindMapper.selectOne(queryWrapper);
        if (userBind != null){
            BeanUtils.copyProperties(userBindVO,userBind);
            userBindMapper.updateById(userBind);
        }else{
            //往绑定表中添加记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);

            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            userBindMapper.insert(userBind);
        }



        //动态构建表单
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        return FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
    }

    @Override
    public void notify(Map<String, Object> paramsMap) {
        //修改绑定状态
        String bindCode = (String) paramsMap.get("bindCode");
        String agentUserId = (String) paramsMap.get("agentUserId");
        Long userId = Long.parseLong(agentUserId);
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        UserBind userBind = userBindMapper.selectOne(queryWrapper);

        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("id",userId);
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setBindCode(bindCode);
        userInfoMapper.updateById(userInfo);
        userBindMapper.updateById(userBind);

    }

    @Override
    public String getBindCodeByUserId(Long investUserId) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();

        userBindQueryWrapper.eq("user_id", investUserId);

        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);

        String bindCode = userBind.getBindCode();

        return bindCode;
    }
}
