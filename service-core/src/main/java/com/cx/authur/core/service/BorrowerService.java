package com.cx.authur.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cx.authur.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.vo.BorrowerApprovalVO;
import com.cx.authur.core.pojo.vo.BorrowerDetailVO;
import com.cx.authur.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getStatusByUserId(Long userId);


    IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword);

    BorrowerDetailVO getBorrowerById(Long userId);


    void dealBorrowerApproval(BorrowerApprovalVO borrowerApprovalVO);
}
