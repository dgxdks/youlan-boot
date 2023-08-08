package com.youlan.common.core.exception;


import com.youlan.common.core.restful.enums.ApiResultCode;

public class BizException extends Exception {
    private String errorMsg = ApiResultCode.ERROR.getErrorMsg();
    private String status = ApiResultCode.ERROR.getStatus();

    public BizException() {
    }

    public BizException(ApiResultCode apiResultCode) {
        this.status = apiResultCode.getStatus();
        this.errorMsg = apiResultCode.getErrorMsg();
    }

    public BizException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMsg = message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
