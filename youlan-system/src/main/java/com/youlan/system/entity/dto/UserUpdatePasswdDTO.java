package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UserUpdatePasswdDTO {

    @NotBlank(message = "旧密码不能为空")
    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(description = "旧密码")
    private String oldPasswd;

    @NotBlank(message = "新密码不能为空")
    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(description = "新密码")
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    private String newPasswd;

    @NotBlank(message = "确认密码不能为空")
    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(description = "确认密码")
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    private String confirmPasswd;

}
