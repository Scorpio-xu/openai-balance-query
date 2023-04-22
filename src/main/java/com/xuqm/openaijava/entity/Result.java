package com.xuqm.openaijava.entity;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回结果
     */
    private T data;

    /**
     * 成功返回调用
     * 
     * @param data 数据
     * @return 结果
     * @param <T> 泛型
     */
    public static <T> Result<T> success(@Nullable T data) {
        return new Result<>(0, "成功", data);
    }

    /**
     * 失败返回调用
     * 
     * @param msg 提示信息
     * @return 结果
     * @param <T> 泛型
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(-1, msg, null);
    }
}
