package com.youlan.system.service.biz;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.system.entity.Menu;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.RoleOrg;
import com.youlan.system.entity.dto.RoleOrgDTO;
import com.youlan.system.enums.MenuType;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.youlan.system.constant.SystemConstant.ROLE_SCOPE_CUSTOM;

@Slf4j
@Service
@AllArgsConstructor
public class RoleBizService {
    public static final String ROLE_ID_PREFIX = "sys-role-";
    private final RoleService roleService;
    private final OrgService orgService;
    private final RoleOrgService roleOrgService;
    private final RoleMenuService roleMenuService;
    private final UserRoleService userRoleService;
    private final MenuService menuService;

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
     * 获取菜单权限缓存(如果是超级管理员则返回匹配所有的权限字符)
     */
    public List<String> getMenuPermsList(Long userId) {
        if (SystemAuthHelper.isAdmin()) {
            return Collections.singletonList("*");
        }
        Set<String> menPermsList = roleMenuService.getBaseMapper().getMenuPermsListByUserId(userId);
        return new ArrayList<>(menPermsList);
    }

    /**
     * 根据用户ID获取用户对应角色的权限字符缓存
     */
    public List<String> getRoleStrListCache(Long userId) {
        SaSession session = SystemAuthHelper.getTokenSession();
        return session.get(SaSession.ROLE_LIST, () -> {
            List<Long> roleIdList = userRoleService.getRoleIdListByUserId(userId);
            if (CollectionUtil.isEmpty(roleIdList)) {
                return new ArrayList<>();
            }
            return roleService.lambdaQuery()
                    .select(Role::getRoleStr)
                    .in(Role::getId, roleIdList)
                    .list()
                    .stream()
                    .map(Role::getRoleStr)
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    /**
     * 根据用户ID删除用户对应角色的缓存字符信息
     */
    public void removeRoleStrListCache(Long userId) {
        SaSession session = SystemAuthHelper.getTokenSession();
        session.delete(SaSession.ROLE_LIST);
    }

    /**
     * 获取菜单权限缓存(如果是超级管理员则返回匹配所有的权限字符)
     */
    public List<String> getMenuPermsListCache(String roleStr) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_ID_PREFIX + roleStr);
        return session.get(SaSession.PERMISSION_LIST, () -> {
            if (SystemAuthHelper.isAdmin()) {
                return Collections.singletonList("*");
            }
            return new ArrayList<>(roleMenuService.getBaseMapper().getMenuPermsListByRoleStr(roleStr));
        });
    }

    /**
     * 清除菜单权限缓存
     */
    public void removeMenuPermsListCache(Long roleId) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_ID_PREFIX + roleId);
        session.delete(SaSession.PERMISSION_LIST);
    }

    /**
     * 修改角色数据权限范围
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleScope(RoleOrgDTO dto) {
        Long roleId = dto.getRoleId();
        Optional<Role> roleOpt = roleService.loadOneOpt(roleId);
        if (roleOpt.isEmpty()) {
            throw new BizRuntimeException("角色信息不存在");
        }
        String roleScope = dto.getRoleScope();
        List<Long> orgIdList = dto.getOrgIdList();
        boolean updateRole = roleService.lambdaUpdate()
                .eq(Role::getId, roleId)
                .set(Role::getRoleScope, roleScope)
                .update();
        if (!updateRole) {
            throw new BizRuntimeException("修改角色数据权限范围信息失败");
        }
        if (!ROLE_SCOPE_CUSTOM.equals(roleScope)) {
            return true;
        }
        if (CollectionUtil.isEmpty(orgIdList)) {
            throw new BizRuntimeException("自定义数据权限必须选择机构");
        }
        orgIdList = orgService.lambdaQuery()
                .select(Org::getOrgId)
                .in(Org::getOrgId, orgIdList)
                .list()
                .stream()
                .map(Org::getOrgId)
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgIdList)) {
            throw new BizRuntimeException("机构信息产生变动请刷新页面重新配置");
        }
        boolean removeByRoleId = roleOrgService.removeByRoleId(roleId);
        if (!removeByRoleId) {
            throw new BizRuntimeException("删除角色关联机构信息失败");
        }
        List<RoleOrg> roleOrgList = orgIdList.stream().map(orgId -> new RoleOrg()
                .setOrgId(orgId)
                .setRoleId(roleId)
        ).collect(Collectors.toList());
        boolean saveRoleOrg = roleOrgService.saveBatch(roleOrgList);
        if (!saveRoleOrg) {
            throw new BizRuntimeException("新增角色关联机构信息失败");
        }
        return true;
    }
}
