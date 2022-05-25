package com.cx.authur.core.controller.admin;

import com.cx.common.exception.Assert;
import com.cx.common.result.R;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.core.pojo.entity.IntegralGrade;
import com.cx.authur.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ChenXu
 * @create 2022-02-08-10:25
 */
@RestController
@Slf4j
@RequestMapping("/admin/core/integralGrade")
//@CrossOrigin
@Api(tags = "积分等级管理")
public class AdminIntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;

    /**
     * 获取积分等级列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("积分等级列表")
    public R getIntegralGradeList(){
        log.info("hi i am cx");
        log.warn("warning!!!");
        log.error("It is a error !");
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list);
    }
    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "根据id删除积分等级" , notes = "逻辑删除")
    public R deleteIntegralGradeById(@PathVariable("id") @ApiParam(value = "数据id",required = true,example = "100") Integer id){
        boolean removed = integralGradeService.removeById(id);
        if (removed){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation("根据id获取积分等级")
    @GetMapping("/get/{id}")
    public R getIntegralGradeById(@ApiParam(value = "数据id",required = true,example = "1")
                                              @PathVariable("id") Integer id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade == null){
            return R.error().message("查询结果不存在!");
        }else {
            return R.ok().data("record",integralGrade);
        }
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R saveIntegralGrade(@ApiParam(value = "积分等级对象",required = true)
                               @RequestBody IntegralGrade integralGrade){
//        if (integralGrade.getBorrowAmount() == null){
//            throw  new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
//        }
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        boolean saved = integralGradeService.save(integralGrade);
        if (saved){
            return R.ok().message("保存成功");
        }else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("修改积分等级")
    @PutMapping("/update")
    public R updateIntegralGrade(@ApiParam(value = "积分等级对象",required = true)
                                     @RequestBody IntegralGrade integralGrade){
        boolean updated = integralGradeService.updateById(integralGrade);
        if (updated){
            return R.ok().message("修改成功");
        }else {
            return R.error().message("修改失败");
        }
    }


}
