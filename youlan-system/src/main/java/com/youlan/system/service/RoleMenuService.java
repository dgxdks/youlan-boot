package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.RoleMenu;
import com.youlan.system.mapper.RoleMenuMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleMenuService extends BaseServiceImpl<RoleMenuMapper, RoleMenu> {

    /**
     * 新增角色关联菜单
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRoleMenuBatch(Long roleId, List<Long> menuIdList) {
        List<RoleMenu> roleMenuList = toRoleMenuList(roleId, menuIdList);
        this.saveBatch(roleMenuList);
    }

    /**
     * 修改角色关联菜单
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenuBatch(Long roleId, List<Long> menuIdList) {
        List<RoleMenu> roleMenuList = toRoleMenuList(roleId, menuIdList);
        removeByRoleId(roleId);
        this.saveBatch(roleMenuList);
    }

    /**
     * 根据角色ID和菜单列表转为角色关联菜单对象列表
     */
    public List<RoleMenu> toRoleMenuList(Long roleId, List<Long> menuIdList) {
        if (ObjectUtil.isNull(roleId) || CollectionUtil.isEmpty(menuIdList)) {
            return new ArrayList<>();
        }
        return menuIdList.stream()
                .map(menuId -> new RoleMenu().setRoleId(roleId).setMenuId(menuId))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeByRoleId(Long roleId) {
        this.remove(Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getRoleId, roleId));
    }

    /**
     * 根据角色ID获取关联菜单ID列表
     */
    public List<Long> getMenuIdListByRoleId(Long roleId) {
        return this.lambdaQuery()
                .select(RoleMenu::getMenuId)
                .eq(RoleMenu::getRoleId, roleId)
                .list()
                .stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
