package com.youlan.plugin.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Getter
@AllArgsConstructor
public enum PayStatus {
    WAITING(PAY_STATUS_WAITING, "待支付"),
    SUCCESS(PAY_STATUS_SUCCESS, "已支付"),
    FAILED(PAY_STATUS_FAILED, "已失败"),
    CLOSED(PAY_STATUS_CLOSED, "已关闭"),
    REFUND(PAY_STATUS_REFUND, "已退款")
    ;

    private final String code;
    private final String text;

}
