package com.cx.authur.core.controller.api;


import com.cx.common.result.R;
import com.cx.authur.core.pojo.entity.Dict;
import com.cx.authur.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@RestController
@RequestMapping("/api/core/dict")
@Api(tags = "数据字典")
@Slf4j
public class DictController {

    @Resource
    DictService dictService;
    @GetMapping("/findByDictCode/{dictCode}")

    @ApiOperation(value = "根据dictCode获取下级节点")
    public R getListByDictCode(@ApiParam(required = true) @PathVariable(name = "dictCode") String dictCode){
        Long parentId = dictService.getIdByDictCode(dictCode);
        List<Dict> list = dictService.getChildren(parentId);
        return R.ok().data("dictList",list);
    }
}

