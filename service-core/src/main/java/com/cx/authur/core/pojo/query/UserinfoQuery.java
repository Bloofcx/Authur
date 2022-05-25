package com.cx.authur.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ChenXu
 * @create 2022-02-24-12:30
 */
@Data
@ApiModel("用户查询对象")
public class UserinfoQuery {
    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户类型 1：出借人 2：借款人")
    private Integer userType;

    @ApiModelProperty("状态（0：锁定 1：正常）")
    private Integer status;
}
