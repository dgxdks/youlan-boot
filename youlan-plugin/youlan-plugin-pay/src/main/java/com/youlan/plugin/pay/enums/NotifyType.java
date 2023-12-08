package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.youlan.plugin.pay.constant.PayConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyType {
    PAY(PayConstant.NOTIFY_TYPE_PAY, "支付回调"),
    REFUND(PayConstant.NOTIFY_TYPE_REFUND, "退款回调"),
    ;

    @EnumValue
    private final String code;
    private final String desc;
}
