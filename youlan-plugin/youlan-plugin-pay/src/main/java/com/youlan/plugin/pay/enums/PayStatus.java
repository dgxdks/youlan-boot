package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Getter
@AllArgsConstructor
public enum PayStatus {
    WAITING(PAY_STATUS_WAITING, "待支付"),
    SUCCESS(PAY_STATUS_SUCCESS, "已支付"),
    CLOSED(PAY_STATUS_CLOSED, "已关闭"),
    REFUND(PAY_STATUS_REFUND, "已退款");

    private static final ConcurrentHashMap<String, PayStatus> PAY_STATUS_CACHE = createPayStatusCache();
    @EnumValue
    @JsonValue
    private final String code;
    private final String text;

    @JsonCreator
    public static PayStatus getPayStatus(String code) {
        return PAY_STATUS_CACHE.get(code);
    }

    public static ConcurrentHashMap<String, PayStatus> createPayStatusCache() {
        ConcurrentHashMap<String, PayStatus> payConfigTypeCache = new ConcurrentHashMap<>();
        Arrays.stream(PayStatus.values())
                .forEach(payStatus -> {
                    payConfigTypeCache.put(payStatus.code, payStatus);
                });
        return payConfigTypeCache;
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

}
