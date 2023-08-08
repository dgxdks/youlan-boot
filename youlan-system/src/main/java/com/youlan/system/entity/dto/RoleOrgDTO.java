package com.youlan.system.entity.dto;

import com.youlan.common.validator.anno.StrIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.youlan.system.constant.SystemConstant.*;

@Data
@Accessors(chain = true)
public class RoleOrgDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(title = "角色ID")
    private Long roleId;

    @StrIn(value = {ROLE_SCOPE_ALL, ROLE_SCOPE_CUSTOM, ROLE_SCOPE_ORG, ROLE_SCOPE_ORG_CHILDREN, ROLE_SCOPE_USER})
    @Schema(title = "角色数据权限范围(1-全部数据权限 2-自定义数据权限 3-本机构数据权限 4-本机构及以下数据权限 5-仅本人数据权限)")
    private String roleScope;

    @Schema(title = "角色关联机构ID(自定义数据权限(orgType=2)时必填)")
    private List<Long> orgIdList;
}
