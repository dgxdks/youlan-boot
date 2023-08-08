package com.youlan.system.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.youlan.system.excel.anno.ExcelDictProperty;
import com.youlan.system.excel.converter.DictConverter;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.enums.QueryType;
import lombok.EqualsAndHashCode;
import com.youlan.common.validator.anno.*;
import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
@TableName("t_sys_menu")
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = true)
public class Menu extends PageDTO {

    @ExcelProperty(value = "主键ID")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "菜单名称")
    @Query(type = QueryType.EQUAL)
    @NotBlank(message="菜单名称不能为空")
    @Schema(title = "菜单名称")
    private String menuName;

    @ExcelProperty(value = "菜单类型(1-目录 2-菜单 3-按钮)")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "菜单类型(1-目录 2-菜单 3-按钮)")
    private String menuType;

    @ExcelProperty(value = "菜单权限字符")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "菜单权限字符")
    private String menuPerms;

    @ExcelProperty(value = "父级菜单ID")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "父级菜单ID")
    private Long parentId;

    @ExcelProperty(value = "菜单图标")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "菜单图标")
    private String menuIcon;

    @ExcelProperty(value = "路由路径")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "路由路径")
    private String routePath;

    @ExcelProperty(value = "路由参数")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "路由参数")
    private String routeQuery;

    @ExcelProperty(value = "路由缓存(1-是 2-否)")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "路由缓存(1-是 2-否)")
    private String routeCache;

    @ExcelProperty(value = "组件路径")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "组件路径")
    private String componentPath;

    @ExcelProperty(value = "是否外链(1-是 2-否)")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "是否外链(1-是 2-否)")
    private String isFrame;

    @ExcelProperty(value = "排序")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_SORT)
    private Integer sort;

    @ExcelProperty(value = "显示状态(1-显示 2-不显示)")
    @Query(type = QueryType.EQUAL)
    @Schema(title = "显示状态(1-显示 2-不显示)")
    private String visible;

    @ExcelProperty(value = "状态(1-正常 2-停用)")
    @Query(type = QueryType.EQUAL)
    @Status
    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

    @ExcelProperty(value = "备注")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_REMARK)
    private String remark;

    @ExcelProperty(value = "创建用户ID")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_CREATE_ID)
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @ExcelProperty(value = "创建用户")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_CREATE_BY)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ExcelProperty(value = "修改用户ID")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_UPDATE_ID)
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @ExcelProperty(value = "修改用户")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_UPDATE_BY)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @Query(type = QueryType.BETWEEN)
    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(exist = false)
    private List<Date> createTimeRange;

    @ExcelProperty(value = "创建时间")
    @Schema(title = DBConstant.DESC_CREATE_TIME)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelProperty(value = "修改时间")
    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
