package com.youlan.system.enums;

import com.youlan.system.constant.SystemConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigType {
    INNER(SystemConstant.CONFIG_TYPE_INNER, "内置参数"),
    OUTER(SystemConstant.CONFIG_TYPE_OUTER, "外置参数");

    private final String code;
    private final String text;
}

