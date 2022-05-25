package com.cx.authur.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ChenXu
 * @create 2022-02-23-22:31
 */
@Data
@ApiModel("用户信息VO")
public class UserinfoVO {
    @ApiModelProperty("用户类型")
    private Integer userType;
    @ApiModelProperty("手机")
    private String mobile;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("头像")
    private String headImg;
    @ApiModelProperty("JWT令牌")
    private String jwt;
}
