package com.youlan.system.utils;

import cn.hutool.core.text.StrPool;
import com.youlan.system.constant.SystemConstant;

import static com.youlan.system.constant.SystemConstant.REDIS_PREFIX_LOGIN_RETRY;

public class SystemUtil {

    /**
     * 获取登录重试redis key
     */
    public static String getLoginRetryRedisKey(String userName) {
        return REDIS_PREFIX_LOGIN_RETRY + userName;
    }

    /**
     * 获取登录重试redis key
     */
    public static String getLoginRetryRedisKey(String userName, String clientIp) {
        return REDIS_PREFIX_LOGIN_RETRY + userName + StrPool.COLON + clientIp;
    }

    /**
     * 获取系统配置redis key
     */
    public static String getConfigRedisKey(String configKey) {
        return SystemConstant.REDIS_PREFIX_CONFIG + configKey;
    }

    /**
     * 获取数据字典redis key
     */
    public static String getDictRedisKey(String typeKey) {
        return SystemConstant.REDIS_PREFIX_DICT + typeKey;
    }

}
