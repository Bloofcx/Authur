package com.cx.authur.core.service;

import com.cx.authur.core.pojo.bo.TransFlowBO;
import com.cx.authur.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface TransFlowService extends IService<TransFlow> {
    void saveTransFlow(TransFlowBO transFlowBO);
    boolean isExists(String agentBillNo);
}
