package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.service.StorageConfigService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.youlan.common.db.constant.DBConstant.VAL_NO;
import static com.youlan.common.db.constant.DBConstant.VAL_YES;

@Service
@AllArgsConstructor
public class StorageConfigBizService {
    private final StorageConfigService storageConfigService;
    private final RedisHelper redisHelper;

    /**
     * 新增存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addStorageConfig(StorageConfig storageConfig) {
        //格式化
        this.formatStorageConfig(storageConfig);
        //生成platform
        storageConfig.setPlatform(SystemUtil.getStoragePlatform(storageConfig.getType()));
        String isDefault = storageConfig.getIsDefault();
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        if (VAL_YES.equals(isDefault)) {
            this.resetDefaultStorageConfig();
        }
        boolean save = storageConfigService.save(storageConfig);
        if (!save) {
            throw new BizRuntimeException("新增存储配置失败");
        }
        //只有保存成功才能设置缓存中的默认存储配置
        redisHelper.set(SystemUtil.getDefaultStorageConfigRedisKey(), storageConfig);
        return true;
    }

    /**
     * 修改存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStorageConfig(StorageConfig storageConfig) {
        //格式化
        this.formatStorageConfig(storageConfig);
        //拷贝一份执行更新操作的存储配置并不允许修改平台名称和存储类型
        StorageConfig updateStorageConfig = BeanUtil.copyProperties(storageConfig, StorageConfig.class);
        updateStorageConfig.setPlatform(null);
        updateStorageConfig.setType(null);
        String isDefault = storageConfig.getIsDefault();
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        if (VAL_YES.equals(isDefault)) {
            this.resetDefaultStorageConfig();
        }
        boolean update = storageConfigService.updateById(updateStorageConfig);
        if (!update) {
            throw new BizRuntimeException("更新存储配置失败");
        }
        //只有更新成功才能设置缓存中的默认存储配置
        redisHelper.set(SystemUtil.getDefaultStorageConfigRedisKey(), storageConfig);
        //删除已经生成的FileStorage
        StorageHelper.removeFileStorage(storageConfig.getPlatform());
        return true;
    }

    /**
     * 充值默认存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetDefaultStorageConfig() {
        List<Long> idList = storageConfigService.lambdaQuery()
                .eq(StorageConfig::getIsDefault, VAL_YES)
                .list()
                .stream()
                .map(StorageConfig::getId)
                .collect(Collectors.toList());
        boolean update = storageConfigService.lambdaUpdate()
                .in(StorageConfig::getId, idList)
                .set(StorageConfig::getIsDefault, VAL_NO)
                .update();
        if (!update) {
            throw new BizRuntimeException("重置默认存储配置失败");
        }
    }

    /**
     * 获取默认存储配置(支持缓存)
     */
    @Transactional(rollbackFor = Exception.class)
    public StorageConfig loadDefaultStorageConfigCacheIfExist() {
        try {
            StorageConfig storageConfig = redisHelper.get(SystemUtil.getDefaultStorageConfigRedisKey());
            if (ObjectUtil.isNotNull(storageConfig)) {
                return storageConfig;
            }
            storageConfig = storageConfigService.lambdaQuery()
                    .eq(StorageConfig::getIsDefault, VAL_YES)
                    .oneOpt()
                    .orElseThrow(() -> new BizRuntimeException("默认存储配置信息不存在"));
            redisHelper.set(SystemUtil.getDefaultStorageConfigRedisKey(), storageConfig);
            return storageConfig;
        } catch (TooManyResultsException e) {
            throw new BizRuntimeException("存储配置中存在多个默认存储配置");
        }
    }

    /**
     * 获取存储配置(支持缓存)
     */
    @Transactional(rollbackFor = Exception.class)
    public StorageConfig loadStorageConfigCacheIfExist(String platform) {
        try {
            StorageConfig storageConfig = redisHelper.get(SystemUtil.getStorageConfigRedisKey(platform));
            if (ObjectUtil.isNotNull(storageConfig)) {
                return storageConfig;
            }
            storageConfig = storageConfigService.lambdaQuery()
                    .eq(StorageConfig::getPlatform, platform)
                    .oneOpt()
                    .orElseThrow(() -> new BizRuntimeException("指定存储配置信息不存在"));
            redisHelper.set(SystemUtil.getStorageConfigRedisKey(platform), storageConfig);
            return storageConfig;
        } catch (TooManyResultsException e) {
            throw new BizRuntimeException("存储配置中存在多个相同存储平台配置");
        }
    }

    /**
     * 删除存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeStorageConfigs(List<Long> ids) {
        List<StorageConfig> storageConfigs = storageConfigService.listByIds(ids);
        //如果包含默认存储配置则不允许删除
        List<StorageConfig> defaultStorageConfigs = ListHelper.filterList(storageConfigs, storageConfig -> VAL_YES.equals(storageConfig.getIsDefault()));
        if (CollectionUtil.isNotEmpty(defaultStorageConfigs)) {
            throw new BizRuntimeException("不允许删除默认存储配置");
        }
        boolean remove = storageConfigService.removeByIds(ids);
        if (!remove) {
            throw new BizRuntimeException("删除存储配置失败");
        }
        //清空缓存
        storageConfigs.forEach(storageConfig -> {
            redisHelper.delete(SystemUtil.getStorageConfigRedisKey(storageConfig.getPlatform()));
        });

        return true;
    }

    public void formatStorageConfig(StorageConfig storageConfig) {
        storageConfig.setDomain(StorageHelper.formatDomain(storageConfig.getDomain()));
        storageConfig.setBasePath(StorageHelper.formatBasePath(storageConfig.getBasePath()));
    }
}
