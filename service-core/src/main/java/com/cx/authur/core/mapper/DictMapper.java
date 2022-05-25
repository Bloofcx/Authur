package com.cx.authur.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.authur.core.pojo.dto.dictExcelDto;
import com.cx.authur.core.pojo.entity.Dict;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface DictMapper extends BaseMapper<Dict> {
    void insertBatch(List<dictExcelDto> list);
}
