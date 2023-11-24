package com.youlan.plugin.pay.exception;

import com.youlan.common.core.exception.BizRuntimeException;

public class WxApiVersionNotSupportException extends BizRuntimeException {
    public WxApiVersionNotSupportException() {
        super("不支持的微信支付API版本");
    }
}
