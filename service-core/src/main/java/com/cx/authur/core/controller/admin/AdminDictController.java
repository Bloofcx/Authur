package com.cx.authur.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.cx.common.exception.BusinessException;
import com.cx.common.result.R;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.core.pojo.dto.dictExcelDto;
import com.cx.authur.core.pojo.entity.Dict;
import com.cx.authur.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author ChenXu
 * @create 2022-02-15-11:03
 */
@RestController
//@CrossOrigin
@Api(tags = "数据字典管理")
@Slf4j
@RequestMapping("admin/core/dict")
public class AdminDictController {

    @Resource
    private DictService dictService;

    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/import")
    public R batchImport(@ApiParam(value = "Excel数据文件",required = true)
             @RequestParam("file") MultipartFile multipartFile){
        try {
            dictService.importData(multipartFile.getInputStream());
            return R.ok().message("批量导入成功");
        } catch (Exception e) {
          throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }
    /**
     * 文件下载（失败了会返回一个有部分数据的Excel）
     * <p>1. 创建excel对应的实体对象 参照
     * <p>2. 设置返回的 参数
     * <p>3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     */
    @GetMapping("/export")
    @ApiOperation("数据的批量导出-有同学反应使用swagger 会导致各种问题，请直接用浏览器")
    public void export(HttpServletResponse response) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        try {
            String fileName = URLEncoder.encode("myDick", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), dictExcelDto.class).sheet("模板").doWrite(dictService.exportData());
        } catch (Exception e) {
           throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR,e);
        }
    }


    @GetMapping("/children/{parentId}")
    @ApiOperation("根据父id获取子节点")
    public R  getChildCode(@ApiParam("父节点的id")
            @PathVariable(name = "parentId")Long parentId){
        List<Dict> childrens = dictService.getChildren(parentId);
        return R.ok().data("list",childrens);
    }

}
