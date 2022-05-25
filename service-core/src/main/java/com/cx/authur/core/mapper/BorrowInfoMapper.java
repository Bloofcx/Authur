package com.cx.authur.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.authur.core.pojo.entity.BorrowInfo;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    List<BorrowInfo> getList();
}
