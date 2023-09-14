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
    @Schema(description = DBConstant.DESC_ID)
    private Long id;

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "功能名称")
    private String featureName;

    @Schema(description = "业务名称")
    private String bizName;

    @Schema(description = "实体类名称")
    private String entityName;

    @Schema(description = "是否需要实体类DTO(1-是 2-否)")
    private String entityDto;

    @Schema(description = "是否需要实体类分页DTO(1-是 2-否)")
    private String entityPageDto;

    @Schema(description = "是否需要实体类VO(1-是 2-否)")
    private String entityVo;

    @Schema(description = "包路径")
    private String packageName;

    @Schema(description = "模版类型(1-单表(增删改查) 2-树表(增删改查))")
    private String templateType;

    @Schema(description = "生成类型(1-zip包 2-指定路径)")
    private String generatorType;

    @Schema(description = "生成路径(不填默认为项目路径)")
    private String generatorPath;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "树表列名")
    private String columnName;

    @Schema(description = "树表父列名")
    private String parentColumnName;

    @Schema(description = "树表排序列名")
    private String sortColumnName;

    @Schema(description = "父级菜单ID")
    private String parentMenuId;

    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @Schema(description = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long create_id;

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
}