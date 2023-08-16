package com.youlan.system.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LoginInfoVO {

    @Schema(title = "用户信息")
    private UserVO user;

    @Schema(title = "角色信息")
    private List<String> roleList;

    @Schema(title = "权限信息")
    private List<String> permissionList;
}
