package com.youlan.system.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class OnlineUserVO {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构类型")
    private String orgType;

    @Schema(description = "用户浏览器代理")
    private String userAgent;

    @Schema(description = "登录位置")
    private String loginLocation;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录时间")
    private Date loginTime;

    @Schema(description = "用户token值")
    private String tokenValue;
}
