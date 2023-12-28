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
public enum NotifyStatus {

    NOTIFY_WAITING(PayConstant.NOTIFY_STATUS_WAITING, "等待回调"),
    NOTIFY_SUCCESS(PayConstant.NOTIFY_STATUS_SUCCESS, "回调成功"),
    NOTIFY_FAILED(PayConstant.NOTIFY_STATUS_FAILED, "回调失败"),
    REQUEST_SUCCESS(PayConstant.NOTIFY_STATUS_REQUEST_SUCCESS, "请求成功"),
    REQUEST_FAILED(PayConstant.NOTIFY_STATUS_REQUEST_FAILED, "请求失败"),
    ;

    private static final Map<String, NotifyStatus> NOTIFY_STATUS_CACHE = EnumHelper.getEnumMap(NotifyStatus.class, NotifyStatus::getCode);
    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static NotifyStatus getNotifyStatus(String code) {
        return NOTIFY_STATUS_CACHE.get(code);
    }

}
