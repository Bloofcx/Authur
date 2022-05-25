package com.cx.authur.core.controller.admin;

import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.LendItem;
import com.cx.authur.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Chen
 * @create 2022-04-28-16:03
 */

@RestController
@Api(tags = "标的投资")
@Slf4j
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController {
    @Resource
    private LendItemService lendItemService;

    @ApiOperation("获取列表")
    @GetMapping("list/{lendId}")
    public R list(@PathVariable("lendId")Long lendId) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return R.ok().data("list",list);
    }


}
