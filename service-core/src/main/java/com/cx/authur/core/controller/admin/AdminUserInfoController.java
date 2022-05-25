package com.cx.authur.core.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.query.UserinfoQuery;
import com.cx.authur.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@RequestMapping("/admin/core/userInfo")
//@CrossOrigin
@Api(tags = "用户模块")
@Slf4j
public class AdminUserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("根据查询条件获取分页数据")
    @GetMapping("/list/{page}/{limit}")
    public R listByCond(@ApiParam("当前页码")@PathVariable("page") Long page,
                        @ApiParam("每页记录数")@PathVariable("limit")Long limit,
                        @ApiParam(value = "查询条件",required = false)UserinfoQuery userinfoQuery){
        Page<UserInfo> userInfoPage = new Page<UserInfo>(page,limit);
        Page<UserInfo> pageInfo = userInfoService.listByCondition(userinfoQuery,userInfoPage);
       return R.ok().data("page",pageInfo);
    }
    
    @ApiOperation("用户的锁定和解锁")
    @PutMapping("/lock/{id}/{status}")
    public R lockOrUnlock(@ApiParam("用户id")@PathVariable("id")Long id,
                          @ApiParam("准备修改的状态")@PathVariable("status")Integer status){
        userInfoService.lock(id,status);
        return R.ok().message(status==1?"解锁成功":"锁定成功");
    }
}

