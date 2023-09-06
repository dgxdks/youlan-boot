package com.youlan.system.entity.vo;

import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptVO extends OrgVO<DeptVO> {

    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Schema(title = "负责人")
    private String leader;

    @Schema(title = "联系电话")
    private String phone;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = DBConstant.DESC_CREATE_ID)
    private Long createId;

    @Schema(title = DBConstant.DESC_CREATE_BY)
    private String createBy;

    @Schema(title = DBConstant.DESC_UPDATE_ID)
    private Long updateId;

    @Schema(title = DBConstant.DESC_UPDATE_BY)
    private String updateBy;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;
}
