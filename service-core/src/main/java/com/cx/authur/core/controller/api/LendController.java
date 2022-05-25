package com.cx.authur.core.controller.api;


import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.Lend;
import com.cx.authur.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@Slf4j
@Api(tags = "标的信息")
@RequestMapping("/api/core/lend")
public class LendController {


    @Resource
    private LendService lendService;

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public R getLendList(){

        List<Lend> list = lendService.selectList();
        return R.ok().data("lendList",list);
    }


    @ApiOperation("获取标的信息和借款人信息")
    @GetMapping("/show/{id}")
    public R getLendById(@PathVariable("id") @Param("标的id") Long id){
        Map<String,Object> map = lendService.getLendById(id);
        return R.ok().data("lendDetail",map);
    }

    @ApiOperation("计算投资收益")
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalMonth}/{returnMethod}")
    public R getInterestCount(@PathVariable("invest") @ApiParam("投资金额")BigDecimal invest,
                              @PathVariable("yearRate")@ApiParam("年化利率")BigDecimal yearRate,
                              @PathVariable("totalMonth")@ApiParam("还款期数")Integer totalMonth,
                              @PathVariable("returnMethod")@ApiParam("还款方式")Integer returnMethod) {
        BigDecimal  interestCount = lendService.getInterestCount(invest, yearRate, totalMonth, returnMethod);
        return R.ok().data("interestCount", interestCount);
    }



}

