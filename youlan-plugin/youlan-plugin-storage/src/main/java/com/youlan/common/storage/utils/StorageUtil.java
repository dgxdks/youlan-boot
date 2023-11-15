package com.youlan.common.storage.utils;

import cn.hutool.core.util.RandomUtil;
import com.youlan.common.storage.enums.StorageType;

import static com.youlan.common.storage.constant.StorageConstant.*;

public class StorageUtil {
    /**
     * 获取存储对象ID redis key
     */
    public static String getStorageObjectIdRedisKey(String objectId) {
        return REDIS_PREFIX_STORAGE_OBJECT_ID + objectId;
    }

    /**
     * 获取文件名称redis key
     */
    public static String getStorageFileNameRedisKey(String fileName) {
        return REDIS_PREFIX_STORAGE_FILE_NAME + fileName;
    }

    /**
     * 获取文件路径redis key
     */
    public static String getStorageUrlRedisKey(String url) {
        return REDIS_PREFIX_STORAGE_URL + url;
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
        return REDIS_PREFIX_STORAGE_CONFIG + platform;
    }

    /**
     * 获取默认存储配置redis key
     */
    public static String getDefaultStorageConfigRedisKey() {
        return getStorageConfigRedisKey("default");
    }
}
