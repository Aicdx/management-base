package com.management.base;

import lombok.Data;

@Data
public class BaseResult<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> BaseResult<T> success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(0);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> BaseResult<T> error(String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
} 