package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UserResetPasswdDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    @DecryptField(algorithm = AlgorithmType.AES)
    private String userPassword;

}
