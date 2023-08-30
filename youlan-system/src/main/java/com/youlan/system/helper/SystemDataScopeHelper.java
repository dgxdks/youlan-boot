package com.youlan.system.helper;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;

public class SystemDataScopeHelper {

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
        if (SystemAuthHelper.isAdmin()) {
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
        if (SystemAuthHelper.isAdmin()) {
            return true;
        }
        // TODO: 2023/8/30 实现数据权限逻辑
        return true;
    }
}
