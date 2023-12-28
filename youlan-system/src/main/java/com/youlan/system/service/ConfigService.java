package com.youlan.system.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.Config;
import com.youlan.system.mapper.ConfigMapper;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigService extends BaseServiceImpl<ConfigMapper, Config> {

    /**
     * 获取布尔类型配置值
     */
    public boolean getConfigValueBoolean(String configKey) {
        Config config = this.loadConfigCacheByConfigKeyNotNull(configKey);
        String configValue = config.getConfigValue();
        Boolean configValueBoolean = Convert.toBool(configValue, null);
        if (ObjectUtil.isNull(configValueBoolean)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的布尔配置值失败", configKey));
        }
        return configValueBoolean;
    }

    /**
     * 获取数字类型配置值
     */
    public Number getConfigValueNumber(String configKey) {
        Config config = this.loadConfigCacheByConfigKeyNotNull(configKey);
        String configValue = config.getConfigValue();
        Number configValueNumber = Convert.toNumber(configValue, null);
        if (ObjectUtil.isNull(configValueNumber)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的数值配置值失败", configKey));
        }
        return configValueNumber;
    }

    /**
     * 获取字符类型配置值
     */
    public String getConfigValueString(String configKey) {
        Config config = this.loadConfigCacheByConfigKeyNotNull(configKey);
        String configValue = config.getConfigValue();
        if (StrUtil.isBlank(configValue)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的字符配置值失败", configKey));
        }
        return configValue;
    }

    /**
     * 根据配置键名获取配置缓存且不为空
     */
    public Config loadConfigCacheByConfigKeyNotNull(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }
        String redisKey = SystemUtil.getConfigRedisKey(configKey);
        Config config = RedisHelper.getCacheObject(redisKey);
        if (ObjectUtil.isNull(config)) {
            config = loadConfigByConfigKeyNotNull(configKey);
        }
        if (ObjectUtil.isNotNull(config)) {
            RedisHelper.setCacheObject(redisKey, config);
        }
        return config;
    }

    /**
     * 设置所有配置缓存
     */
    public void setConfigCache() {
        List<Config> cacheList = this.list();
        Map<String, Config> configGroupMap = cacheList.stream()
                .collect(Collectors.toMap(Config::getConfigKey, config -> config));
        configGroupMap.forEach(this::setConfigCache);
    }

    /**
     * 设置配置缓存
     */
    public void setConfigCache(String configKey, Config config) {
        RedisHelper.setCacheObject(SystemUtil.getConfigRedisKey(configKey), config);
    }

    /**
     * 删除配置缓存
     */
    public void removeConfigCache(String configKey) {
        RedisHelper.deleteCacheObject(SystemUtil.getConfigRedisKey(configKey));
    }

    /**
     * 删除配置缓存
     */
    public void removeConfigCache() {
        RedisHelper.deleteKeysByPattern(SystemConstant.REDIS_PREFIX_CONFIG + StringPool.ASTERISK);
    }

    /**
     * 刷新配置缓存
     */
    public void refreshConfigCache() {
        this.removeConfigCache();
        this.setConfigCache();
    }

    /**
     * 获取配置且不为空
     */
    public Config loadConfigNotNull(Serializable id) {
        return loadOneOpt(id)
                .orElseThrow(ApiResultCode.B0016::getException);
    }

    /**
     * 根据配置键名查询配置且不为空
     */
    public Config loadConfigByConfigKeyNotNull(String configKey) {
        Config config = loadConfigByConfigKey(configKey);
        Assert.notNull(config, ApiResultCode.B0016::getException);
        return config;
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

    /**
     * 新增系统配置
     */
    public void addConfig(Config config) {
        beforeAddOrUpdateConfig(config);
        this.save(config);
    }

    /**
     * 修改系统配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Config config) {
        beforeAddOrUpdateConfig(config);
        // 如果前后configKey不一致则需要删除旧的configKey缓存
        Config oldConfig = this.loadConfigNotNull(config.getId());
        boolean update = this.updateById(config);
        if (update) {
            removeConfigCache(oldConfig.getConfigKey());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeConfig(List<Long> idList) {
        List<String> configKeyList = new ArrayList<>();
        for (Long id : idList) {
            //判断参数类型，内置参数不能删除
            Config config = this.loadConfigNotNull(id);
            if (SystemConstant.CONFIG_TYPE_INNER.equals(config.getConfigType())) {
                throw new BizRuntimeException(ApiResultCode.B0017);
            }
            configKeyList.add(config.getConfigKey());
        }
        boolean removed = this.removeBatchByIds(idList);
        if (removed) {
            configKeyList.forEach(this::removeConfigCache);
        }
    }

    /**
     * 新增或修改配置前置操作
     */
    public void beforeAddOrUpdateConfig(Config config) {
        boolean configExists = this.lambdaQuery()
                .select(Config::getId)
                .eq(Config::getConfigKey, config.getConfigKey())
                .ne(ObjectUtil.isNotNull(config.getId()), Config::getId, config.getId())
                .exists();
        if (configExists) {
            throw new BizRuntimeException(StrUtil.format("参数键名{}已存在", config.getConfigKey()));
        }
    }
}
