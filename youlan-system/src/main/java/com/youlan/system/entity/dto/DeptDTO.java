package com.youlan.system.entity.dto;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.validator.anno.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptDTO extends OrgDTO {

    @Schema(description = DBConstant.DESC_ID)
    private Long id;

    @Schema(description = "负责人")
    private String leader;

    @Phone
    @Schema(description = "联系电话")
    private String phone;

    @Email
    @Schema(description = "邮箱")
    private String email;
}
