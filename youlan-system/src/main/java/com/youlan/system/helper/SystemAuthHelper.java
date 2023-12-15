package com.youlan.system.helper;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.system.entity.Role;
import com.youlan.system.entity.auth.SystemAuthInfo;
import com.youlan.system.enums.RoleScope;
import com.youlan.system.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.youlan.system.constant.SystemConstant.*;

@Slf4j
public class SystemAuthHelper {
    public static final String ROLE_STR_PREFIX = "sys-role-";
    public static final String ROLE_SCOPE = "ROLE_SCOPE";
    private static final UserService userService = SpringUtil.getBean(UserService.class);
    private static final RoleService roleService = SpringUtil.getBean(RoleService.class);
    private static final OrgService orgService = SpringUtil.getBean(OrgService.class);
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
        SaSession session = StpUtil.getSession();
        session.set(SaSession.USER, authInfo);
    }


    /**
     * 根据token获取用户信息
     */
    public static SystemAuthInfo getSystemAuthInfoByTokenValue(String tokenValue) {
        try {
            Object loginId = StpUtil.getLoginIdByToken(tokenValue);
            SaSession session = StpUtil.getSessionByLoginId(loginId);
            return session.getModel(SaSession.USER, SystemAuthInfo.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据token强踢用户
     */
    public static void kickoutByTokenValue(String tokenValue) {
        try {
            StpUtil.kickoutByTokenValue(tokenValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取用户授权信息
     */
    public static SystemAuthInfo getSystemAuthInfo() {
        SaSession session = StpUtil.getSession();
        SystemAuthInfo systemAuthInfo = session.getModel(SaSession.USER, SystemAuthInfo.class);
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
     * 如果存在获取用户名称
     */
    public static String getUserNameIfExists() {
        try {
            return getUserName();
        } catch (Exception e) {
            return null;
        }
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
     * 如果存在获取用户ID
     */
    public static Long getUserIdIfExists() {
        try {
            return getUserId();
        } catch (Exception e) {
            return null;
        }
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
    public static SaSession getSession() {
        return StpUtil.getSession();
    }

    /**
     * 判断是否是顶级机构ID
     */
    public static boolean isTopOrg(Long orgId) {
        return TOP_ORG_ID.equals(orgId);
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
    public static boolean isAdminRole(Long roleStr) {
        return ADMIN_ROLE_ID.equals(roleStr);
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
        SaSession session = getSession();
        List<?> roleList = session.getModel(SaSession.ROLE_LIST, List.class, new ArrayList<>());
        return roleList.contains(ADMIN_ROLE_STR);
    }

    /**
     * 校验机构不是顶级机构
     */
    public static void checkOrgNotTop(Long orgId) {
        if (ObjectUtil.isNotNull(orgId) && isTopOrg(orgId)) {
            throw new BizRuntimeException(ApiResultCode.A0028);
        }
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
     * 当前用户是否有此用户数据权限
     */
    public static boolean hasUserId(Long userId) {
        if (isAdmin()) {
            return true;
        }
        List<Long> userIds = userService.getBaseMapper().hasUserId(userId);
        return userIds.contains(userId);
    }

    /**
     * 当前用户是否有此用户数据权限
     */
    public static boolean hasUserIds(List<Long> userIds) {
        if (isAdmin()) {
            return true;
        }
        List<Long> userIdsRet = userService.getBaseMapper().hasUserIds(userIds);
        return new HashSet<>(userIdsRet).containsAll(userIds);
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
     * 校验当前用户是否有此用户数据权限
     */
    public static void checkHasUserIds(List<Long> userIds) {
        if (!hasUserIds(userIds)) {
            throw new BizRuntimeException(ApiResultCode.A0013);
        }
    }

    /**
     * 当前用户是否有此角色数据权限
     */
    public static boolean hasRoleId(Long roleId) {
        if (isAdmin()) {
            return true;
        }
        List<Long> roleIds = roleService.getBaseMapper().hasRoleId(roleId);
        return roleIds.contains(roleId);
    }

    /**
     * 当前用户是否有此角色数据权限
     */
    public static boolean hasRoleIds(List<Long> roleIds) {
        if (isAdmin()) {
            return true;
        }
        List<Long> roleIdsRet = roleService.getBaseMapper().hasRoleIds(roleIds);
        return new HashSet<>(roleIdsRet).containsAll(roleIds);
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
     * 校验当前用户是否有此角色数据权限
     */
    public static void checkHasRoleIds(List<Long> roleIds) {
        if (!hasRoleIds(roleIds)) {
            throw new BizRuntimeException(ApiResultCode.A0015);
        }
    }

    /**
     * 当前用户是否有此机构权限
     */
    public static boolean hasOrgId(Long orgId) {
        if (isAdmin()) {
            return true;
        }
        List<Long> orgIdList = orgService.getBaseMapper().hasOrgId(orgId);
        return orgIdList.contains(orgId);
    }

    /**
     * 当前用户是否有此机构权限
     */
    public static boolean hasOrgIds(List<Long> orgIds) {
        if (isAdmin()) {
            return true;
        }
        List<Long> orgIdsRet = orgService.getBaseMapper().hasOrgIds(orgIds);
        return new HashSet<>(orgIdsRet).containsAll(orgIds);
    }

    /**
     * 校验当前用户是否有此机构权限
     */
    public static void checkHasOrgId(Long orgId) {
        if (!hasOrgId(orgId)) {
            throw new BizRuntimeException(ApiResultCode.A0021);
        }
    }

    /**
     * 校验当前用户是否有此机构权限
     */
    public static void checkHasOrgIds(List<Long> orgIds) {
        if (!hasOrgIds(orgIds)) {
            throw new BizRuntimeException(ApiResultCode.A0021);
        }
    }


    /**
     * 根据用户ID获取用户对应角色的权限字符缓存
     */
    public static List<String> getRoleStrList(Long userId) {
        SaSession session = StpUtil.getSessionByLoginId(userId);
        return session.get(SaSession.ROLE_LIST, () -> userRoleService.getBaseMapper().getRoleStrListByUserId(userId));
    }

    /**
     * 根据用户ID删除用户对应角色信息
     */
    public static void removeRoleStr(Long userId, String roleStr) {
        if (StrUtil.isBlank(roleStr)) {
            return;
        }
        SaSession session = StpUtil.getSessionByLoginId(userId);
        List<String> roleStrList = (List<String>) session.get(SaSession.ROLE_LIST);
        if (CollectionUtil.isEmpty(roleStrList)) {
            return;
        }
        roleStrList.remove(roleStr);
        session.set(SaSession.ROLE_LIST, roleStrList);
    }

    /**
     * 根据用ID设置用户对应的角色信息
     */
    public static void setRoleStrList(Long userId, List<String> roleStrList) {
        SaSession session = StpUtil.getSessionByLoginId(userId);
        session.set(SaSession.ROLE_LIST, roleStrList);
    }

    /**
     * 根据用户ID新增用户对应角色信息
     */
    public static void addRoleStr(Long userId, String roleStr) {
        if (StrUtil.isBlank(roleStr)) {
            return;
        }
        SaSession session = StpUtil.getSessionByLoginId(userId);
        List<String> roleStrList = (List<String>) session.get(SaSession.ROLE_LIST);
        if (CollectionUtil.isEmpty(roleStrList)) {
            roleStrList = new ArrayList<>();
        }
        //避免存在重名角色字符
        if (!roleStrList.contains(roleStr)) {
            roleStrList.add(roleStr);
        }
        session.set(SaSession.ROLE_LIST, roleStrList);
    }

    /**
     * 获取自定义角色session
     */
    public static SaSession getRoleSession(String roleStr) {
        return SaSessionCustomUtil.getSessionById(ROLE_STR_PREFIX + roleStr);
    }

    /**
     * 设置权限列表缓存
     */
    public static List<String> setPermissionList(String roleStr) {
        List<String> permissionList;
        if (isAdminRoleStr(roleStr)) {
            permissionList = Collections.singletonList(ADMIN_PERM_STR);
        } else {
            permissionList = roleMenuService.getBaseMapper().getMenuPermsListByRoleStr(roleStr);
        }
        setPermissionList(roleStr, permissionList);
        return permissionList;
    }

    /**
     * 设置权限列表缓存
     */
    public static void setPermissionList(String roleStr, List<String> permissionList) {
        SaSession session = getRoleSession(roleStr);
        session.set(SaSession.PERMISSION_LIST, permissionList);
    }

    /**
     * 获取菜单权限缓存(如果是超级管理员则返回匹配所有的权限字符)
     */
    public static List<String> getPermissionList(String roleStr) {
        SaSession session = getRoleSession(roleStr);
        return session.get(SaSession.PERMISSION_LIST, () -> setPermissionList(roleStr));
    }

    /**
     * 根据角色字符获取对应权限范围
     */
    public static String getRoleScope(String roleStr) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_STR_PREFIX + roleStr);
        return session.get(ROLE_SCOPE, () -> setRoleScope(roleStr));
    }

    /**
     * 设置角色字符对应的角色权限范围
     */
    public static void setRoleScope(String roleStr, String roleScope) {
        SaSession session = SaSessionCustomUtil.getSessionById(ROLE_STR_PREFIX + roleStr);
        session.set(ROLE_SCOPE, roleScope);
    }

    /**
     * 设置角色字符对应的角色权限范围
     */
    public static String setRoleScope(String roleStr) {
        Role role = roleService.loadRoleByRoleStr(roleStr);
        String roleScope;
        if (ObjectUtil.isNull(role) || !RoleScope.contains(role.getRoleScope())) {
            roleScope = StrUtil.EMPTY;
        } else {
            roleScope = role.getRoleScope();
        }
        setRoleScope(roleStr, roleScope);
        return roleScope;
    }

    /**
     * 查找token
     */
    public static List<String> searchTokenValue(String keyword, int start, int size, boolean sortType) {
        List<String> tokenKeys = StpUtil.searchTokenValue(keyword, start, size, sortType);
        //不知道是不是sa-token的bug，返回的竟然是token对应的redis key，导致如果用这个值取数据会取出问题，需要自己截取一下真实的token
        if (CollectionUtil.isEmpty(tokenKeys)) {
            return new ArrayList<>();
        }
        return tokenKeys.stream()
                .map(tokenKey -> StrUtil.subAfter(tokenKey, StringPool.COLON, true))
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取对应token列表
     */
    public static List<String> getTokenValuesByUserId(Long userId) {
        return StpUtil.getTokenValueListByLoginId(userId);
    }
}
