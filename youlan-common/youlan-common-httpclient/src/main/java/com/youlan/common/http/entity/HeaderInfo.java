package com.youlan.common.http.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HeaderInfo {
    private final List<Header> headers = new ArrayList<>();

    public static HeaderInfo create() {
        return new HeaderInfo();
    }

    public static HeaderInfo create(CookieInfo cookieInfo) {
        return new HeaderInfo()
                .appendCookie(cookieInfo);
    }

    public HeaderInfo appendCookie(String cookie) {
        return this.append("Cookie", cookie);
    }

    public HeaderInfo appendCookie(CookieInfo cookieInfo) {
        return this.appendCookie(cookieInfo.toString());
    }

    public HeaderInfo appendXMLHttpRequest() {
        return this.append("x-requested-with", "XMLHttpRequest");
    }

    public HeaderInfo appendUserAgent(String userAgent) {
        try {
            if (StrUtil.isBlank(userAgent)) {
                return this;
            }
            UserAgentUtil.parse(userAgent);
            return this.append("User-Agent", userAgent);
        } catch (Exception e) {
            log.error("设置UserAgent失败: {}", userAgent);
            return this;
        }
    }

    public HeaderInfo appendReferer(String referer) {
        return this.append("Referer", referer);
    }

    public HeaderInfo appendIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return this;
        }
        if (ip.contains("127.0.0.1") || ip.contains("localhost")) {
            return this;
        }
        return this.append("X-Forwarded-For", ip)
                .append("X-Originating-IP", ip)
                .append("X-Remote-IP", ip)
                .append("X-Remote-Addr", ip)
                .append("X-Client-IP", ip);
    }

    public HeaderInfo appendHost(String host) {
        return this.append("Host", host);
    }

    public HeaderInfo appendOrigin(String origin) {
        return this.append("Origin", origin);
    }

    public HeaderInfo append(BasicHeader header) {
        headers.add(header);
        return this;
    }

    public HeaderInfo append(String key, String value) {
        return this.append(new BasicHeader(key, value));
    }

    public Header[] toArray() {
        if (CollectionUtil.isEmpty(headers)) {
            return null;
        } else {
            return headers.toArray(new Header[0]);
        }
    }
}
