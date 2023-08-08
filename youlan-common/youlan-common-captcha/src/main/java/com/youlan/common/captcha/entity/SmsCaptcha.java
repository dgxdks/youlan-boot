package com.youlan.common.captcha.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsCaptcha {
    @Schema(title = "验证码ID")
    private String captchaId;

    @Schema(title = "手机号")
    private String mobile;

    @Schema(title = "验证码超时时间")
    private Long codeTimeout;
}
