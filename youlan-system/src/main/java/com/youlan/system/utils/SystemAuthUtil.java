package com.youlan.system.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.youlan.system.entity.auth.SystemAuthInfo;

import static com.youlan.system.constant.SystemConstant.ADMIN_USER_ID;

public class SystemAuthUtil {
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
        return tokenSession.getModel(SaSession.USER, SystemAuthInfo.class);
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
}
