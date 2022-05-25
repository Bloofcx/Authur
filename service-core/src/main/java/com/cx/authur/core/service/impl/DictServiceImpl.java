package com.cx.authur.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.authur.core.listener.dictExcelDtoListener;
import com.cx.authur.core.mapper.DictMapper;
import com.cx.authur.core.pojo.dto.dictExcelDto;
import com.cx.authur.core.pojo.entity.Dict;
import com.cx.authur.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    RedisTemplate redisTemplate;

    /**
     * 导入excel
     * @param inputStream
     */
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, dictExcelDto.class,new dictExcelDtoListener(baseMapper)).sheet().doRead();
        log.info("importData finished");
    }

    /**
     * 导出excel
     * @return
     */
    @Override
    public List<dictExcelDto> exportData() {
        List<Dict> dicts = baseMapper.selectList(null);
        List<dictExcelDto> dictExcelDtos = new ArrayList<>(dicts.size());
        dicts.forEach(dict -> {
            dictExcelDto dictExcelDto = new dictExcelDto();
            BeanUtils.copyProperties(dict,dictExcelDto);
            dictExcelDtos.add(dictExcelDto);
        });
        return dictExcelDtos;
    }

    /**
     * 根据父节点获取其子节点
     * @param parentId
     * @return
     */
    @Override
    public List<Dict> getChildren(Long parentId) {

        //如果缓存出现异常 , 我们需要将代码正常往下执行
        //先判断 缓存中是否有数据
        List<Dict> list = null;
        try {
            list = (List<Dict>) redisTemplate.opsForValue().get("core:dict:" + parentId);
            if ( list != null){
                //有的话就从缓存中获取 返回
                log.info("从缓存中取值");
                return list;
            }
        } catch (Exception e) {
            log.error("redis服务异常" + ExceptionUtils.getStackTrace(e)); //此处不抛出异常 继续执行后面的代码
        }

            //没有的话从数据库中获取
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id",parentId);
            list= baseMapper.selectList(queryWrapper);

            list.forEach(dict -> {
                //判断子节点列表中的每个元素是否还有子节点
                dict.setHasChildren(hasChildren(dict.getId()));
            });

            //将该数据存入缓存
        try {
            redisTemplate.opsForValue().set("core:dict:" + parentId,list,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis服务异常" + ExceptionUtils.getStackTrace(e)); //此处不抛出异常 继续执行后面的代码
        }
        return list;

    }

    @Override
    public Long getIdByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        return dict.getId();
    }

    @Override
    public String getNameByValAndType(Integer value, String type) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code",type);
        Dict parentDict = baseMapper.selectOne(queryWrapper);
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentDict.getId())
                .eq("value",value);
        Dict dict = baseMapper.selectOne(queryWrapper);
        return dict.getName();
    }

    private Boolean hasChildren(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
