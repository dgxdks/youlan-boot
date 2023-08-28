package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserResetPasswdDTO {

    @Schema(title = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(title = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    @DecryptField(algorithm = AlgorithmType.AES)
    private String userPassword;

}
