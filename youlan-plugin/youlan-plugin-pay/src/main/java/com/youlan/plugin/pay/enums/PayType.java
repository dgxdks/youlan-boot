package com.youlan.plugin.pay.enums;

import cn.hutool.core.lang.Assert;
import com.youlan.common.core.helper.EnumHelper;
import com.youlan.plugin.pay.params.AliPayParams;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.params.WxPayParams;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum PayType {

    WECHAT("WECHAT", "微信支付", WxPayParams.class),
    ALIPAY("ALIPAY", "支付宝支付", AliPayParams.class);

    private static final Map<String, PayType> PAY_TYPE_CACHE = EnumHelper.getEnumMap(PayType.class, PayType::getCode);
    private final String code;
    private final String text;
    private final Class<? extends PayParams> paramsClass;

    public static PayType getPayType(String type) {
        PayType payType = PAY_TYPE_CACHE.get(type);
        Assert.notNull(payType, "支付类型不存在");
        return payType;
    }

    public static Class<? extends PayParams> getParamsClass(String type) {
        return getPayType(type).getParamsClass();
    }
}
