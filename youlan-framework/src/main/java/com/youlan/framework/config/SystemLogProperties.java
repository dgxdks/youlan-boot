package com.youlan.framework.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SystemLogProperties {
    /**
     * 是否开启操作日志
     */
    private boolean enabled;

    /**
     * 是否开启位置查询
     */
    private boolean regionEnabled;

    /**
     * 是否异步处理
     */
    private boolean asyncEnabled;

    /**
     * 异常信息保存长度上限
     */
    private int errorLengthLimit;

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getRegionEnabled() {
        return regionEnabled;
    }

    public void setRegionEnabled(boolean regionEnabled) {
        this.regionEnabled = regionEnabled;
    }

    public boolean getAsyncEnabled() {
        return asyncEnabled;
    }

    public void setAsyncEnabled(boolean asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
    }
}
