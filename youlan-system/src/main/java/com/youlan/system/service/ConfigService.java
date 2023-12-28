package com.youlan.system.service;

import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Config;
import com.youlan.system.mapper.ConfigMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigService extends BaseServiceImpl<ConfigMapper, Config> {
    /**
     * 如果存在获取配置
     */
    public Config loadConfigIfExists(Serializable id) {
        return loadOneOpt(id)
                .orElseThrow(ApiResultCode.B0016::getException);
    }

    /**
     * 根据配置键名查询配置
     */
    public Config loadConfigByConfigKeyIfExists(String configKey) {
        return this.loadOneOpt(Config::getConfigKey, configKey)
                .orElseThrow(ApiResultCode.B0016::getException);
    }

    /**
     * 根据配置键名查询配置
     */
    public Config loadConfigByConfigKey(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }
        return this.loadOne(Config::getConfigKey, configKey);
    }
}
