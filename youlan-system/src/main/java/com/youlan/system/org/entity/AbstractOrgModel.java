package com.youlan.system.org.entity;

import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 机构模型
 */
@Data
public abstract class AbstractOrgModel {

    @Schema(title = "机构ID")
    private Long orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "机构类型")
    private String orgType;

    @Schema(title = "机构层级")
    private Integer orgLevel;

    @Schema(title = "机构祖级")
    private String orgAncestors;

    @Schema(title = "父级机构ID")
    private String parentOrgId;

    @Schema(title = "机构排序")
    private Integer orgSort;

    @Schema(title = "机构备注")
    private String orgRemark;

    @Schema(title = DBConstant.DESC_STATUS)
    private String orgStatus;

    /**
     * 子类实现时返回对应机构类型
     */
    public abstract String orgType();
}
