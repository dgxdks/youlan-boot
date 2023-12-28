package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@TableName("t_sys_dict_type")
public class DictType extends PageDTO {

    @ExcelProperty(value = "字典序号")
    @TableId(type = IdType.AUTO)
    @Schema(description = DBConstant.DESC_ID)
    private Long id;

    @ExcelProperty(value = "字典名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典类型名称不能为空")
    @Schema(description = "字典类型名称")
    private String typeName;

    @ExcelProperty(value = "字典类型")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典类型键名不能为空")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型键名必须以字母开头，且只能为（小写字母，数字，下滑线）")
    @Schema(description = "字典类型键名")
    private String typeKey;

    @ExcelProperty(value = "字典状态", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    @Query(type = QueryType.EQUAL)
    @NotBlank(message = DBConstant.DESC_STATUS_REQUIRED)
    @Schema(description = DBConstant.DESC_STATUS)
    private String status;

    @ExcelProperty(value = "备注")
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

    @Query(type = QueryType.BETWEEN, column = "create_time")
    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

    @Schema(description = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictType)) return false;
        DictType dictType = (DictType) o;
        return getTypeKey().equals(dictType.getTypeKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeKey());
    }
}
