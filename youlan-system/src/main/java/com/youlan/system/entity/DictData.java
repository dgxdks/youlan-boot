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
import com.youlan.common.db.enums.DBYesNo;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.excel.anno.ExcelEnumProperty;
import com.youlan.common.excel.converter.EnumConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@TableName("t_sys_dict_data")
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class DictData extends PageDTO {

    @ExcelProperty("字典值序号")
    @TableId(type = IdType.AUTO)
    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @ExcelProperty("字典类型")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典类型键名不能为空")
    @Schema(title = "字典类型键名")
    private String typeKey;

    @ExcelProperty("字典值标签")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典值名称不能为空")
    @Schema(title = "字典值名称")
    private String dataName;

    @ExcelProperty("字典值键值")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典值键值不能为空")
    @Schema(title = "字典值键值")
    private String dataValue;

    @ExcelProperty("UI样式")
    @Schema(title = "UI样式")
    private String uiClass;

    @ExcelProperty("CSS样式")
    @Schema(title = "CSS样式")
    private String cssClass;

    @ExcelProperty(value = "是否默认", converter = EnumConverter.class)
    @ExcelEnumProperty(DBYesNo.class)
    @NotBlank(message = "是否默认不能为空")
    @Schema(title = "是否默认" + DBConstant.DESC_YES_NO)
    private String isDefault;

    @ExcelProperty("字典值排序")
    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = DBConstant.DESC_SORT)
    private Long sort;

    @ExcelProperty(value = "字典值状态", converter = EnumConverter.class)
    @ExcelEnumProperty(DBStatus.class)
    @Query(type = QueryType.EQUAL)
    @NotBlank(message = DBConstant.DESC_STATUS_REQUIRED)
    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @ExcelProperty(value = "字典值备注")
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

    @Schema(title = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;
}
