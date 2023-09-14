package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_sys_user")
public class User {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "用户密码")
    private String userPassword;

    @Schema(description = "用户手机")
    private String userMobile;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户性别(字典类型[sys_user_sex])")
    private String sex;

    @Schema(description = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private String loginTime;

    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(description = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Schema(description = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;

}
