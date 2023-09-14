package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("t_sys_org")
public class Org {

    @Schema(description = "机构ID")
    @TableId(type = IdType.AUTO)
    private Long orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构类型")
    private String orgType;

    @Schema(description = "机构层级")
    private Integer orgLevel;

    @Schema(description = "机构祖级")
    private String orgAncestors;

    @Schema(description = "父级机构ID")
    private String parentOrgId;

    @Schema(description = "机构排序")
    private Integer orgSort;

    @Schema(description = "机构备注")
    private String orgRemark;

    @Schema(description = "机构状态(1-可用 2-禁用)")
    @TableField(fill = FieldFill.INSERT)
    private String orgStatus;

    @Schema(description = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String orgSts;
}
