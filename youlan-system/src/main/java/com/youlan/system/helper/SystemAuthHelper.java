package com.youlan.system.helper;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.system.entity.auth.SystemAuthInfo;

import java.util.ArrayList;
import java.util.List;

import static com.youlan.system.constant.SystemConstant.*;

public class SystemAuthHelper {
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
}
