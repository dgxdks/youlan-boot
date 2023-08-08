package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@TableName("t_sys_role_org")
public class RoleOrg {

    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "角色ID不能为空")
    @Schema(title = "角色ID")
    private Long roleId;

    @NotNull(message = "机构ID不能为空")
    @Schema(title = "机构ID")
    private Long orgId;

}