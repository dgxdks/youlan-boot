package com.youlan.common.captcha.config;

import com.youlan.common.captcha.enums.CodeType;
import lombok.Data;

@Data
public class SmsCaptchaProperties {
    /**
     * 验证码超时时间(s)
     */
    private int codeTimeout = 60;

    /**
     * 验证码码类型
     */
    private CodeType codeType = CodeType.NUMBER;

    /**
     * 验证码内容长度
     */
    private int codeLength = 5;
}
