package com.cx.authur.core.controller.api;


import com.cx.common.exception.Assert;
import com.cx.common.result.R;
import com.cx.common.result.ResponseEnum;
import com.cx.common.util.RegexValidateUtils;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.pojo.vo.LoginVO;
import com.cx.authur.core.pojo.vo.RegisterVO;
import com.cx.authur.core.pojo.vo.UserinfoVO;
import com.cx.authur.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@RequestMapping("/api/core/userInfo")
//@CrossOrigin
@Api(tags = "用户模块")
@Slf4j
public class UserInfoController {


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserInfoService userInfoService;



    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R registerUser(@ApiParam("注册VO") @RequestBody RegisterVO registerVO){
            //判断手机号 ， 验证码  ， 密码不为空
            Assert.notNull(registerVO.getMobile(), ResponseEnum.MOBILE_NULL_ERROR);
            Assert.notNull(registerVO.getCode(),ResponseEnum.CODE_NULL_ERROR);
            Assert.notNull(registerVO.getPassword(),ResponseEnum.PASSWORD_NULL_ERROR);
            //判断手机号码是否正确
            Assert.isTrue(RegexValidateUtils.checkCellphone(registerVO.getMobile()),ResponseEnum.MOBILE_ERROR);


        //判断验证码是否正确
        String code = (String) redisTemplate.opsForValue().get("api:sms:" + registerVO.getMobile());
     //   log.info("code:{}",code);
        Assert.isTrue(registerVO.getCode().equals(code),ResponseEnum.CODE_ERROR);
        //注册用户
       userInfoService.register(registerVO);

        return R.ok().message("注册成功");
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@RequestBody
                   @ApiParam(value = "登录VO",required = true) LoginVO loginVO,
                   HttpServletRequest request){

        //简单判断
        Assert.notNull(loginVO.getMobile(),ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(loginVO.getMobile()),ResponseEnum.MOBILE_ERROR);
        Assert.notNull(loginVO.getPassword(),ResponseEnum.PASSWORD_NULL_ERROR);


        String ip = request.getRemoteAddr();


        UserinfoVO userinfoVO = userInfoService.login(loginVO,ip);
        return R.ok().data("userInfo",userinfoVO);
    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkJWT")
    public R checkJwt(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);
        if (result){
            return R.ok().message("校验成功");
        }else {
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }


    @ApiOperation("检查手机是否存在")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@ApiParam("手机号")@PathVariable("mobile")String mobile){
        return userInfoService.checkMobileExist(mobile);
    }
}

