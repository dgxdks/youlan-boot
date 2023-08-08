package com.youlan.system.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPasswdDTO {

    @Schema(title = "旧密码")
    private String oldPasswd;

    @Schema(title = "新密码")
    private String newPasswd;

    @Schema(title = "确认密码")
    private String confirmPasswd;

}
