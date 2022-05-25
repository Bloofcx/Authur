package com.cx.authur.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ChenXu
 * @create 2022-02-23-22:26
 */

@Data
@ApiModel("登录VO")
public class LoginVO {
    @ApiModelProperty("用户类型")
    private Integer userType;
    @ApiModelProperty("用户手机")
    private String mobile;
    @ApiModelProperty("用户密码")
    private String password;
}
