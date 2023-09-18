package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import com.youlan.common.validator.anno.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class AccountLoginDTO {

    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Schema(description = "用户账号")
    private String userName;

    @DecryptField(algorithm = AlgorithmType.AES)
    @NotBlank(message = "用户密码不能为空")
    @Schema(description = "用户密码")
    private String userPassword;

    @Xss(message = "验证码不能包含脚本字符")
    @Schema(description = "验证码")
    private String captchaCode;

    @Schema(description = "验证码ID")
    private String captchaId;
}
