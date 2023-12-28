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
public enum RefundStatus {
    WAITING(REFUND_STATUS_WAITING, "待退款"),
    SUCCESS(REFUND_STATUS_SUCCESS, "已退款"),
    FAILED(REFUND_STATUS_FAILED, "已失败"),
    ;

    private static final Map<String, RefundStatus> REFUND_STATUS_CACHE = EnumHelper.getEnumMap(RefundStatus.class, RefundStatus::getCode);
    @EnumValue
    @JsonValue
    private final String code;
    private final String text;

    @JsonCreator
    public RefundStatus getRefundStatus(String code) {
        return REFUND_STATUS_CACHE.get(code);
    }

}
