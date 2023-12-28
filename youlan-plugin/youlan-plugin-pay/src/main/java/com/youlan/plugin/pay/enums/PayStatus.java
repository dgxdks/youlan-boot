package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.youlan.common.core.helper.EnumHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Getter
@AllArgsConstructor
public enum PayStatus {
    WAITING(PAY_STATUS_WAITING, "待支付"),
    SUCCESS(PAY_STATUS_SUCCESS, "已支付"),
    CLOSED(PAY_STATUS_CLOSED, "已关闭"),
    REFUND(PAY_STATUS_REFUND, "已退款");

    private static final Map<String, PayStatus> PAY_STATUS_CACHE = EnumHelper.getEnumMap(PayStatus.class, PayStatus::getCode);
    @EnumValue
    @JsonValue
    private final String code;
    private final String text;

    @JsonCreator
    public static PayStatus getPayStatus(String code) {
        return PAY_STATUS_CACHE.get(code);
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public static boolean isSuccess(String code) {
        return SUCCESS.getCode().equals(code);
    }

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean isRefund() {
        return this == REFUND;
    }

}
