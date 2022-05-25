package com.cx.authur.core.controller.api;


import com.cx.common.result.R;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.pojo.vo.BorrowerVO;
import com.cx.authur.core.service.BorrowerService;
import com.cx.authur.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@Slf4j
@Api(tags = "借款信息申请")
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Resource
    BorrowerService borrowerService;

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("借款人信息表单提交")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowerVO borrowerVO,
                  HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowerVOByUserId(borrowerVO,userId);
        return R.ok().message("借款表单提交成功");
    }

    @ApiOperation("获取借款人状态")
    @GetMapping("/auth/getBorrowerStatus")
    public R getStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("borrowerStatus",status);
    }




}

