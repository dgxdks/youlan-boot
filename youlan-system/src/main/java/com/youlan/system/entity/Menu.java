package com.youlan.system.entity;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@TableName("t_sys_menu")
@ExcelIgnoreUnannotated
public class Menu extends PageDTO {

    @Schema(description = DBConstant.DESC_ID)
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型(1-目录 2-菜单 3-按钮)")
    private String menuType;

    @Schema(description = "菜单权限字符")
    private String menuPerms;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "路由路径")
    private String routePath;

    @Schema(description = "路由参数")
    private String routeQuery;

    @Schema(description = "路由缓存(1-是 2-否)")
    private String routeCache;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "是否外链(1-是 2-否)")
    private String isFrame;

    @Schema(description = DBConstant.DESC_SORT)
    private Integer sort;

    @Schema(description = "显示状态(1-显示 2-不显示)")
    private String visible;

    @Schema(description = DBConstant.DESC_STATUS)
    @TableField(fill = FieldFill.INSERT)
    private String status;

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

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @Schema(description = "菜单类型列表")
    @TableField(exist = false)
    private List<String> menuTypeList;

    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (ObjectUtil.isNull(o)) {
            return false;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        return getId().equals(((Menu) o).getId());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
