package com.cx.authur.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.entity.BorrowInfo;
import com.cx.authur.core.pojo.entity.Lend;
import com.cx.authur.core.pojo.entity.LendItemReturn;
import com.cx.authur.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface LendService extends IService<Lend> {

     Map<String,Object> getLendById(Long id);

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod);

    void makeLoan(Long lendId);

    List<LendItemReturn> returnInvest(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend);


}
