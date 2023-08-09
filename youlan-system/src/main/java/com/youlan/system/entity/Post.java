package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
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
@TableName("t_sys_post")
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class Post extends PageDTO {

    @ExcelProperty(value = "主键ID")
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "岗位名称")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "岗位名称不能为空")
    @Schema(title = "岗位名称")
    private String postName;

    @ExcelProperty(value = "岗位编码")
    @Query(type = QueryType.LIKE)
    @NotBlank(message = "岗位编码不能为空")
    @Schema(title = "岗位编码")
    private String postCode;

    @ExcelProperty(value = "排序")
    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = DBConstant.DESC_SORT)
    private Integer sort;

    @ExcelProperty(value = "状态(1-正常 2-停用)", converter = EnumConverter.class)
    @ExcelEnumProperty(value = DBStatus.class)
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelProperty(value = "备注")
    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

    @ExcelProperty(value = "创建用户ID")
    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @ExcelProperty(value = "创建用户")
    @Schema(title = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ExcelProperty(value = "修改用户ID")
    @Schema(title = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @ExcelProperty(value = "修改用户")
    @Schema(title = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ExcelProperty(value = "创建时间")
    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ExcelProperty(value = "修改时间")
    @Schema(title = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;
}
