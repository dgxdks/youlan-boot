package com.youlan.common.storage.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.entity.StorageConfig;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.common.storage.mapper.StorageConfigMapper;
import com.youlan.common.storage.utils.StorageUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static com.youlan.common.db.constant.DBConstant.VAL_YES;

@Service
@AllArgsConstructor
public class StorageConfigService extends BaseServiceImpl<StorageConfigMapper, StorageConfig> {

    /**
     * 获取或默认存储配置
     */
    public StorageConfig loadOrDefaultStorageConfigCache(String platform) {
        return StrUtil.isBlank(platform) ? this.loadDefaultStorageConfigCache() :
                this.loadStorageConfigCache(platform);
    }

    /**
     * 获取存储配置
     */
    public StorageConfig loadStorageConfigCache(String platform) {
        StorageConfig storageConfig = RedisHelper.getCacheObject(StorageUtil.getStorageConfigRedisKey(platform));
        if (ObjectUtil.isNotNull(storageConfig)) {
            return storageConfig;
        }
        storageConfig = this.loadDefaultStorageConfig();
        if (ObjectUtil.isNotNull(storageConfig)) {
            RedisHelper.setCacheObject(StorageUtil.getStorageConfigRedisKey(platform), storageConfig);
        }
        return storageConfig;
    }

    /**
     * 获取默认存储配置
     */
    public StorageConfig loadDefaultStorageConfigCache() {
        StorageConfig storageConfig = RedisHelper.getCacheObject(StorageUtil.getDefaultStorageConfigRedisKey());
        if (ObjectUtil.isNotNull(storageConfig)) {
            return storageConfig;
        }
        storageConfig = this.loadDefaultStorageConfig();
        if (ObjectUtil.isNotNull(storageConfig)) {
            RedisHelper.setCacheObject(StorageUtil.getDefaultStorageConfigRedisKey(), storageConfig);
        }
        return storageConfig;
    }


    /**
     * 重新加载存储配置缓存
     */
    public void reloadDefaultStorageConfigCache() {
        StorageConfig storageConfig = this.loadStorageConfig(null);
        RedisHelper.setCacheObject(StorageUtil.getDefaultStorageConfigRedisKey(), storageConfig);
    }

    /**
     * 重新根据平台名称获取对应存储配置缓存
     */
    public void reloadStorageConfigCache(String platform) {
        StorageConfig storageConfig = this.loadStorageConfig(platform);
        RedisHelper.setCacheObject(StorageUtil.getStorageConfigRedisKey(platform), storageConfig);
    }

    /**
     * 刷新存储配置缓存
     */
    public void refreshStorageConfigCache() {
        String defaultStorageConfigRedisKey = StorageUtil.getDefaultStorageConfigRedisKey();
        String storageConfigRedisKeyPattern = StorageUtil.getStorageConfigRedisKey(StringPool.ASTERISK);
        RedisHelper.deleteKeysByPattern(storageConfigRedisKeyPattern);
        RedisHelper.deleteCacheObject(defaultStorageConfigRedisKey);
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 重置默认存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetDefaultStorageConfig() {
        this.lambdaUpdate()
                .set(StorageConfig::getIsDefault, DBConstant.VAL_NO)
                .eq(StorageConfig::getIsDefault, DBConstant.VAL_YES)
                .update();
    }

    /**
     * 如果存在返回存储配置
     */
    public StorageConfig loadStorageConfigIfExists(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException("存储配置不存在"));
    }

    /**
     * 新增存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStorageConfig(StorageConfig storageConfig) {
        beforeAddOrUpdateStorageConfig(storageConfig);
        //生成platform
        storageConfig.setPlatform(StorageUtil.getStoragePlatform(storageConfig.getType()));
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        if (DBConstant.yesNo2Boolean(storageConfig.getIsDefault())) {
            this.resetDefaultStorageConfig();
        }
        boolean save = this.save(storageConfig);
        if (!save) {
            throw new BizRuntimeException("新增存储配置失败");
        }
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 修改存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfig(StorageConfig storageConfig) {
        String platform = storageConfig.getPlatform();
        beforeAddOrUpdateStorageConfig(storageConfig);
        // 平台名称不能更改
        storageConfig.setPlatform(null);
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        if (DBConstant.yesNo2Boolean(storageConfig.getIsDefault())) {
            this.resetDefaultStorageConfig();
        }
        boolean update = this.updateById(storageConfig);
        if (!update) {
            throw new BizRuntimeException("更新存储配置失败");
        }
        this.reloadStorageConfigCache(platform);
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 删除存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeStorageConfigs(List<Long> ids) {
        List<StorageConfig> storageConfigs = this.listByIds(ids);
        //如果包含默认存储配置则不允许删除
        List<StorageConfig> defaultStorageConfigs = ListHelper.filterList(storageConfigs, storageConfig -> VAL_YES.equals(storageConfig.getIsDefault()));
        if (CollectionUtil.isNotEmpty(defaultStorageConfigs)) {
            throw new BizRuntimeException("存储配置设置为默认时不允许删除");
        }
        this.removeByIds(ids);
        //删除配置缓存
        storageConfigs.forEach(storageConfig -> {
            try {
                RedisHelper.deleteCacheObject(StorageUtil.getStorageConfigRedisKey(storageConfig.getPlatform()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 存储配置状态修改
     */
    public void updateStorageConfigStatus(Long id, String status) {
        StorageConfig storageConfig = this.loadStorageConfigIfExists(id);
        StorageConfig updateStorageConfig = new StorageConfig();
        updateStorageConfig.setId(id);
        updateStorageConfig.setStatus(status);
        boolean update = this.updateById(updateStorageConfig);
        if (!update) {
            throw new BizRuntimeException("存储配置状态修改失败");
        }
        this.reloadStorageConfigCache(storageConfig.getPlatform());
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 存储配置是否默认修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfigIsDefault(Long id, String isDefault) {
        StorageConfig storageConfig = this.loadStorageConfigIfExists(id);
        this.resetDefaultStorageConfig();
        storageConfig.setIsDefault(isDefault);
        boolean update = this.updateById(storageConfig);
        if (!update) {
            throw new BizRuntimeException("存储配置是否默认修改失败");
        }
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 获取存储配置
     */
    public StorageConfig loadStorageConfig(String platform) {
        List<StorageConfig> storageConfigs = this.lambdaQuery()
                .eq(StorageConfig::getPlatform, platform)
                .list();
        return CollectionUtil.getFirst(storageConfigs);
    }

    /**
     * 获取默认存储配置
     */
    public StorageConfig loadDefaultStorageConfig() {
        List<StorageConfig> storageConfigs = this.lambdaQuery()
                .eq(StorageConfig::getIsDefault, VAL_YES)
                .list();
        return CollectionUtil.getFirst(storageConfigs);
    }

    /**
     * 新增或修改前要执行的操作
     */
    public void beforeAddOrUpdateStorageConfig(StorageConfig storageConfig) {
        storageConfig.setDomain(StorageHelper.formatDomain(storageConfig.getDomain()));
        storageConfig.setBasePath(StorageHelper.formatBasePath(storageConfig.getBasePath()));
    }
}
