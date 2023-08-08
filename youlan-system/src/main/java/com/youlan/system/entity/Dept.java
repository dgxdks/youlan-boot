package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.system.org.AbstractOrgModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import static com.youlan.system.constant.SystemConstant.ORG_TYPE_DEPT;

@Data
@TableName("t_sys_dept")
@EqualsAndHashCode(callSuper = true)
public class Dept extends AbstractOrgModel {

    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "负责人")
    private String leader;

    @Schema(title = "联系电话")
    private String phone;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(title = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(title = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(title = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Schema(title = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;

    @Override
    public String orgType() {
        return ORG_TYPE_DEPT;
    }
}
