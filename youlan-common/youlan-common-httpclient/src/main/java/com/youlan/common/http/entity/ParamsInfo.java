package com.youlan.common.http.entity;

import cn.hutool.core.util.StrUtil;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ParamsInfo extends HashMap<String, Object> {
    public ParamsInfo(String params) {
        init(params);
    }

    public ParamsInfo() {
    }

    public static ParamsInfo create(String params) {
        return new ParamsInfo(params);
    }

    public static ParamsInfo create() {
        return new ParamsInfo();
    }

    public ParamsInfo append(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public String getStr(String name) {
        Object value = get(name);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public void init(String params) {
        if (StrUtil.isBlank(params)) {
            return;
        }
        if (!params.contains("&") || !params.contains("=")) {
            return;
        }
        String[] paramPair = params.split("&");
        if (paramPair.length == 0) {
            return;
        }
        Arrays.stream(paramPair).forEach(pair -> {
            if (!pair.contains("=")) {
                return;
            }
            String[] split = pair.split("=");
            if (split.length == 1) {
                this.put(split[0], "");
            }
            if (split.length == 2) {
                this.put(split[0], split[1]);
            }
        });
    }

    @Override
    public String toString() {
        List<BasicNameValuePair> formData = this.entrySet()
                .stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), StrUtil.str(entry.getValue(), Charset.defaultCharset())))
                .collect(Collectors.toList());
        return URLEncodedUtils.format(formData, StandardCharsets.UTF_8);
    }

    public void printPretty() {
        this.forEach((key, value) -> System.out.println(key + "=" + URLDecoder.decode(value.toString())));
    }
}
