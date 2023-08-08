package com.youlan.common.captcha.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CaptchaInfo {
    /**
     * 验证码ID
     */
    private String captchaId;

    /**
     * 验证码
     */
    private String captchaCode;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 验证码超时时间
     */
    private Long codeTimeout;
}
