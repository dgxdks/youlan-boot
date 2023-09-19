package com.youlan.system.service.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.Config;
import com.youlan.system.service.ConfigService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConfigBizService {
    private final ConfigService configService;

    /**
     * 新增系统配置
     */
    public void addConfig(Config config) {
        beforeAddOrUpdateConfig(config);
        configService.save(config);
    }

    /**
     * 修改系统配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Config config) {
        beforeAddOrUpdateConfig(config);
        // 如果前后configKey不一致则需要删除旧的configKey缓存
        Config oldConfig = configService.loadConfigIfExists(config.getId());
        boolean update = configService.updateById(config);
        if (update) {
            removeConfigCache(oldConfig.getConfigKey());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeConfig(List<Long> idList) {
        List<String> configKeyList = new ArrayList<>();
        for (Long id : idList) {
            //判断参数类型，内置参数不能删除
            Config config = configService.loadConfigIfExists(id);
            if (SystemConstant.CONFIG_TYPE_INNER.equals(config.getConfigType())) {
                throw new BizRuntimeException(ApiResultCode.B0017);
            }
            configKeyList.add(config.getConfigKey());
        }
        boolean removed = configService.removeBatchByIds(idList);
        if (removed) {
            configKeyList.forEach(this::removeConfigCache);
        }
    }

    /**
     * 新增或修改配置前置操作
     */
    public void beforeAddOrUpdateConfig(Config config) {
        boolean configExists = configService.lambdaQuery()
                .select(Config::getId)
                .eq(Config::getConfigKey, config.getConfigKey())
                .ne(ObjectUtil.isNotNull(config.getId()), Config::getId, config.getId())
                .exists();
        if (configExists) {
            throw new BizRuntimeException(StrUtil.format("参数键名{}已存在", config.getConfigKey()));
        }
    }

    /**
     * 根据配置键名获取配置(支持缓存)
     */
    public Config loadConfigCacheByConfigKeyIfExists(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }
        String redisKey = SystemUtil.getConfigRedisKey(configKey);
        Config config = RedisHelper.get(redisKey);
        if (ObjectUtil.isNull(config)) {
            config = loadConfigByConfigKeyIfExist(configKey);
        }
        if (ObjectUtil.isNotNull(config)) {
            RedisHelper.set(redisKey, config);
        }
        return config;
    }

    /**
     * 根据配置键名获取配置且不能为空(支持缓存)
     */
    public Config loadConfigByConfigKeyIfExist(String configKey) {
        Config config = configService.loadConfigByConfigKeyIfExists(configKey);
        if (ObjectUtil.isNull(config)) {
            throw new BizRuntimeException(StrUtil.format("配置键值[{}]不存在", configKey));
        }
        return config;
    }

    /**
     * 设置所有配置缓存
     */
    public void setConfigCache() {
        List<Config> cacheList = configService.list();
        Map<String, Config> configGroupMap = cacheList.stream()
                .collect(Collectors.toMap(Config::getConfigKey, config -> config));
        configGroupMap.forEach(this::setConfigCache);
    }

    /**
     * 设置配置缓存
     */
    public void setConfigCache(String configKey, Config config) {
        RedisHelper.set(SystemUtil.getConfigRedisKey(configKey), config);
    }

    /**
     * 删除配置缓存
     */
    public void removeConfigCache(String configKey) {
        RedisHelper.delete(SystemUtil.getConfigRedisKey(configKey));
    }

    /**
     * 删除配置缓存
     */
    public void removeConfigCache() {
        RedisHelper.deleteByPattern(SystemConstant.REDIS_PREFIX_CONFIG + StringPool.ASTERISK);
    }

    /**
     * 刷新配置缓存
     */
    public void refreshConfigCache() {
        this.removeConfigCache();
        this.setConfigCache();
    }
}
