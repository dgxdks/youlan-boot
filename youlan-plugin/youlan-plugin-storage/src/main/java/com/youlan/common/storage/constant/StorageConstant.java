package com.youlan.common.storage.constant;

public class StorageConstant {

    // ******************** 缓存前缀 ********************
    /**
     * 存储配置缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_CONFIG = "system_storage_config:";
    /**
     * 存储记录objectId缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_OBJECT_ID = "system_storage_record:objectId:";
    /**
     * 存储记录fileName缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_FILE_NAME = "system_storage_record:fileName:";
    /**
     * 存储记录url缓存前缀
     */
    public static final String REDIS_PREFIX_STORAGE_URL = "system_storage_record:url:";

}
