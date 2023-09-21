package com.youlan.common.captcha.enums;

import lombok.Getter;

@Getter
public enum CodeType {
    /**
     * 字符类型
     */
    CHAR,
    /**
     * 数字类型
     */
    NUMBER,
    /**
     * 字符加数字类型
     */
    CHAR_NUMBER,
    /**
     * 四则运算类型
     */
    MATH;
}
