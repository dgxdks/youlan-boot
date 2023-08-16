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
import org.springframework.util.ResourceUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class StorageHelper {
    private static final FileStorageService FILE_STORAGE_SERVICE = SpringUtil.getBean(FileStorageService.class);
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of() {
        return getFileStorageService().of();
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(String platform, Supplier<StorageContext> supplier) {
        getOrAddFileStorage(platform, supplier);
        return of().setPlatform(platform);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source) {
        return getFileStorageService().of(source);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String platform, Supplier<StorageContext> supplier) {
        getOrAddFileStorage(platform, supplier);
        return of(source).setPlatform(platform);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name) {
        return getFileStorageService().of(source, name);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name, String platform, Supplier<StorageContext> supplier) {
        getOrAddFileStorage(platform, supplier);
        return of(source, name).setPlatform(platform);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name, String contentType) {
        return getFileStorageService().of(source, name, contentType);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name, String contentType, String platform, Supplier<StorageContext> supplier) {
        getOrAddFileStorage(platform, supplier);
        return of(source, name, contentType).setPlatform(platform);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name, String contentType, Long size) {
        return getFileStorageService().of(source, name, contentType, size);
    }

    /**
     * 创建上传预处理器
     */
    public static UploadPretreatment of(Object source, String name, String contentType, Long size, String platform, Supplier<StorageContext> supplier) {
        getOrAddFileStorage(platform, supplier);
        return getFileStorageService().of(source, name, contentType, size).setPlatform(platform);
    }

    /**
     * 删除文件存储
     */
    public static void removeFileStorage(String platform) {
        FileStorage fileStorage = getFileStorage(platform);
        if (ObjectUtil.isNotNull(fileStorage)) {
            removeFileStorage(fileStorage);
        }
    }

    public static void removeFileStorage(FileStorage fileStorage) {
        getFileStorageService().getFileStorageList().remove(fileStorage);
    }

    /**
     * 新增文件存储
     */
    public static void addFileStorage(FileStorage fileStorage) {
        getFileStorageService().getFileStorageList().add(fileStorage);
    }

    /**
     * 新增文件存储
     */
    public static void addFileStorage(List<FileStorage> fileStorageList) {
        getFileStorageService().getFileStorageList().addAll(fileStorageList);
    }

    /**
     * 根据平台名称获取文件存储
     */
    public static FileStorage getFileStorage(String platform) {
        return getFileStorageService().getFileStorage(platform);
    }

    /**
     * 获取默认文件存储
     */
    public static FileStorage getFileStorage() {
        return getFileStorageService().getFileStorage();
    }

    /**
     * 获取或创建文件存储
     */
    public static FileStorage getOrAddFileStorage(String platform, Supplier<StorageContext> supplier) {
        //多线程下避免重复创建
        try {
            LOCK.lock();
            FileStorage fileStorage = getFileStorage(platform);
            if (ObjectUtil.isNotNull(fileStorage)) {
                return fileStorage;
            }
            fileStorage = createFileStorage(supplier.get());
            addFileStorage(fileStorage);
            return fileStorage;
        } finally {
            LOCK.unlock();
        }
    }

    public static FileStorage createFileStorage(StorageContext context) {
        StorageType type = context.getType();
        if (ObjectUtil.isNull(type)) {
            throw new BizRuntimeException("请指定存储平台类型");
        }
        //平台名称必填
        Assert.notBlank(context.getPlatform(), () -> new BizRuntimeException("platform必须指定且唯一"));
        //基础存储路径必填
        Assert.notBlank(context.getBasePath(), () -> new BizRuntimeException("basePath必须指定"));
        //存储路径需要格式化校验,没有添加/后缀则需要补齐
        if (StrUtil.isNotBlank(context.getStoragePath()) && !context.getStoragePath().endsWith("/")) {
            context.setStoragePath(context.getStoragePath() + "/");
        }
        //如果domain不是空的则需要判断他是否是合法domain
        if (StrUtil.isNotBlank(context.getDomain())) {
            Assert.isTrue(ResourceUtils.isUrl(context.getDomain()), () -> new BizRuntimeException("domain不符合规则"));
        }
        //本地模式
        if (type == StorageType.LOCAL) {
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

    public static FileStorageService getFileStorageService() {
        return FILE_STORAGE_SERVICE;
    }
}
