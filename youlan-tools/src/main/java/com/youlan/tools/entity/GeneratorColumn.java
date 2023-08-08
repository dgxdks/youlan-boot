package com.youlan.tools.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("t_tools_generator_column")
public class GeneratorColumn {

    @TableId(type = IdType.AUTO)
    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Schema(title = "生成表ID")
    private Long tableId;

    @Schema(title = "生成表列名称")
    private String columnName;

    @Schema(title = "生成表列描述")
    private String columnComment;

    @Schema(title = "生成表列类型")
    private String columnType;

    @Schema(title = "生成表列Java类型")
    private String javaType;

    @Schema(title = "生成表列Java字段名")
    private String javaField;

    @Schema(title = "是否主键" + DBConstant.DESC_YES_NO)
    private String isPk;

    @Schema(title = "是否自增" + DBConstant.DESC_YES_NO)
    private String isIncrement;

    @Schema(title = "是否必填" + DBConstant.DESC_YES_NO)
    private String isRequired;

    @Schema(title = "编辑时字段" + DBConstant.DESC_YES_NO)
    private String isEdit;

    @Schema(title = "表格时字段" + DBConstant.DESC_YES_NO)
    private String isTable;

    @Schema(title = "查询时字段" + DBConstant.DESC_YES_NO)
    private String isQuery;

    @Schema(title = "查询类型(字典类型[tools_generator_query_type])")
    private String queryType;

    @Schema(title = "组件类型(字典类型[tools_generator_component_type])")
    private String componentType;

    @Schema(title = "字典类型键名")
    private String typeKey;

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

    @TableField(exist = false)
    private String apiModelPropertyAnno;

    @TableField(exist = false)
    private List<String> validatorAnnoList;

    @TableField(exist = false)
    private List<String> excelAnnoList;

    @TableField(exist = false)
    private String tableFieldAnno;

    @TableField(exist = false)
    private String queryAnno;

    @TableField(exist = false)
    private String isCollection;

    @TableField(exist = false)
    private String isVoExclude;

    public GeneratorColumn copy() {
        return BeanUtil.copyProperties(this, GeneratorColumn.class);
    }
}
