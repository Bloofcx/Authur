package com.cx.authur.core.controller.admin;


import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.UserLoginRecord;
import com.cx.authur.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
//@CrossOrigin
@Api(tags = "用户登录日志")
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取用户最近50次登录记录")
    @GetMapping("/list/{id}")
    public R getListTop50(@ApiParam("用户id")@PathVariable("id")Long userId){
         List<UserLoginRecord> userLoginRecords = userLoginRecordService.getListTop50(userId);
         return R.ok().data("list",userLoginRecords);
    }
}

