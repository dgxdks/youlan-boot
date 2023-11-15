package com.youlan.plugin.region.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.youlan.plugin.region.searcher.AbstractSearcher;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegionHelper {

    /**
     * ipv4地址转区域信息
     */
    public static String ip2Region(String ip) {
        return abstractSearcher().search(ip);
    }

    /**
     * ipv4地址转区域信息
     */
    public static String ip2Region(long ip) {
        return abstractSearcher().search(ip);
    }

    /**
     * 获取{@link AbstractSearcher}实例
     */
    public static AbstractSearcher abstractSearcher() {
        return SpringUtil.getBean(AbstractSearcher.class);
    }
}
