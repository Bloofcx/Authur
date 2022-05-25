package com.cx.authur.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cx.authur.core.mapper.DictMapper;
import com.cx.authur.core.pojo.dto.dictExcelDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenXu
 * @create 2022-02-15-10:38
 */
@Slf4j
@NoArgsConstructor
public class dictExcelDtoListener extends AnalysisEventListener<dictExcelDto> {
    private static final Integer BATCH_COUNT = 5;
    private List<dictExcelDto> list = new ArrayList<>();
    private DictMapper dictMapper;

    public dictExcelDtoListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }
    @Override
    public void invoke(dictExcelDto data, AnalysisContext context) {
        log.info("读取到一条数据{}",data);
         list.add(data);
         if (list.size() >= BATCH_COUNT){
             saveData();
             list.clear();
         }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
         log.info("所有数据读取完毕");
    }

    private void saveData(){
        log.info("{}条数据,开始存储到数据库",list.size());
        dictMapper.insertBatch(list);
        log.info("存储数据库成功!");
    }
}
