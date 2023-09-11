package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.mapper.StorageConfigMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static com.youlan.common.db.constant.DBConstant.VAL_NO;
import static com.youlan.common.db.constant.DBConstant.VAL_YES;

@Service
@AllArgsConstructor
public class StorageConfigService extends BaseServiceImpl<StorageConfigMapper, StorageConfig> {
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
     * 根据平台名称获取存储配置
     */
    public StorageConfig loadStorageConfigByPlatform(String platform) {
        List<StorageConfig> storageConfigs = this.lambdaQuery()
                .eq(StorageConfig::getPlatform, platform)
                .list();
        return CollectionUtil.getFirst(storageConfigs);
    }

    /**
     * 重置默认存储
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetDefaultStorageConfig() {
        this.lambdaUpdate()
                .set(StorageConfig::getIsDefault, VAL_NO)
                .eq(StorageConfig::getIsDefault, VAL_YES)
                .update();
    }

    /**
     * 如果存在返回存储配置
     */
    public StorageConfig loadStorageConfigIfExists(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException("存储配置不存在"));
    }
}
