package com.youlan.system.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrgVO<T> {

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构类型")
    private String orgType;

    @Schema(description = "机构祖级")
    private String orgAncestors;

    @Schema(description = "父级机构ID")
    private Long parentOrgId;

    @Schema(description = "机构排序")
    private Integer orgSort;

    @Schema(description = "机构备注")
    private String orgRemark;

    @Schema(description = "机构状态")
    private String orgStatus;

    @Schema(description = "下级机构")
    private List<T> children = new ArrayList<>();
}
