package com.cx.authur.core.controller.admin;


import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.Lend;
import com.cx.authur.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@Api(tags = "标的")
@RequestMapping("/admin/core/lend")
public class AdminLendController {

    @Resource
    private LendService lendService;

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public R getLendList(){
        List<Lend> list = lendService.selectList();
        return R.ok().data("list",list);
    }


    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R getLendById(@PathVariable("id") @Param("标的id") Long id){
        Map<String,Object> map = lendService.getLendById(id);
        return R.ok().data("lendDetail",map);
    }

    @ApiOperation("满标放款")
    @GetMapping("/makeLoan/{id}")
    public R makeLoan(@PathVariable("id") @ApiParam(value = "标的id",required = true) Long lendId) {
        lendService.makeLoan(lendId);
        return R.ok().message("放款成功");
    }
}

