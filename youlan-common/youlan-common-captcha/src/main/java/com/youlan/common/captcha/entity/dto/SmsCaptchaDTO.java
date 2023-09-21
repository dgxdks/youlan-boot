package com.youlan.common.captcha.entity.dto;

import com.youlan.common.validator.anno.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class SmsCaptchaDTO {

    @Phone
    @NotBlank(message = "手机号码不能为空")
    @Schema(description = "手机号码")
    private String mobile;
}
