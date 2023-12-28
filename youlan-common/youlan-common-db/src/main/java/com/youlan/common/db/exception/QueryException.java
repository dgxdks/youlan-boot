package com.youlan.common.db.exception;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;

public class QueryException extends BizRuntimeException {
    public QueryException() {
    }

    public QueryException(ApiResultCode apiResultCode) {
        super(apiResultCode);
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
