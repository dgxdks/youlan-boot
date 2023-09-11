package com.youlan.common.storage.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xuyanwu.spring.file.storage.FileStorageProperties;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.FileStorageServiceBuilder;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.storage.entity.StorageContext;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.proxy.FileStorageServiceProxy;
import lombok.Getter;
import org.springframework.util.ResourceUtils;

import java.util.Collections;

import static com.youlan.common.storage.enums.StorageType.LOCAL;

@Getter
public class StorageHelper {
    private static final FileStorageService fileStorageService = SpringUtil.getBean(FileStorageService.class);


    public static UploadPretreatment of() {
        return fileStorageService.of();
    }

    public static UploadPretreatment of(FileStorage fileStorage) {
        return fileStorageService.of()
                .setPlatform(fileStorage.getPlatform())
                .setFileStorageService(new FileStorageServiceProxy(fileStorageService, fileStorage));
    }

    public static UploadPretreatment of(StorageContext storageContext) {
        return of(createFileStorage(storageContext));
    }

    public UploadPretreatment of(Object source) {
        return fileStorageService.of(source);
    }

    public static UploadPretreatment of(Object source, FileStorage fileStorage) {
        return fileStorageService.of(source)
                .setPlatform(fileStorage.getPlatform())
                .setFileStorageService(new FileStorageServiceProxy(fileStorageService, fileStorage));
    }

    public static UploadPretreatment of(Object source, StorageContext storageContext) {
        return of(source, createFileStorage(storageContext));
    }

    public static UploadPretreatment of(Object source, String name) {
        return fileStorageService.of(source, name);
    }

    public static UploadPretreatment of(Object source, String name, FileStorage fileStorage) {
        return fileStorageService.of(source, name)
                .setPlatform(fileStorage.getPlatform())
                .setFileStorageService(new FileStorageServiceProxy(fileStorageService, fileStorage));
    }

    public static UploadPretreatment of(Object source, String name, StorageContext storageContext) {
        return of(source, name, createFileStorage(storageContext));
    }

    public static UploadPretreatment of(Object source, String name, String contentType) {
        return fileStorageService.of(source, name, contentType);
    }

    public static UploadPretreatment of(Object source, String name, String contentType, FileStorage fileStorage) {
        return fileStorageService.of(source, name, contentType)
                .setPlatform(fileStorage.getPlatform())
                .setFileStorageService(new FileStorageServiceProxy(fileStorageService, fileStorage));
    }

    public static UploadPretreatment of(Object source, String name, String contentType, StorageContext storageContext) {
        return of(source, name, contentType, createFileStorage(storageContext));
    }

    public static UploadPretreatment of(Object source, String name, String contentType, Long size) {
        return fileStorageService.of(source, name, contentType, size);
    }

    public static UploadPretreatment of(Object source, String name, String contentType, Long size, FileStorage fileStorage) {
        return fileStorageService.of(source, name, contentType, size)
                .setPlatform(fileStorage.getPlatform())
                .setFileStorageService(new FileStorageServiceProxy(fileStorageService, fileStorage));
    }

    public static UploadPretreatment of(Object source, String name, String contentType, Long size, StorageContext storageContext) {
        return of(source, name, contentType, size, createFileStorage(storageContext));
    }

