package com.youlan.system.entity.vo;

import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptVO extends OrgVO<DeptVO> {

    @Schema(description = DBConstant.DESC_ID)
    private Long id;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;
}
