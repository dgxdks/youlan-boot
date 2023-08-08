package com.youlan.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginStatus {
    SUCCESS("1", "登录成功"),
    FAILED("2", "登录失败"),
    LOCKED("3", "登录锁定");

    private final String code;
    private final String text;
}
