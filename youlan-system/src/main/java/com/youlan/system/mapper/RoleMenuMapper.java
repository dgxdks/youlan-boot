package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 根据角色ID查询角色关联的菜单权限字符列表
     */
    Set<String> getMenuPermsListByRoleId(@Param("roleId") Long roleId);
}
