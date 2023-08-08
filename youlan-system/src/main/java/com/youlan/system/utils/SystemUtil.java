package com.youlan.system.utils;

import com.youlan.system.constant.SystemConstant;

public class SystemUtil {
    /**
     * 根据configKey生成对应redis key
     */
    public static String getConfigRedisKey(String configKey) {
        return SystemConstant.REDIS_PREFIX_CONFIG + configKey;
    }

    /**
     * 根据typeKey生成对应redis key
     */
    public static String getDictRedisKey(String typeKey) {
        return SystemConstant.REDIS_PREFIX_DICT + typeKey;
    }

}
