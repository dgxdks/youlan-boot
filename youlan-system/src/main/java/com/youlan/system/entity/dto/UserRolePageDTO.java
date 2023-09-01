package com.youlan.system.entity.dto;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRolePageDTO extends PageDTO {

    @Schema(title = "角色ID")
    private Long roleId;

    @Schema(title = "用户账号")
    private String userName;

    @Schema(title = "用户手机")
    private String userMobile;

    @Schema(title = DBConstant.DESC_STATUS)
    private String status;
}
