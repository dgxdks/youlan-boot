package com.youlan.common.exception;

import com.youlan.common.enums.ResultEnum;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private String code = ResultEnum.ERROR.getCode();

    public BaseException(String module, String code, String message) {
        super(message);
        this.module = module;
        this.code = code;
    }

    public BaseException(ResultEnum resultEnum) {
        this(null, resultEnum.getCode(), resultEnum.getMsg());
    }

    public BaseException(String module, ResultEnum resultEnum) {
        this(module, resultEnum.getCode(), resultEnum.getMsg());
    }

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
