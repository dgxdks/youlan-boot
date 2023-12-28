package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.youlan.common.core.helper.EnumHelper;
import com.youlan.plugin.pay.constant.PayConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum NotifyType {
    PAY(PayConstant.NOTIFY_TYPE_PAY, "支付回调"),
    REFUND(PayConstant.NOTIFY_TYPE_REFUND, "退款回调"),
    ;

    private static final Map<String, NotifyType> NOTIFY_TYPE_CACHE = EnumHelper.getEnumMap(NotifyType.class, NotifyType::getCode);
    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public NotifyType getNotifyType(String code) {
        return NOTIFY_TYPE_CACHE.get(code);
    }

}
