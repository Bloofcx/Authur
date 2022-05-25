package com.cx.authur.core.pojo.bo;

import com.cx.authur.core.TransTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Chen
 * @create 2022-04-24-10:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransFlowBO {
    private String agentBillNo;

    private String bindCode;

    private BigDecimal amount;

    private TransTypeEnum transTypeEnum;

    private String memo;
}
