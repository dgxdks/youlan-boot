package com.youlan.common.captcha.entity.dto;

import com.youlan.common.validator.anno.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class SmsCaptchaDTO {

    @Phone
    @NotBlank(message = "手机号码不能为空")
    @Schema(title = "手机号码")
    private String mobile;


    @NotNull(message = "时间戳不能为空")
    @Schema(title = "时间戳")
    private Long timestamp;
}
