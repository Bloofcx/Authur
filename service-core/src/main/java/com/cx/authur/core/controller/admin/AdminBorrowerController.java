package com.cx.authur.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.Borrower;
import com.cx.authur.core.pojo.vo.BorrowerApprovalVO;
import com.cx.authur.core.pojo.vo.BorrowerDetailVO;
import com.cx.authur.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Chen
 * @create 2022-04-08-23:05
 */

@Api(tags = "借款人管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @GetMapping("/list/{page}/{size}")
    @ApiOperation("获取借款人列表")
    public R getBorrowerPageList(@PathVariable("page") @ApiParam("当前页码") Long page,
                                 @PathVariable("size") @ApiParam("每页记录数") Long size,
                                 @RequestParam("keyword") @ApiParam("查询关键字") String keyword){
        Page<Borrower> borrowerPage = new Page<>(page,size);
        IPage<Borrower> pageModel = borrowerService.listPage(borrowerPage,keyword);
        return R.ok().data("pageModel",pageModel);
    }


    @GetMapping("/show/{userId}")
    @ApiOperation("根据ID获取借款人信息")
    public R getBorrowerById(@ApiParam("借款人ID") @PathVariable("userId") Long userId){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerById(userId);
        return R.ok().data("borrowerDetailVO",borrowerDetailVO);
    }


    @ApiOperation("借款申请审核")
    @PostMapping("/approval")
    public void approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.dealBorrowerApproval(borrowerApprovalVO);
    }
}
