package com.youlan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum {
    OK("200", "SUCCESS"),
    ERROR("500", "SUCCESS");

    private final String code;
    private final String msg;
}
