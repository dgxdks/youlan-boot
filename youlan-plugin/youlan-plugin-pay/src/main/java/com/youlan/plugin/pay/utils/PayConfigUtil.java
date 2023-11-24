package com.youlan.plugin.pay.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.plugin.pay.enums.PayType;
import com.youlan.plugin.pay.params.PayParams;

public class PayConfigUtil {

    /**
     * 获取支付参数
     *
     * @param params  支付参数
     * @param payType 支付类型
     * @return 支付参数
     */
    public static PayParams getPayParams(String params, String payType) {
        return getPayParams(params, PayType.getPayType(payType));
    }

    /**
     * 获取支付参数
     *
     * @param params  支付参数
     * @param payType 支付类型
     * @return 支付参数
     */
    public static PayParams getPayParams(String params, PayType payType) {
        // 配置参数必须是JSON格式
        Assert.isTrue(JSONUtil.isTypeJSON(params), () -> new BizRuntimeException("配置参数必须是JSON格式"));
        // 配置参数必须通过校验
        Class<? extends PayParams> paramsClass = payType.getParamsClass();
        return JSONUtil.toBean(params, paramsClass);
    }
}
