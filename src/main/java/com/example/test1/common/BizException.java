package com.example.test1.common;

/**
 * 业务异常：用于表达可预期的业务校验失败（参数非法、数据不存在、被引用无法删除等），由 GlobalExceptionHandler 统一转换为 HTTP 400。
 */
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }
}
