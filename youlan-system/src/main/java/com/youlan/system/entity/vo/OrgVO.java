package com.youlan.system.entity.vo;

import com.youlan.common.core.entity.vo.TreeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrgVO<T> extends TreeVO<T> {

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
}
