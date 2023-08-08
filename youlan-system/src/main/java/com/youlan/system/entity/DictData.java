package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.validator.anno.Status;
import com.youlan.common.validator.anno.YesNo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@TableName("t_sys_dict_data")
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class DictData extends PageDTO {

    @TableId(type = IdType.AUTO)
    @Schema(title = DBConstant.DESC_ID)
    private Long id;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典类型键名不能为空")
    @Schema(title = "字典类型键名")
    private String typeKey;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典值名称不能为空")
    @Schema(title = "字典值名称")
    private String dataName;

    @Query(type = QueryType.LIKE)
    @NotBlank(message = "字典值键值不能为空")
    @Schema(title = "字典值键值")
    private String dataValue;

    @Schema(title = "UI样式")
    private String uiClass;

    @Schema(title = "CSS样式")
    private String cssClass;

    @YesNo
    @NotBlank(message = "是否默认不能为空")
    @Schema(title = "是否默认" + DBConstant.DESC_YES_NO)
    private String isDefault;

    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @Schema(title = DBConstant.DESC_SORT)
    private Long sort;

    @Status
    @Query(type = QueryType.EQUAL)
    @NotBlank(message = DBConstant.DESC_STATUS_REQUIRED)
    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

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
