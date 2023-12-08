package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.youlan.plugin.pay.constant.PayConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyStatus {
    NOTIFY_WAITING(PayConstant.NOTIFY_STATUS_WAITING, "等待回调"),
    NOTIFY_SUCCESS(PayConstant.NOTIFY_STATUS_SUCCESS, "回调成功"),
    NOTIFY_FAILED(PayConstant.NOTIFY_STATUS_FAILED, "回调失败"),
    REQUEST_SUCCESS(PayConstant.NOTIFY_STATUS_REQUEST_SUCCESS, "请求成功"),
    REQUEST_FAILED(PayConstant.NOTIFY_STATUS_REQUEST_FAILED, "请求失败"),
    ;

    @EnumValue
    private final String code;
    private final String desc;
}
