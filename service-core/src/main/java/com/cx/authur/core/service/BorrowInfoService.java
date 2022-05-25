package com.cx.authur.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.entity.BorrowInfo;
import com.cx.authur.core.pojo.vo.BorrowInfoApprovalVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface BorrowInfoService extends IService<BorrowInfo> {


    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    Integer getBorrowStatus(Long userId);

    List<BorrowInfo> getBorrowInfoList();

    Map<String, Object> getBorrowInfoById(Long id);

    void approvlBorrowInfo(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
