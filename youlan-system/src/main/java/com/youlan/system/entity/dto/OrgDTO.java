package com.youlan.system.entity.dto;

import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.common.validator.anno.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrgDTO {

    @Schema(title = "机构ID")
    private Long orgId;

    @NotBlank(message = "机构名称不能为空")
    @Schema(title = "机构名称")
    private String orgName;

    @NotBlank(message = "机构类型不能为空")
    @Schema(title = "机构类型")
    private String orgType;

    @NotBlank(message = "父级机构ID不能为空")
    @Schema(title = "父级机构ID")
    private String parentOrgId;

    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = "机构排序")
    private Integer orgSort;

    @Schema(title = "机构备注")
    private String orgRemark;

    @Status
    @Schema(title = "机构状态(1-正常 2-禁用)")
    private String orgStatus;
}
