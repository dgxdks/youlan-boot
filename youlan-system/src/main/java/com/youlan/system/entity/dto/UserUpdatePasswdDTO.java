package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UserUpdatePasswdDTO {

    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(title = "旧密码")
    private String oldPasswd;

    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(title = "新密码")
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    private String newPasswd;

    @DecryptField(algorithm = AlgorithmType.AES)
    @Schema(title = "确认密码")
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    private String confirmPasswd;

}
