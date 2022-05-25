package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.authur.core.pojo.entity.Borrower;
import com.cx.authur.core.pojo.entity.BorrowerAttach;
import com.cx.authur.core.mapper.BorrowerAttachMapper;
import com.cx.authur.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Override
    public void insertByBorrowerAttachList(List<BorrowerAttach> borrowerAttachList, Borrower borrower) {
        borrowerAttachList.forEach((borrowerAttach) -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });
    }

    @Override
    public List<BorrowerAttach> selectListByUserId(Long userId) {
        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id",userId);
        return borrowerAttachMapper.selectList(borrowerAttachQueryWrapper);
    }


}
