package com.youlan.plugin.pay.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.youlan.common.core.helper.EnumHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum PayShowType {
    URL("URL", "链接类型"),
    FORM("FORM", "Form表单类型"),
    QR_CODE("QR_CODE", "二维码类型"),
    BAR_CODE("BAR_CODE", "条形码类型"),
    CUSTOM("CUSTOM", "自定义类型"),
    ;

    private static final Map<String, PayShowType> PAY_SHOW_TYPE_CACHE = EnumHelper.getEnumMap(PayShowType.class, PayShowType::getCode);
    @EnumValue
    @JsonValue
    private final String code;
    private final String text;

    @JsonCreator
    public static PayShowType getPayShowType(String code) {
        return PAY_SHOW_TYPE_CACHE.get(code);
    }

}
