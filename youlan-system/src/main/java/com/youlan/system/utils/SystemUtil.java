package com.youlan.system.utils;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.RandomUtil;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.system.constant.SystemConstant;

import static com.youlan.system.constant.SystemConstant.*;

public class SystemUtil {
    public static String getStorageObjectIdRedisKey(String objectId) {
        return REDIS_PREFIX_STORAGE_OBJECT_ID + objectId;
    }

    public static String getStorageFileNameRedisKey(String fileName) {
        return REDIS_PREFIX_STORAGE_FILE_NAME + fileName;
    }

    public static String getLoginRetryRedisKey(String userName) {
        return REDIS_PREFIX_LOGIN_RETRY + userName;
    }

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

    /**
     * 生成存储平台名称
     */
    public static String getStoragePlatform(StorageType storageType) {
        return storageType.getCode() + "-" + RandomUtil.randomString(8);
    }

    /**
     * 获取存储配置redis key
     */
    public static String getStorageConfigRedisKey(String platform) {
        return SystemConstant.REDIS_PREFIX_STORAGE_CONFIG + platform;
    }

    /**
     * 获取默认存储配置redis key
     */
    public static String getDefaultStorageConfigRedisKey() {
        return getStorageConfigRedisKey("default");
    }

}
