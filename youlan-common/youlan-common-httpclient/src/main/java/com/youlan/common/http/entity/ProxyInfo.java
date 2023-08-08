package com.youlan.common.http.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProxyInfo {
    private String ip;
    private String port;
    private boolean isSsl;
    private int connectTimeout = 3 * 1000;
    private int connectionRequestTimeout = 10 * 1000;
    private int socketTimeout = 4 * 1000;

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return StrUtil.format("{}:{}", ip, port);
    }

    public boolean isSsl() {
        return isSsl;
    }

    public void setSsl(boolean ssl) {
        isSsl = ssl;
    }

    public boolean getIsSsl() {
        return isSsl;
    }
}
