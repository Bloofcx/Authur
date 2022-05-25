package com.cx.common.exception;

import com.cx.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ChenXu
 * @create 2022-02-08-14:10
 */
@Slf4j
public class Assert {
    public static void notNull(Object obj, ResponseEnum responseEnum){
        if (obj == null){
            log.info("obj is null ... ");
            throw  new BusinessException(responseEnum);
        }
    }

    public static void isTrue(Boolean bool, ResponseEnum responseEnum){
        if (!bool){
            throw new BusinessException(responseEnum);
        }
    }

    public static void isNull(Object obj,ResponseEnum responseEnum){
        if (obj != null){
            log.info("obj != null");
            throw new BusinessException(responseEnum);
        }
    }

    public static void isNotEquals(Object res,Object dest,ResponseEnum responseEnum){
        if (res.equals(dest)){
            log.error("is not equals ... ");
            throw new BusinessException(responseEnum);
        }
    }
}
