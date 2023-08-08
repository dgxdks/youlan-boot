package com.youlan.system.entity.dto;

import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptDTO extends OrgDTO {

    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Schema(title = "负责人")
    private String leader;

    @Schema(title = "联系电话")
    private String phone;

    @Schema(title = "邮箱")
    private String email;
}
