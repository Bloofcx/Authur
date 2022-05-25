package com.cx.authur.core.controller.admin;


import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.BorrowInfo;
import com.cx.authur.core.pojo.vo.BorrowInfoApprovalVO;
import com.cx.authur.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@Slf4j
@RequestMapping("/admin/core/borrowInfo")
@Api(tags = "借款信息")
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @GetMapping("/list")
    @ApiOperation("借款信息列表")
    public R checkBorrowMoney(){
        List<BorrowInfo> list = borrowInfoService.getBorrowInfoList();
        return R.ok().data("list",list);
    }


    @GetMapping("/show/{id}")
    @ApiOperation("借款人信息")
    public R show(@PathVariable @ApiParam("借款人id") Long id){
        Map<String,Object> map = borrowInfoService.getBorrowInfoById(id);
        return R.ok().data("borrowInfoDetail",map);
    }

    @PostMapping("/approval")
    @ApiOperation("借款信息审批")
    public R approval(@RequestBody  @ApiParam("借款信息VO") BorrowInfoApprovalVO borrowInfoApprovalVO){
        borrowInfoService.approvlBorrowInfo(borrowInfoApprovalVO);
        return R.ok().message("审批成功");
    }
}

