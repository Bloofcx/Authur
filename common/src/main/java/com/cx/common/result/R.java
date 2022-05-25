package com.cx.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ChenXu
 * @create 2022-02-07-16:40
 */
@Data
public class R {
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<>();

    /**
     * 构造函数私有化
     */
    private R(){}

    /**
     * 返回成功的实例
     * @return
     */
    public static R ok(){
        R r = new R();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 返回错误实例
     * @return
     */
    public static R error(){
        R r = new R();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 设置特定的结果
     * @param result
     * @return
     */
    public static R setResult(ResponseEnum result){
        R r = new R();
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }

    /**
     * 个性化消息
     */
    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    /**
     *  设置数据
     * @param key 数据内容的说明
     * @param value 数据
     * @return
     */
    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    /**
     * 设置数据
     * @param data Map类型的数据
     * @return
     */
    public R data(Map<String,Object> data){
        this.data(data);
        return this;
    }
}