    /**
     * 根据存储上下文创建文件存储对象
     */
    public static FileStorage createFileStorage(StorageContext context) {
        StorageType type = context.getType();
        if (ObjectUtil.isNull(type)) {
            throw new BizRuntimeException("请指定存储平台类型");
        }
        //平台名称必填
        Assert.notBlank(context.getPlatform(), () -> new BizRuntimeException("platform必须指定且唯一"));
        //基础存储路径必填
        Assert.notBlank(context.getBasePath(), () -> new BizRuntimeException("basePath必须指定"));
        //格式化字符
        context.setDomain(formatDomain(context.getDomain()));
        context.setStoragePath(formatStoragePath(context.getStoragePath()));
        context.setBasePath(formatBasePath(context.getBasePath()));
        //如果domain不是空的则需要判断他是否是合法domain
        if (StrUtil.isNotBlank(context.getDomain())) {
            Assert.isTrue(ResourceUtils.isUrl(context.getDomain()), () -> new BizRuntimeException("domain不符合规则"));
        }
        //本地模式
        if (type == LOCAL) {
            FileStorageProperties.LocalPlusConfig config = new FileStorageProperties.LocalPlusConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildLocalPlusFileStorage(Collections.singletonList(config)).get(0);
        }
        //不是本地模式则需要判断其它必填参数
        Assert.notBlank(context.getAccessKey(), () -> new BizRuntimeException("accessKey必须指定"));
        Assert.notBlank(context.getSecretKey(), () -> new BizRuntimeException("secretKey必须指定"));
        Assert.notBlank(context.getBucketName(), () -> new BizRuntimeException("bucketName必须指定"));
        //华为
        if (type == StorageType.HUAWEI_OBS) {
            FileStorageProperties.HuaweiObsConfig config = new FileStorageProperties.HuaweiObsConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildHuaweiObsFileStorage(Collections.singletonList(config), null).get(0);
        }
        //阿里云
        if (type == StorageType.ALIYUN_OSS) {
            FileStorageProperties.AliyunOssConfig config = new FileStorageProperties.AliyunOssConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildAliyunOssFileStorage(Collections.singletonList(config), null).get(0);
        }
        //七牛
        if (type == StorageType.QINIU_KODO) {
            FileStorageProperties.QiniuKodoConfig config = new FileStorageProperties.QiniuKodoConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildQiniuKodoFileStorage(Collections.singletonList(config), null).get(0);
        }
        //腾讯
        if (type == StorageType.TENCENT_COS) {
            FileStorageProperties.TencentCosConfig config = new FileStorageProperties.TencentCosConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildTencentCosFileStorage(Collections.singletonList(config), null).get(0);
        }
        //百度
        if (type == StorageType.BAIDU_BOS) {
            FileStorageProperties.BaiduBosConfig config = new FileStorageProperties.BaiduBosConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildBaiduBosFileStorage(Collections.singletonList(config), null).get(0);
        }
        //MINIO
        if (type == StorageType.MINIO) {
            FileStorageProperties.MinioConfig config = new FileStorageProperties.MinioConfig();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildMinioFileStorage(Collections.singletonList(config), null).get(0);
        }
        //amazon
        if (type == StorageType.AMAZON_S3) {
            FileStorageProperties.AmazonS3Config config = new FileStorageProperties.AmazonS3Config();
            BeanUtil.copyProperties(context, config);
            return FileStorageServiceBuilder.buildAmazonS3FileStorage(Collections.singletonList(config), null).get(0);
        }

        throw new BizRuntimeException("存储平台类型不支持");
    }

    /**
     * 格式化基础路径
     */
    public static String formatBasePath(String basePath) {
        //去除前面前缀/,补齐后面尾缀/
        if (StrUtil.isBlank(basePath)) {
            return StrUtil.EMPTY;
        }
        if (basePath.startsWith(StrUtil.SLASH)) {
            basePath = basePath.substring(1);
        }
        if (!basePath.endsWith(StrUtil.SLASH)) {
            basePath = basePath + StrUtil.SLASH;
        }
        return basePath;
    }

    /**
     * 格式化domain
     */
    public static String formatDomain(String domain) {
        if (StrUtil.isBlank(domain)) {
            return domain;
        }
        if (!domain.endsWith(StrUtil.SLASH)) {
            return domain + StrUtil.SLASH;
        }
        return domain;
    }

    /**
     * 格式化存储路径
     */
    public static String formatStoragePath(String storagePath) {
        if (StrUtil.isBlank(storagePath)) {
            return StrUtil.SLASH;
        }
        //相对目录默认去除.符号
        if (storagePath.startsWith("./")) {
            storagePath = storagePath.substring(2);
        }
        if (!storagePath.endsWith("/")) {
            storagePath = storagePath + "/";
        }
        return storagePath;
    }
}
