package com.youlan.plugin.http.helper;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserAgentHelper {
    public static List<String> MOB_DEVICES_NAMES = Arrays.asList("android", "ios", "ipad", "iphone", "mobile");
    public static List<String> WEB_DEVICES_NAMES = Arrays.asList("mac-os-x", "linux", "internet-explorer", "firefox", "computer", "chrome");
    public static List<String> MOB_AGENTS = new ArrayList<>();
    public static List<String> WEB_AGENTS = new ArrayList<>();

    static {
        String agentStr = IoUtil.read(new ClassPathResource("ua/useragent.json").getStream(), Charset.defaultCharset());
        JSONObject jsonObject = JSONUtil.parseObj(agentStr);
        jsonObject.forEach((key, value) -> {
            if (MOB_DEVICES_NAMES.contains(key)) {
                ((cn.hutool.json.JSONArray) value).forEach(agent -> MOB_AGENTS.add(agent.toString()));
            } else if (WEB_DEVICES_NAMES.contains(key)) {
                ((cn.hutool.json.JSONArray) value).forEach(agent -> {
                    if (!agent.toString().contains("Android")) {
                        WEB_AGENTS.add(agent.toString());
                    }
                });
            }
        });
    }

    public static String getMob() {
        return MOB_AGENTS.get(ThreadLocalRandom.current().nextInt(MOB_AGENTS.size()));
    }

    public static String getWeb() {
        return WEB_AGENTS.get(ThreadLocalRandom.current().nextInt(WEB_AGENTS.size()));
    }
}
