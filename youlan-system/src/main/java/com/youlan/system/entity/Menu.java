package com.youlan.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sys_menu")
public class Menu {

    @Schema(title = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "菜单名称")
    private String menuName;

    @Schema(title = "菜单类型(1-目录 2-菜单 3-按钮)")
    private String menuType;

    @Schema(title = "菜单权限字符")
    private String menuPerms;

    @Schema(title = "父级菜单ID")
    private Long parentId;

    @Schema(title = "菜单图标")
    private String menuIcon;

    @Schema(title = "路由路径")
    private String routePath;

    @Schema(title = "路由参数")
    private String routeQuery;

    @Schema(title = "路由缓存(1-是 2-否)")
    private String routeCache;

    @Schema(title = "组件路径")
    private String componentPath;

    @Schema(title = "是否外链(1-是 2-否)")
    private String isFrame;

    @Schema(title = DBConstant.DESC_SORT)
    private Integer sort;

    @Schema(title = "显示状态(1-显示 2-不显示)")
    private String visible;

    @Schema(title = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

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

}
