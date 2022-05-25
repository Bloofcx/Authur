package com.cx.common.exception;

import com.cx.common.result.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChenXu
 * @create 2022-02-08-13:58
 */
@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    private Integer code;
    private String message;

    /**
     *
     * @param message 错误信息
     */
    public BusinessException(String message){
      this.message = message;
    }

    /**
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    /**
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常对象
     */
    public BusinessException(Integer code,String message,Throwable cause){
        super(cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResponseEnum responseEnum){
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public BusinessException(ResponseEnum responseEnum,Throwable cause){
        super(cause);
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
}
