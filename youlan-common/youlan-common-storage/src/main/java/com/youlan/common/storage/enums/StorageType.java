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
    LOCAL("local", "本地存储"),
    HUAWEI_OBS("huaweiObs", "华为OBS"),
    ALIYUN_OSS("aliyunOss", "阿里云OSS"),
    QINIU_KODO("qiniuKodo", "七牛KODO"),
    TENCENT_COS("tencentCos", "腾讯COS"),
    BAIDU_BOS("baiduBos", "百度BOS"),
    MINIO("minio", "MINIO"),
    AMAZON_S3("amazonS3", "AmazonS3");

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
