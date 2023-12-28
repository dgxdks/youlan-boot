package com.youlan.plugin.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseSource {
    PAY("支付"),
    PAY_QUERY("支付查询"),
    PAY_NOTIFY("支付回调"),
    REFUND("退款"),
    REFUND_QUERY("退款查询"),
    REFUND_NOTIFY("退款回调");
    private final String text;

    public boolean isPayNotify() {
        return this == PAY_NOTIFY;
    }

    public boolean isRefundNotify() {
        return this == REFUND_NOTIFY;
    }
}
