package com.youlan.system.config;

import com.youlan.common.storage.helper.StorageHelper;
import lombok.Data;

@Data
public class StorageProperties {
    /**
     * 文件存储根路径(默认/, 指定路径时必须以/结尾)
     */
    private String storagePath = "/";

    /**
     * 是否开始存储记录
     */
    private boolean recordEnabled = true;

    /**
     * 存储记录缓存时间(s)
     */
    private long recordCacheTimeout = 1800;

    public boolean getRecordEnabled() {
        return recordEnabled;
    }

    public void setRecordEnabled(boolean recordEnabled) {
        this.recordEnabled = recordEnabled;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = StorageHelper.formatStoragePath(storagePath);
    }
}
