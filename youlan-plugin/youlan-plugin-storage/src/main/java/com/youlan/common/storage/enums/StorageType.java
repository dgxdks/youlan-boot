package com.youlan.common.storage.enums;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum StorageType {
    LOCAL("LOCAL", "本地存储"),
    HUAWEI_OBS("HUAWEI_OBS", "华为OBS"),
    ALIYUN_OSS("ALIYUN_OSS", "阿里云OSS"),
    QINIU_KODO("QINIU_KODO", "七牛KODO"),
    TENCENT_COS("TENCENT_COS", "腾讯COS"),
    BAIDU_BOS("BAIDU_BOS", "百度BOS"),
    MINIO("MINIO", "MINIO"),
    AMAZON_S3("AMAZON_S3", "AmazonS3");

    private final String code;
    private final String text;

    public static StorageType getStorageType(String code) {
        List<StorageType> storageTypes = Arrays.stream(StorageType.values())
                .filter(type -> type.getCode().equals(code))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(storageTypes)) {
            return CollectionUtil.getFirst(storageTypes);
        }
        return null;
    }
}
