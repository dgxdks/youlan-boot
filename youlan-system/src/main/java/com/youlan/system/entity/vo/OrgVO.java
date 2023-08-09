package com.youlan.system.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrgVO<T> {

    @Schema(title = "机构ID")
    private Long orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "机构类型")
    private String orgType;

    @Schema(title = "父级机构ID")
    private Long parentOrgId;

    @Schema(title = "机构排序")
    private Integer orgSort;

    @Schema(title = "机构备注")
    private String orgRemark;

    @Schema(title = "机构状态")
    private String orgStatus;

    @Schema(title = "下级机构")
    private List<T> children = new ArrayList<>();
}
