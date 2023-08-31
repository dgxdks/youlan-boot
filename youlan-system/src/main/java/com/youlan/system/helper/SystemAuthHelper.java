package com.youlan.system.helper;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.system.entity.auth.SystemAuthInfo;
import com.youlan.system.service.RoleMenuService;
import com.youlan.system.service.RoleService;
import com.youlan.system.service.UserRoleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.youlan.system.constant.SystemConstant.ADMIN_ROLE_ID;
import static com.youlan.system.constant.SystemConstant.ADMIN_USER_ID;

public class SystemAuthHelper {
    public static final String ROLE_STR_PREFIX = "sys-role-";
    public static final String ADMIN_ROLE_STR = "*";
    private static final RoleService roleService = SpringUtil.getBean(RoleService.class);
    private static final UserRoleService userRoleService = SpringUtil.getBean(UserRoleService.class);
    private static final RoleMenuService roleMenuService = SpringUtil.getBean(RoleMenuService.class);

    /**
     * 用户登录
     */
    public static void login(Long userId) {
        StpUtil.login(userId);
    }

    /**
     * 用户退出登录
     */
    public static void logout() {
        StpUtil.logout();
    }

    /**
     * 设置用户授权信息
     */
    public static void setSystemAuthInfo(SystemAuthInfo authInfo) {
        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.set(SaSession.USER, authInfo);
    }

    /**
     * 获取用户授权信息
     */
    public static SystemAuthInfo getSystemAuthInfo() {
        SaSession tokenSession = StpUtil.getTokenSession();
        SystemAuthInfo systemAuthInfo = tokenSession.getModel(SaSession.USER, SystemAuthInfo.class);
        if (ObjectUtil.isNull(systemAuthInfo)) {
            throw new BizRuntimeException(ApiResultCode.A0003);
        }
        return systemAuthInfo;
    }

    /**
     * 获取用户名称
     */
    public static String getUserName() {
        return getSystemAuthInfo().getUserName();
    }

    /**
     * 获取用户机构ID
     */
    public static Long getOrgId() {
        return getSystemAuthInfo().getOrgId();
    }

    /**
     * 获取用户机构类型
     */
    public static String getOrgType() {
        return getSystemAuthInfo().getOrgType();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取用户Token
     */
    public static SaTokenInfo getTokenInfo() {
        return StpUtil.getTokenInfo();
    }

    /**
     * 获取用户session
     */
    public static SaSession getTokenSession() {
        return StpUtil.getTokenSession();
    }

    /**
     * 判断是否是超级管理员用户
     */
    public static boolean isAdmin() {
        return ADMIN_USER_ID.equals(getUserId());
    }

    /**
     * 判断是否是超级管理员用户
     */
    public static boolean isAdmin(Long userId) {
        return ADMIN_USER_ID.equals(userId);
    }

    /**
     * 判断是否是超级管理员角色
     */
    public static boolean isAdminRole(String roleStr) {
        return ADMIN_ROLE_STR.equals(roleStr);
    }

    /**
     * 判断是否是超级管理员角色
     */
    public static boolean isAdminRole(Long roleId) {
        return ADMIN_ROLE_ID.equals(roleId);
    }

    /**
     * 判断是否是超级管理员角色字符
     */
    public static boolean isAdminRoleStr(String roleStr) {
        return ADMIN_ROLE_STR.equals(roleStr);
    }

    /**
     * 判断是否是超级管理员角色
     */
    public static boolean isAdminRole() {
        SaSession session = getTokenSession();
        List<?> roleList = session.getModel(SaSession.ROLE_LIST, List.class, new ArrayList<>());
        return roleList.contains(ADMIN_ROLE_STR);
    }

    /**
     * 校验用户不是管理员用户
     */
    public static void checkUserNotAdmin(Long userId) {
        if (ObjectUtil.isNotNull(userId) && isAdmin(userId)) {
            throw new BizRuntimeException(ApiResultCode.A0011);
        }
    }

    /**
     * 校验角色不是管理员角色
     */
    public static void checkRoleNotAdmin(Long roleId) {
        if (ObjectUtil.isNotNull(roleId) && isAdminRole(roleId)) {
            throw new BizRuntimeException(ApiResultCode.A0014);
        }
    }

    /**
     * 校验当前用户是否有此用户数据权限
     */
    public static void checkHasUserId(Long userId) {
        if (!hasUserId(userId)) {
            throw new BizRuntimeException(ApiResultCode.A0013);
        }
    }

    /**
     * 当前用户是否有此用户数据权限
     */
    public static boolean hasUserId(Long userId) {
        if (isAdmin()) {
            return true;
        }
        // TODO: 2023/8/30 实现数据权限逻辑
        return true;
    }

    /**
     * 校验当前用户是否有此角色数据权限
     */
    public static void checkHasRoleId(Long roleId) {
        if (!hasRoleId(roleId)) {
            throw new BizRuntimeException(ApiResultCode.A0015);
        }
    }

    /**
     * 当前用户是否有此角色数据权限
     */
    public static boolean hasRoleId(Long roleId) {
        if (isAdmin()) {
            return true;
        }
        // TODO: 2023/8/30 实现数据权限逻辑
        return true;
    }

    /**
     * 根据用户ID获取用户对应角色的权限字符缓存
     */
    public static List<String> getRoleStrList(Long userId) {
        SaSession session = SystemAuthHelper.getTokenSession();
        return session.get(SaSession.ROLE_LIST, () -> {
            // 获取用户关联的角色ID列表
            List<Long> roleIdList = userRoleService.getRoleIdListByUserId(userId);
            if (CollectionUtil.isEmpty(roleIdList)) {
                return new ArrayList<>();
            }
            // 返回角色ID对应角色字符列表
            return roleService.getRoleStrList(roleIdList);
        });
    }

    /**
     * 根据用户ID删除用户对应角色的缓存字符信息
     */
    public static void removeRoleStrList(Long userId) {
        SaSession session = SystemAuthHelper.getTokenSession();
        session.delete(SaSession.ROLE_LIST);
    }

    /**
     * 获取菜单权限缓存(如果是超级管理员则返回匹配所有的权限字符)
     */
    public static List<String> getMenuPermsList(Long userId) {
        if (isAdmin(userId)) {
            return Collections.singletonList(ADMIN_ROLE_STR);
        }
        Set<String> menPermsList = roleMenuService.getBaseMapper().getMenuPermsListByUserId(userId);
        return new ArrayList<>(menPermsList);
    }

    /**
     * 获取菜单权限缓存(如果是超级管理员则返回匹配所有的权限字符)
     */
    public static List<String> getMenuPermsList(String roleStr) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_STR_PREFIX + roleStr);
        return session.get(SaSession.PERMISSION_LIST, () -> {
            if (isAdminRoleStr(roleStr)) {
                return Collections.singletonList(ADMIN_ROLE_STR);
            }
            return new ArrayList<>(roleMenuService.getBaseMapper().getMenuPermsListByRoleStr(roleStr));
        });
    }

    /**
     * 清除菜单权限缓存
     */
    public static void removeMenuPermsList(String roleStr) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_STR_PREFIX + roleStr);
        session.delete(SaSession.PERMISSION_LIST);
    }
}
