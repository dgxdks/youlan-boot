package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.youlan.common.storage.entity.StorageContext;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.system.entity.StorageConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class StorageBizService {
    private final StorageConfigBizService storageConfigBizService;

    /**
     * 上传文件
     */
    public FileInfo upload(MultipartFile file, String platform) {
        StorageConfig storageConfig;
        if (StrUtil.isNotBlank(platform)) {
            storageConfig = storageConfigBizService.getStorageConfigIfExist(platform);
        } else {
            storageConfig = storageConfigBizService.getDefaultStorageConfigIfExist();
        }
        return StorageHelper.of(file, storageConfig.getPlatform(), () -> createStorageContext(storageConfig))
                .upload();
    }

    /**
     * 创建存储上下文
     */
    public StorageContext createStorageContext(StorageConfig storageConfig) {
        return BeanUtil.copyProperties(storageConfig, StorageContext.class)
                .setType(StorageType.getStorageType(storageConfig.getType()));
    }
}
