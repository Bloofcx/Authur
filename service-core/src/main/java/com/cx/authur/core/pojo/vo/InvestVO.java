package com.cx.authur.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Chen
 * @create 2022-04-24-11:26
 */
@Data
@ApiModel(description = "投标信息")
public class InvestVO {
    private Long lendId;


        //投标金额

    private String investAmount;


        //用户id

    private Long investUserId;

        //用户姓名

    private String investName;
}
