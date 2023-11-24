package com.youlan.plugin.region.config;

import com.youlan.plugin.region.enums.SearcherModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "youlan.common.region")
public class RegionProperties {
    /**
     * ip2region xdb文件路径
     */
    private SearcherModel searcherModel;

    /**
     * ip2region xdb文件路径
     */
    private String xdbPath;
}
