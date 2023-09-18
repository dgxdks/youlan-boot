package com.youlan.common.core.exception;


import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.i18n.helper.MessageHelper;

public class BizRuntimeException extends RuntimeException {
    private String errorMsg = ApiResultCode.ERROR.getErrorMsg();
    private String status = ApiResultCode.ERROR.getStatus();

    public BizRuntimeException() {
        super(ApiResultCode.ERROR.getErrorMsg());
    }

    public BizRuntimeException(ApiResultCode apiResultCode) {
        super(apiResultCode.getErrorMsg());
        this.status = apiResultCode.getStatus();
        this.errorMsg = apiResultCode.getErrorMsg();
    }

    public BizRuntimeException(ApiResultCode apiResultCode, Object[] args) {
        super(apiResultCode.getErrorMsg(args));
        this.status = apiResultCode.getStatus();
        this.errorMsg = apiResultCode.getErrorMsg(args);
    }

    public BizRuntimeException(String errorMsg, Object[] args) {
        super(MessageHelper.message(errorMsg, args));
        this.errorMsg = MessageHelper.message(errorMsg, args);
    }

    public BizRuntimeException(String errorMsg) {
        super(MessageHelper.message(errorMsg));
        this.errorMsg = errorMsg;
    }

    public BizRuntimeException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg;
    }

    public BizRuntimeException(Throwable cause) {
        super(cause);
    }

    protected BizRuntimeException(String errorMsg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMsg, cause, enableSuppression, writableStackTrace);
        this.errorMsg = errorMsg;
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
