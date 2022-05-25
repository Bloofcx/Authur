package com.cx.authur.core.controller.api;


import com.cx.common.result.R;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.hfb.RequestHelper;
import com.cx.authur.core.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@Slf4j
@Api(tags = "用户账户")
@RequestMapping("/api/core/userAccount")
public class UserAccountController {
    @Resource
    private UserAccountService userAccountService;

    @ApiOperation("充值表单提交")
    @RequestMapping(value = "/auth/commitCharge/{chargeAmt}",method = RequestMethod.POST)
    public R commitCharge(@PathVariable(name = "chargeAmt") @ApiParam("充值金额") BigDecimal chargeAmt, HttpServletRequest request) {
        String token = request.getHeader("token");
        String formStr = userAccountService.getFormStr(chargeAmt,token);
        return R.ok().data("formStr",formStr);
    }


    @PostMapping("/notify")
    @ApiOperation("充值异步回调")
    public String chargeNotify(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = RequestHelper.switchMap(parameterMap);
        parameterMap = null;

        if (RequestHelper.isSignEquals(paramMap)) {
            if (!"0001".equals(paramMap.get("resultCode"))) {
                //业务失败
                log.info("业务失败 防止hfb重试");
                return "success";
            } else {
                //业务成功
                return userAccountService.notify(paramMap);
            }
        } else {
            log.info("用户充值异步回调签名错误");
            return "fail";
        }
    }

    @GetMapping("/auth/getAccount")
    @ApiOperation("查询账户余额")
    public R getAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal amount = userAccountService.getAmtByUserId(userId);
        return R.ok().data("account",amount);
    }
}

