package com.youlan.system.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SystemAuthInfo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "机构类型")
    private String orgType;

    @Schema(description = "用户浏览器代理")
    private String userAgent;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录时间")
    private Date loginTime;
}
