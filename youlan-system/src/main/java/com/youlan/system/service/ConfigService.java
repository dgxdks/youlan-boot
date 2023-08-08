package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Config;
import com.youlan.system.mapper.ConfigMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigService extends BaseServiceImpl<ConfigMapper, Config> {
    /**
     * 根据配置键名查询配置
     */
    public Config loadByConfigKey(String configKey) {
        return this.loadOne(Config::getConfigKey, configKey);
    }
}
