package com.cx.authur.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Chen
 * @create 2022-04-08-23:21
 */
@Data
@ToString
@ApiModel(value = "借款人附件资料VO")
public class BorrowerAttachVO {
    @ApiModelProperty(value = "图片类型（idCard1：身份证正面，idCard2：身份证反面，house：房产证，car：车）")
    private String imageType;

    @ApiModelProperty(value = "图片路径")
    private String imageUrl;
}
