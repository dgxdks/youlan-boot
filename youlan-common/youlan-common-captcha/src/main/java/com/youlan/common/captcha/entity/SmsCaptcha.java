package com.youlan.common.captcha.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SmsCaptcha {

    @Schema(description = "验证码ID")
    private String captchaId;

    @Schema(description = "验证码超时时间")
    private int codeTimeout;
}
