package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_sys_org")
public class SysOrg {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orgName;

    private Integer orgType;

    private Long parentOrgId;

    private String orgNo;

    private String orgCode;

    private Integer isUse;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.UPDATE)
    private Long updateUser;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableLogic
    private String delete;

}
