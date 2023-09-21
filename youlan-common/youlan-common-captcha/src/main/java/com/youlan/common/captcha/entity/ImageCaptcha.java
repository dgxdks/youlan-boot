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
public class ImageCaptcha {

    @Schema(description = "验证码ID")
    private String captchaId;

    @Schema(description = "验证码图片(Base64)")
    private String captchaImg;

    @Schema(description = "验证码超时时间")
    private int codeTimeout;

    @Schema(description = "是否开启验证码")
    private Boolean captchaEnabled;
}
