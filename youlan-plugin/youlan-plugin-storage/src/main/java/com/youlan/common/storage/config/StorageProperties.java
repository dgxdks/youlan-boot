package com.youlan.common.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "youlan.storage")
public class StorageProperties {
    /**
     * 文件存储根路径(默认/, 指定路径时必须以/结尾)
     */
    private String storagePath = "/";

    /**
     * 是否开启存储记录
     */
    private Boolean recordEnabled = true;

    /**
     * 存储记录缓存时间(s)
     */
    private long recordCacheTimeout = 1800;

    /**
     * 是否开启存储配置刷新
     */
    private Boolean refreshConfigEnabled = true;

    /**
     * 存储配置刷新时间间隔(s)
     */
    private long refreshConfigInterval = 60 * 60;
}
