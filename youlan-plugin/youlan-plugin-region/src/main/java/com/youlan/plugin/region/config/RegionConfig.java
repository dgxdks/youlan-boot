package com.youlan.plugin.region.config;

import cn.hutool.extra.spring.SpringUtil;
import com.youlan.plugin.region.enums.SearcherModel;
import com.youlan.plugin.region.searcher.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;

@Import(SpringUtil.class)
@Configuration
@EnableConfigurationProperties({RegionProperties.class})
@AllArgsConstructor
public class RegionConfig {
    private final RegionProperties regionProperties;


    @Bean
    public AbstractSearcher regionHelper() throws IOException {
        SearcherModel searcherModel = regionProperties.getSearcherModel();
        String xdbPath = regionProperties.getXdbPath();
        URL xdbUrl = ResourceUtils.getURL(xdbPath);
        switch (searcherModel) {
            case FILE:
                return new FileSearcher(xdbUrl.getFile());
            case INDEX:
                return new VectorIndexSearcher(xdbUrl.getFile());
            case NETWORK:
                return new NetworkSearcher(xdbUrl.getFile());
            case MEMORY:
            default:
                return new ContentSearcher(xdbUrl.getFile());
        }
    }
}
