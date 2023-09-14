package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.validator.anno.StrIn;
import com.youlan.system.excel.anno.ExcelDictProperty;
import com.youlan.system.excel.converter.DictConverter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import static com.youlan.system.constant.SystemConstant.*;

@Data
@TableName("t_sys_role")
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12)
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class Role extends PageDTO {

    @ExcelProperty(value = "角色编号")
    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Hidden
    @Schema(description = "要排除的角色ID")
    @TableField(exist = false)
    private List<Long> idExcludes;

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 30, message = "角色名称长度不能超过{max}个字符")
    @ExcelProperty(value = "角色名称")
    @Schema(description = "角色名称")
    private String roleName;

    @NotBlank(message = "角色字符不能为空")
    @Size(min = 1, max = 30, message = "角色字符长度不能超过{max}个字符")
    @ExcelProperty(value = "角色字符")
    @Schema(description = "角色字符")
    private String roleStr;

    @StrIn(value = {ROLE_SCOPE_ALL, ROLE_SCOPE_CUSTOM, ROLE_SCOPE_ORG, ROLE_SCOPE_ORG_CHILDREN, ROLE_SCOPE_USER})
    @ExcelProperty(value = "权限范围", converter = DictConverter.class)
    @ExcelDictProperty(value = "sys_data_scope")
    @Schema(description = "角色数据权限范围(1-全部数据权限 2-自定义数据权限 3-本机构数据权限 4-本机构及以下数据权限 5-仅本人数据权限)")
    private String roleScope;

    @NotNull(message = DBConstant.DESC_SORT_REQUIRED)
    @ExcelProperty(value = "排序")
    @Schema(description = DBConstant.DESC_SORT)
    private Integer sort;

    @ExcelProperty(value = "状态", converter = DictConverter.class)
    @ExcelDictProperty(value = "db_status")
    @Schema(description = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelDictProperty(value = "备注")
    @Schema(description = DBConstant.DESC_REMARK)
    private String remark;

    @TableField(exist = false)
    @Schema(description = "关联菜单ID列表")
    private List<Long> menuIdList;

    @Schema(description = "关联机构ID列表")
    @TableField(exist = false)
    private List<Long> orgIdList;

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

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Schema(description = DBConstant.DESC_STS)
    @TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)
    private String sts;

}
