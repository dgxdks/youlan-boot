package com.youlan.system.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.Config;
import com.youlan.system.service.ConfigService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConfigBizService {
    private final ConfigService configService;
    private final RedisHelper redisHelper;

    @PostConstruct
    public void iniConfigCache() {
        this.setAllConfigCache();
    }

    /**
     * 设置所有配置缓存
     */
    public void setAllConfigCache() {
        List<Config> cacheList = configService.list();
        Map<String, Config> configGroupMap = cacheList.stream()
                .collect(Collectors.toMap(Config::getConfigKey, config -> config));
        configGroupMap.forEach(this::setConfigCache);
    }

    /**
     * 设置配置缓存
     */
    public void setConfigCache(String configKey, Config config) {
        redisHelper.set(SystemUtil.getConfigRedisKey(configKey), config);
    }

    /**
     * 删除配置缓存
     */
    public void removeConfigCache(String configKey) {
        redisHelper.delete(SystemUtil.getConfigRedisKey(configKey));
    }

    /**
     * 根据配置键名获取配置(支持缓存)
     */
    public Config getConfigByConfigKey(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }
        String redisKey = SystemUtil.getConfigRedisKey(configKey);
        Config config = (Config) redisHelper.getOpt(redisKey)
                .orElseGet(() -> configService.loadByConfigKey(configKey));
        if (ObjectUtil.isNotNull(config)) {
            redisHelper.set(redisKey, config);
        }
        return config;
    }

    /**
     * 根据配置键名获取配置且不能为空(支持缓存)
     */
    public Config getConfigByConfigKeyIfExist(String configKey) {
        Config config = getConfigByConfigKey(configKey);
        if (ObjectUtil.isNull(config)) {
            throw new BizRuntimeException(StrUtil.format("配置键值[{}]不存在", configKey));
        }
        return config;
    }

    /**
     * 新增系统配置
     */
    public boolean addConfig(Config config) {
        checkConfigKeyRepeat(config);
        return configService.save(config);
    }

    /**
     * 修改系统配置
     */
    public boolean updateConfig(Config config) {
        checkConfigKeyRepeat(config);
        boolean update = configService.updateById(config);
        if (update) {
            removeConfigCache(config.getConfigKey());
        }
        return update;
    }

    /**
     * 校验配置键名是否重复
     */
    public void checkConfigKeyRepeat(Config config) {
        List<Config> configList = configService.lambdaQuery()
                .select(Config::getId)
                .eq(Config::getConfigKey, config.getConfigKey())
                .list();
        if (CollectionUtil.isEmpty(configList)) {
            return;
        }
        for (Config retConfig : configList) {
            //存在与当前主键ID不匹配的则说明存在重复
            if (!retConfig.getId().equals(config.getId())) {
                throw new BizRuntimeException(StrUtil.format("当前配置键名[{}]已存在", config.getConfigKey()));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeConfig(List<Long> idList) {
        for (Long id : idList) {
            //判断参数类型，内置参数不能删除
            Optional<Config> configOpt = configService.lambdaQuery()
                    .select(Config::getConfigType)
                    .eq(Config::getId, id).oneOpt();
            if (configOpt.isEmpty()) {
                throw new BizRuntimeException(StrUtil.format("配置ID[{}]不存在", id));
            }
            if (SystemConstant.CONFIG_TYPE_INNER.equals(configOpt.get().getConfigType())) {
                throw new BizRuntimeException("内置参数不能删除");
            }
            configService.removeBatchByIds(idList);
            redisHelper.delete(SystemUtil.getConfigRedisKey(configOpt.get().getConfigKey()));
        }
        return true;
    }
}
