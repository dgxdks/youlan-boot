package com.youlan.system.entity.dto;

import com.youlan.common.crypto.anno.DecryptField;
import com.youlan.common.crypto.enums.AlgorithmType;
import com.youlan.common.validator.Insert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AccountRegistryDTO extends AccountLoginDTO {

    @DecryptField(algorithm = AlgorithmType.AES)
    @NotBlank(message = "用户密码不能为空", groups = {Insert.class})
    @Size(min = 6, max = 20, message = "用户密码长度必须介于5和20之间")
    @Schema(description = "确认密码")
    private String confirmPassword;

}
