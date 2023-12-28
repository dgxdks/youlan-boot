package com.youlan.common.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
