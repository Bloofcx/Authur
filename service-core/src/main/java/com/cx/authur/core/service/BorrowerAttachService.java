package com.cx.authur.core.service;

import com.cx.authur.core.pojo.entity.Borrower;
import com.cx.authur.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    void insertByBorrowerAttachList(List<BorrowerAttach> borrowerAttachList, Borrower borrower);

    List<BorrowerAttach> selectListByUserId(Long userId);
}
