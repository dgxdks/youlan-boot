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
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@TableName("t_sys_post")
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class Post extends PageDTO {

    @ExcelProperty(value = "岗位序号")
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "岗位名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 1, max = 32, message = "岗位名称长度不能超过{max}个字符")
    @Schema(title = "岗位名称")
    private String postName;

    @ExcelProperty(value = "岗位编码")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 1, max = 32, message = "岗位编码长度不能超过{max}个字符")
    @Schema(title = "岗位编码")
    private String postCode;

    @ExcelProperty(value = "岗位排序")
    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = DBConstant.DESC_SORT)
    private Integer sort;

    @ExcelProperty(value = "状态", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelProperty(value = "备注")
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

    @Schema(title = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;
}
