package com.youlan.system.service.api;

import com.youlan.common.api.ConfigApi;
import com.youlan.system.service.ConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfigApiService implements ConfigApi {

    private final ConfigService configService;

    @Override
    public boolean getConfigValueBoolean(String configKey) {
        return configService.getConfigValueBoolean(configKey);
    }

    @Override
    public Number getConfigValueNumber(String configKey) {
        return configService.getConfigValueNumber(configKey);
    }

    @Override
    public String getConfigValueString(String configKey) {
        return configService.getConfigValueString(configKey);
    }
}
