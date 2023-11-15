package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Role;
import com.youlan.system.anno.DataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 角色分页
     */
    @DataPermission(tableBind = "role", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    IPage<Role> getRolePageList(@Param("page") IPage<Role> page, @Param("role") Role role);

    /**
     * 角色列表
     */
    @DataPermission(tableBind = "role", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<Role> getRoleList(@Param("role") Role role);

    /**
     * 判断当前用户是否拥有此角色ID
     */
    @DataPermission(tableBind = "role", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<Long> hasRoleId(@Param("roleId") Long roleId);

    /**
     * 判断当前用户是否拥有此角色ID
     */
    @DataPermission(tableBind = "role", orgIdColumn = "org.org_id", userIdColumn = "user.id")
    List<Long> hasRoleIds(@Param("roleIds") List<Long> roleIds);
}
