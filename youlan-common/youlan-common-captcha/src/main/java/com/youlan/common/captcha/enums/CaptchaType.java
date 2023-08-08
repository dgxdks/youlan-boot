package com.youlan.common.captcha.enums;

import lombok.Getter;

@Getter
public enum CaptchaType {
    /**
     * 线条干扰类型
     */
    LINE,
    /**
     * 圆圈干扰类型
     */
    CIRCLE,
    /**
     * 扭曲干扰类型
     */
    SHEAR,
    /**
     * 短信类型
     */
    SMS
}
