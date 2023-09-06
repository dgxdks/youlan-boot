package com.youlan.system.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class OrgDTO {

    @Schema(title = "机构ID")
    @TableField(exist = false)
    private Long orgId;

    @NotBlank(message = "机构名称不能为空")
    @Size(min = 1, max = 30, message = "机构名称长度不能超过{max}个字符")
    @Schema(title = "机构名称")
    @TableField(exist = false)
    private String orgName;

    @NotBlank(message = "机构类型不能为空")
    @Schema(title = "机构类型")
    @TableField(exist = false)
    private String orgType;

    @NotNull(message = "父级机构ID不能为空")
    @Schema(title = "父级机构ID")
    @TableField(exist = false)
    private Long parentOrgId;

    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = "机构排序")
    @TableField(exist = false)
    private Integer orgSort;

    @Schema(title = "机构备注")
    @TableField(exist = false)
    private String orgRemark;

    @Schema(title = "机构状态(1-正常 2-禁用)")
    @TableField(exist = false)
    private String orgStatus;
}
