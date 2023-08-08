package com.youlan.common.captcha.config;

import com.youlan.common.captcha.enums.CaptchaCodeType;
import com.youlan.common.captcha.enums.CaptchaType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "youlan.common.captcha")
public class CaptchaProperties {
    /**
     * 验证码类型(LINE-线条干扰 CIRCLE-圆圈干扰 SHEAR-扭曲干扰)
     */
    private CaptchaType captchaType = CaptchaType.LINE;

    /**
     * 验证码内容类型(CHAR-字符类型 NUMBER-数字类型 CHAR_NUMBER-字符+数字类型 MATH-四则运算类型)
     */
    private CaptchaCodeType captchaCodeType = CaptchaCodeType.MATH;

    /**
     * 验证码长度
     */
    private int captchaCodeLen = 5;
}
