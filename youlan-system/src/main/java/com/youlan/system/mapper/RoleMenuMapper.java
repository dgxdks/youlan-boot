package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.Menu;
import com.youlan.system.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 根据角色字符查询角色关联的菜单权限字符列表
     */
    Set<String> getMenuPermsListByRoleStr(@Param("roleStr") String roleStr);

    /**
     * 根据用户ID查询角色关联的菜单权限字符列表
     */
    Set<String> getMenuPermsListByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色关联的菜单列表
     */
    List<Menu> getMenuListByUserId(@Param("userId") Long userId, @Param("menuTypeList") List<String> menuTypeList);
}
