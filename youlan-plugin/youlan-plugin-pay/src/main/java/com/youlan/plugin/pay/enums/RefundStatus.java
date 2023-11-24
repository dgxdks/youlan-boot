package com.youlan.plugin.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Getter
@AllArgsConstructor
public enum RefundStatus {
    WAITING(REFUND_STATUS_WAITING, "待退款"),
    SUCCESS(REFUND_STATUS_SUCCESS, "已退款"),
    FAILED(REFUND_STATUS_FAILED, "已失败"),
    ;

    private final String code;
    private final String text;
}
