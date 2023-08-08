package com.youlan.system.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SystemAuthInfo {

    @Schema(title = "用户ID")
    private Long userId;

    @Schema(title = "用户名")
    private String userName;

    @Schema(title = "机构ID")
    private Long orgId;

    @Schema(title = "机构类型")
    private String orgType;
}
