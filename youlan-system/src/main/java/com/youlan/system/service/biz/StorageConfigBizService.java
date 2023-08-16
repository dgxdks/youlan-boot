package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.enums.StorageType;
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
        //校验存储类型
        String type = storageConfig.getType();
        StorageType storageType = StorageType.getStorageType(type);
        if (ObjectUtil.isNull(storageType)) {
            throw new BizRuntimeException("存储类型不存在");
        }
        //生成platform
        storageConfig.setPlatform(SystemUtil.getStoragePlatform(storageType));
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

    // TODO: 2023/8/16 增加的时候要判断有没有指定为默认
    // TODO: 2023/8/16 删除时要判断是否是默认的存储配置

    /**
     * 修改存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStorageConfig(StorageConfig storageConfig) {
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
    public StorageConfig getDefaultStorageConfigIfExist() {
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
    public StorageConfig getStorageConfigIfExist(String platform) {
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
}
