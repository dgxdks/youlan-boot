package com.youlan.system.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.system.entity.Menu;
import com.youlan.system.entity.Role;
import com.youlan.system.enums.MenuType;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.MenuService;
import com.youlan.system.service.RoleMenuService;
import com.youlan.system.service.RoleOrgService;
import com.youlan.system.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.youlan.system.constant.SystemConstant.ROLE_SCOPE_CUSTOM;

@Slf4j
@Service
@AllArgsConstructor
public class RoleBizService {
    private final RoleService roleService;
    private final RoleOrgService roleOrgService;
    private final RoleMenuService roleMenuService;
    private final MenuService menuService;

    /**
     * 新增角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(Role role) {
        roleService.checkRoleNameRepeat(role);
        roleService.checkRoleStrRepeat(role);
        boolean save = roleService.save(role);
        roleMenuService.addRoleMenuBatch(role.getId(), role.getMenuIdList());
        return save;
    }

    /**
     * 修改角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        roleService.checkRoleNameRepeat(role);
        roleService.checkRoleStrRepeat(role);
        boolean update = roleService.updateById(role);
        roleMenuService.updateRoleMenuBatch(role.getId(), role.getMenuIdList());
        return update;
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRole(List<Long> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return true;
        }
        List<String> roleStrList = roleService.getRoleStrList(idList);
        for (Long roleId : idList) {
            //删除角色
            roleService.removeById(roleId);
            //删除角色关联菜单
            roleMenuService.removeByRoleId(roleId);
            //删除角色关联机构
            roleOrgService.removeByRoleId(roleId);
        }
        //删除角色字符缓存,虽然没有删除与此角色ID关联的用户，但是由于删除角色字符缓存会触发二次查库加载，查不到也就自然没有权限
        //每个用户缓存的角色ID会在token到期后自动清除
        for (String roleStr : roleStrList) {
            SystemAuthHelper.removeMenuPermsList(roleStr);
        }
        return true;
    }

    /**
     * 角色详情
     */
    public Role loadRole(Long id) {
        Role role = roleService.loadOneOpt(id)
                .orElseThrow(ApiResultCode.A0016::getException);
        role.setMenuIdList(roleMenuService.getMenuIdListByRoleId(id));
        role.setOrgIdList(roleOrgService.getOrgIdListByRoleId(id));
        return role;
    }

    /**
     * 获取用户对应的菜单树列表
     */
    public List<Menu> getMenuTreeList(Long userId) {
        List<Menu> menuList;
        List<String> menuTypeList = Arrays.asList(MenuType.DIRECTORY.getCode(), MenuType.MENU.getCode());
        if (SystemAuthHelper.isAdmin()) {
            menuList = menuService.lambdaQuery()
                    .eq(Menu::getStatus, DBStatus.ENABLED.getCode())
                    .in(Menu::getMenuType, menuTypeList)
                    .list();
        } else {
            menuList = roleMenuService.getBaseMapper().getMenuListByUserId(userId, menuTypeList)
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
        }
        return ListHelper.getTreeList(menuList, Menu::getChildren, Menu::getId, Menu::getParentId, Menu::getSort);
    }

    /**
     * 修改角色数据权限范围
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleScope(Role role) {
        Long roleId = role.getId();
        roleService.loadOneOpt(roleId)
                .orElseThrow(ApiResultCode.A0016::getException);
        String roleScope = role.getRoleScope();
        // 先更新角色数据范围
        roleService.updateRoleScope(roleId, roleScope);
        // 如果不是自定义数据权限则删除角色关联机构ID信息并返回
        if (!ROLE_SCOPE_CUSTOM.equals(roleScope)) {
            roleOrgService.removeByRoleId(roleId);
            return true;
        }
        roleOrgService.updateRoleOrgBatch(roleId, role.getOrgIdList());
        return true;
    }
}
