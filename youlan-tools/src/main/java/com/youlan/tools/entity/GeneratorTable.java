package com.youlan.tools.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_tools_generator_table")
public class GeneratorTable {

    @TableId(type = IdType.AUTO)
    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Schema(title = "表名称")
    private String tableName;

    @Schema(title = "表描述")
    private String tableComment;

    @Schema(title = "模块名称")
    private String moduleName;

    @Schema(title = "功能名称")
    private String featureName;

    @Schema(title = "业务名称")
    private String bizName;

    @Schema(title = "实体类名称")
    private String entityName;

    @Schema(title = "是否需要实体类DTO(1-是 2-否)")
    private String entityDto;

    @Schema(title = "是否需要实体类分页DTO(1-是 2-否)")
    private String entityPageDto;

    @Schema(title = "是否需要实体类VO(1-是 2-否)")
    private String entityVo;

    @Schema(title = "包路径")
    private String packageName;

    @Schema(title = "模版类型(1-单表(增删改查) 2-树表(增删改查))")
    private String templateType;

    @Schema(title = "生成类型(1-zip包 2-指定路径)")
    private String generatorType;

    @Schema(title = "生成路径(不填默认为项目路径)")
    private String generatorPath;

    @Schema(title = "作者名称")
    private String authorName;

    @Schema(title = "树表列名")
    private String columnName;

    @Schema(title = "树表父列名")
    private String parentColumnName;

    @Schema(title = "父级菜单ID")
    private String parentMenuId;

    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long create_id;

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