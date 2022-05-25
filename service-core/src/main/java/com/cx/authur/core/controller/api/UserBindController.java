package com.cx.authur.core.controller.api;

import com.cx.common.result.R;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.hfb.RequestHelper;
import com.cx.authur.core.pojo.vo.UserBindVO;
import com.cx.authur.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Chen
 * @create 2022-04-04-8:35
 */
@RestController
@RequestMapping("api/core/userBind")
@Slf4j
@Api(tags = "用户绑定")
public class UserBindController {
    @Resource
    private UserBindService userBindService;

    @PostMapping("/auth/bind")
    @ApiOperation("账户绑定提交数据")
    public R userAuthBind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {
        // 确保用户已登录 并且得到userID
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        //根据userID组装表单提交
        String formStr = userBindService.commitBindUser(userBindVO, userId);
        return R.ok().data("formStr",formStr);
    }

    @PostMapping("/notify")
    @ApiOperation("账户绑定异步回调")
    public String notify(HttpServletRequest request){
        //得到 hfb传递来的数据
        Map<String, Object> paramsMap = RequestHelper.switchMap(request.getParameterMap());
        if (!RequestHelper.isSignEquals(paramsMap)){
            log.error("用户绑定异步回调验签失败！");
            return "fail";
        }
        userBindService.notify(paramsMap);
        return "success";
    }
}
