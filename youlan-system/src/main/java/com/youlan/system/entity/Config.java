package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import com.youlan.common.validator.anno.StrIn;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.enums.ConfigType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
@TableName("t_sys_config")
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class Config extends PageDTO {

    @ExcelProperty(value = "配置序号")
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "配置名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置名称不能为空")
    @Schema(description = "配置名称")
    private String configName;

    @ExcelProperty(value = "配置键名")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置键名不能不能为空")
    @Schema(description = "配置键名")
    private String configKey;

    @ExcelProperty(value = "配置键值")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "配置键值不能为空")
    @Schema(description = "配置键值")
    private String configValue;

    @ExcelProperty(value = "配置类型", converter = EnumConverter.class)
    @ExcelEnumProperty(ConfigType.class)
    @Query(type = QueryType.EQUAL)
    @StrIn(value = {SystemConstant.CONFIG_TYPE_INNER, SystemConstant.CONFIG_TYPE_OUTER})
    @Schema(description = "配置类型(1-内置参数 2-外置参数)")
    private String configType;

    @ExcelProperty(value = "配置备注")
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

    @Query(column = "create_time", type = QueryType.BETWEEN)
    @TableField(exist = false)
    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private List<Date> createTimeRange;


    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
