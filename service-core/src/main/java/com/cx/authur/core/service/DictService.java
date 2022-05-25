package com.cx.authur.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cx.authur.core.pojo.dto.dictExcelDto;
import com.cx.authur.core.pojo.entity.Dict;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
public interface DictService extends IService<Dict> {
    void importData(InputStream inputStream);
    List<dictExcelDto> exportData();
    List<Dict> getChildren(Long parentId);

    Long getIdByDictCode(String dictCode);

    String getNameByValAndType(Integer value, String type);
}
