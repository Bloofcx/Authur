package com.cx.authur.core.controller.api;

import com.cx.common.result.R;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.pojo.entity.BorrowInfo;
import com.cx.authur.core.service.BorrowInfoService;
import com.cx.authur.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author Chen
 * @create 2022-04-10-23:57
 */
@RestController
@Api(tags = "借款信息")
@Slf4j
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取当前用户借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowerAmount = userInfoService.getBorrowAmountByUserId(userId);
        return R.ok().data("borrowAmount",borrowerAmount);
    }


    @ApiOperation("提交借款信息")
    @PostMapping("/auth/save")
    public R saveBorrowInfo(@RequestBody @ApiParam(name = "借款提交信息")BorrowInfo borrowInfo,
                            HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowInfo(borrowInfo,userId);
        return R.ok().message("提交借款信息成功");
    }


    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowStatusByUserId(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowInfoService.getBorrowStatus(userId);
        return R.ok().data("borrowInfoStatus",status);
    }
}
