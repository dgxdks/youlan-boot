package com.youlan.system.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.system.entity.Menu;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.UserRole;
import com.youlan.system.entity.dto.UserRolePageDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.enums.MenuType;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.*;
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
    private final UserRoleService userRoleService;
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
        Role oldRole = roleService.loadRoleNotNull(role.getId());
        roleService.checkRoleNameRepeat(role);
        //角色字符不允许修改,否则会导致已缓存用户关联的角色字符失效
        role.setRoleStr(oldRole.getRoleStr());
        boolean update = roleService.updateById(role);
        roleMenuService.updateRoleMenuBatch(role.getId(), role.getMenuIdList());
        SystemAuthHelper.setPermissionList(oldRole.getRoleStr());
        SystemAuthHelper.setRoleScope(role.getRoleStr(), role.getRoleScope());
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
            // 不建议删除角色权限缓存，而是将内容置空，避免有些用户缓存了已经删除的角色字符，但是查权限缓存又没有数据不断的去查库加载，然后库里也没
            SystemAuthHelper.setPermissionList(roleStr);
            SystemAuthHelper.setRoleScope(roleStr, StrUtil.EMPTY);
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
     * 用户角色授权分页
     */
    public IPage<UserVO> getAuthUserPageList(UserRolePageDTO dto) {
        return userRoleService.getBaseMapper().getAuthUserPageList(DBHelper.getPage(dto), dto);
    }

    /**
     * 用户角色未授权分页
     */
    public IPage<UserVO> getUnAuthUserPageList(UserRolePageDTO dto) {
        return userRoleService.getBaseMapper().getUnAuthUserList(DBHelper.getPage(dto), dto);
    }

    /**
     * 用户角色授权取消
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelAuthUser(Long roleId, List<Long> userIds) {
        String roleStr = roleService.getRoleStr(roleId);
        LambdaQueryWrapper<UserRole> queryWrapper = Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getRoleId, roleId).in(UserRole::getUserId, userIds);
        userRoleService.remove(queryWrapper);
        // 删除用户对应的角色信息
        userIds.forEach(userId -> SystemAuthHelper.removeRoleStr(userId, roleStr));
    }

    /**
     * 用户角色授权新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAuthUser(Long roleId, List<Long> userIds) {
        String roleStr = roleService.getRoleStr(roleId);
        userRoleService.addUserRoleBatch(userIds, roleId);
        // 添加用户对应的角色信息
        userIds.forEach(userId -> SystemAuthHelper.addRoleStr(userId, roleStr));
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
        // 如果不是自定义数据权限则删除角色关联机构ID信息
        if (!ROLE_SCOPE_CUSTOM.equals(roleScope)) {
            roleOrgService.removeByRoleId(roleId);
        } else {
            roleOrgService.updateRoleOrgBatch(roleId, role.getOrgIdList());
        }
        SystemAuthHelper.setRoleScope(role.getRoleStr(), roleScope);
        return true;
    }

    /**
     * 角色缓存刷新
     */
    public void refreshRoleCache(Long id) {
        Role role = roleService.loadRoleNotNull(id);
        SystemAuthHelper.setPermissionList(role.getRoleStr());
    }

    /**
     * 角色状态修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long id, String status) {
        Role role = roleService.loadRoleNotNull(id);
        roleService.updateStatus(id, status);
        SystemAuthHelper.setPermissionList(role.getRoleStr());
    }
}
