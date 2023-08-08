package com.youlan.common.http.entity;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.http.Header;

import java.util.*;
import java.util.stream.Collectors;

public class CookieInfo {
    private final Map<String, String> cookies = new HashMap<>();
    private String mobile;
    private String name;
    private String cookieId = IdUtil.objectId();

    public static CookieInfo create() {
        return new CookieInfo();
    }

    public static CookieInfo create(String cookies) {
        return new CookieInfo(cookies);
    }

    public CookieInfo() {

    }

    public CookieInfo(String cookies) {
        if (StrUtil.isBlank(cookies) || !cookies.contains(";")) {
            return;
        }
        String[] cookieSplit = cookies.split(";");
        Map<String, String> result = Arrays.stream(cookieSplit)
                .map(String::trim)
                .map(pair -> pair.split("="))
                .filter(pair -> pair.length == 2)
                .filter(pair -> StrUtil.isAllNotBlank(pair[0], pair[1]))
                .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1], (key1, kye2) -> kye2));
        this.putAll(result);
    }

    public CookieInfo(Map<String, String> cookies) {
        this.putAll(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public CookieInfo put(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public CookieInfo putAll(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
        return this;
    }

    public CookieInfo putFormHeaders(Header[] headers, String... names) {
        if (ArrayUtil.isEmpty(headers)) {
            return this;
        }
        Set<String> nameSet = ArrayUtil.isNotEmpty(names) ? new HashSet<>(Arrays.asList(names)) : null;
        Arrays.asList(headers).forEach(header -> {
            String name = header.getName();
            String value = header.getValue();
            if (StrUtil.equalsAnyIgnoreCase(name, "Set-Cookie")) {
                //Set-Cookie对应的格式: tradeMA=119;Max-Age=25920000;domain=.suning.com;path=/;HTTPOnly;
                String[] split = value.split(";");
                String cookieInfoStr = split[0];
                String[] cookieSplit = cookieInfoStr.split("=");
                if (cookieSplit.length == 2) {
                    String cookieName = cookieSplit[0];
                    String cookieValue = cookieSplit[1];
                    if (nameSet == null) {
                        cookies.put(cookieName, cookieValue);
                    } else if (nameSet.contains(cookieName)) {
                        cookies.put(cookieName, cookieValue);
                    }
                }
            }
        });
        return this;
    }

    public CookieInfo remove(String name) {
        cookies.remove(name);
        return this;
    }

    public boolean exist(String name) {
        return cookies.containsKey(name);
    }

    public boolean notExist(String name) {
        return !exist(name);
    }

    public CookieInfo copy() {
        return new CookieInfo(this.toString())
                .setMobile(mobile)
                .setName(name)
                .setCookieId(cookieId);
    }

    @Override
    public String toString() {
        List<String> cookieList = new ArrayList<>();
        cookies.forEach((key, value) -> cookieList.add(StrUtil.format("{}={}", key, value)));
        return StrUtil.join("; ", cookieList);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getCookiesStr() {
        return toString();
    }

    public String getMobile() {
        return mobile;
    }

    public CookieInfo setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getCookieId() {
        return cookieId;
    }

    public CookieInfo setCookieId(String cookieId) {
        this.cookieId = cookieId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CookieInfo setName(String name) {
        this.name = name;
        return this;
    }
}
