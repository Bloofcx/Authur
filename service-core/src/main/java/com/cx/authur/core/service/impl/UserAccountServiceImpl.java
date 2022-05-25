package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.common.exception.Assert;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.TransTypeEnum;
import com.cx.authur.core.hfb.FormHelper;
import com.cx.authur.core.hfb.HfbConst;
import com.cx.authur.core.hfb.RequestHelper;
import com.cx.authur.core.mapper.UserAccountMapper;
import com.cx.authur.core.mapper.UserInfoMapper;
import com.cx.authur.core.pojo.bo.TransFlowBO;
import com.cx.authur.core.pojo.entity.UserAccount;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.service.TransFlowService;
import com.cx.authur.core.service.UserAccountService;
import com.cx.authur.core.utils.LendNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private TransFlowService transFlowService;

    @Override
    public String getFormStr(BigDecimal chargeAmt, String token) {

        Long userId = JwtUtils.getUserId(token);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        //判断账户绑定状态
        Assert.notNull(bindCode,ResponseEnum.USER_NO_BIND_ERROR);

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
            paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
            paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());

        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);
        return FormHelper.buildForm(HfbConst.RECHARGE_URL,paramMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {


        String agentBillNo = (String) paramMap.get("agentBillNo");
        //幂等
        if (transFlowService.isExists(agentBillNo)) {
            log.warn("幂等性返回");
            return "success";
        }else {
            //账户处理
            String chargeAmt = (String) paramMap.get("chargeAmt");
            String bindCode = (String) paramMap.get("bindCode");
            userAccountMapper.updateUserAccount(new BigDecimal(chargeAmt),new BigDecimal("0"),bindCode);


            //记录流水
            TransFlowBO transFlowBO = new TransFlowBO();
            transFlowBO.setAmount(new BigDecimal(chargeAmt));
            transFlowBO.setAgentBillNo((String) paramMap.get("agentBillNo"));
            transFlowBO.setBindCode(bindCode);
            transFlowBO.setTransTypeEnum(TransTypeEnum.RECHARGE);
            transFlowBO.setMemo("来自用户绑定号为" + bindCode + "的充值");
            transFlowService.saveTransFlow(transFlowBO);
            return "success";
        }


    }

    @Override
    public BigDecimal getAmtByUserId(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        return userAccount.getAmount();
    }



}
