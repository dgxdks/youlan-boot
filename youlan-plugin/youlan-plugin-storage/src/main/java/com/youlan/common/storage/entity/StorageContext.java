package com.youlan.common.storage.entity;


import com.youlan.common.storage.enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StorageContext {

    private StorageType type;

    private String platform;

    private String domain;

    private String basePath;

    private String storagePath;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String endPoint;

    private String region;
}
