package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.validator.anno.StrIn;
import com.youlan.system.constant.SystemConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@TableName("t_sys_config")
@EqualsAndHashCode(callSuper = true)
public class Config extends PageDTO {

    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置名称不能为空")
    @Schema(title = "配置名称")
    private String configName;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置键名不能不能为空")
    @Schema(title = "配置键名")
    private String configKey;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置键值不能为空")
    @Schema(title = "配置键值")
    private String configValue;

    @Query(type = QueryType.EQUAL)
    @StrIn(value = {SystemConstant.CONFIG_TYPE_INNER, SystemConstant.CONFIG_TYPE_OUTER})
    @Schema(title = "配置类型(1-内置参数 2-外置参数)")
    private String configType;

    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

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

}
