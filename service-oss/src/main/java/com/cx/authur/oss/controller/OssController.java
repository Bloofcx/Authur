package com.cx.authur.oss.controller;

import com.cx.common.exception.BusinessException;
import com.cx.common.result.R;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author ChenXu
 * @create 2022-02-20-21:24
 */
@RestController
@Api(tags = "阿里云oss文件上传")
@RequestMapping("/api/oss/file")
@Slf4j
//@CrossOrigin
public class OssController {
    @Resource
    private OssService ossService;


    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public R upload(@ApiParam("文件") @RequestParam(value = "file",required = true) MultipartFile multipartFile,
                        @RequestParam(value = "module",required = true) @ApiParam("模块") String module){
        try {
            String url = ossService.upload(module, multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            return R.ok().message("上传成功").data("url",url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }


    @ApiOperation("删除文件")
    @DeleteMapping("/remove")
    public R remove(@ApiParam(value = "文件url路径",required = true)
                                @RequestParam(value = "url",required = true) String url){
        ossService.remove(url);
        return R.ok().message("删除成功");
    }

}
