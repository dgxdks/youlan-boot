package com.youlan.common.storage.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.enums.DBYesNo;
import com.youlan.common.storage.config.StorageProperties;
import com.youlan.common.storage.entity.StorageConfig;
import com.youlan.common.storage.entity.StorageRecord;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.service.StorageConfigService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.*;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.springframework.util.ResourceUtils;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.youlan.common.core.restful.enums.ApiResultCode.B0008;
import static com.youlan.common.storage.enums.StorageType.LOCAL;
import static com.youlan.common.storage.enums.StorageType.MINIO;

@Slf4j
public class StorageHelper {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final ConcurrentHashMap<String, StorageConfig> STORAGE_CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final StorageProperties storageProperties = SpringUtil.getBean(StorageProperties.class);
    private static final StorageConfigService storageConfigService = SpringUtil.getBean(StorageConfigService.class);
    private static final FileStorageService fileStorageService = SpringUtil.getBean(FileStorageService.class);

    /**
     * 设置完成文件地址
     *
     * @param storageRecord 存储记录
     */
    public static StorageRecord processStroageUrl(StorageRecord storageRecord) {
        if (ObjectUtil.isNull(storageRecord) || StrUtil.isBlank(storageRecord.getPlatform())) {
            return storageRecord;
        }
        StorageConfig storageConfig = storageConfigService.loadStorageConfig(storageRecord.getPlatform());
        if (ObjectUtil.isNull(storageConfig)) {
            return storageRecord;
        }
        String url = storageRecord.getUrl();
        String thUrl = storageRecord.getThUrl();
        StorageType storageType = storageConfig.getType();
        String endPoint = storageConfig.getEndPoint();
        String bucketName = storageConfig.getBucketName();
        String isHttps = storageConfig.getIsHttps();
        storageRecord.setUrl(getStorageUrl(storageType, endPoint, bucketName, isHttps, url));
        storageRecord.setThUrl(getStorageUrl(storageType, endPoint, bucketName, isHttps, thUrl));
        return storageRecord;
    }

    /**
     * 获取完整文件地址
     *
     * @param storageType 存储类型
     * @param endPoint    端点
     * @param bucketName  桶名称
     * @param isHttps     是否是HTTPS
     * @param url         文件地址
     * @return 完成文件地址
     */
    public static String getStorageUrl(StorageType storageType, String endPoint, String bucketName, String isHttps, String url) {
        // url是空的直接返回
        if (StrUtil.isBlank(url)) {
            return url;
        }
        // 本地存储直接返回
        if (storageType == LOCAL) {
            return url;
        }
        // 其他类型需要判断url是否包含访问协议前缀的完整访问地址，不包含协议则需要根据存储配置自行拼接访问协议前缀
        if (ResourceUtils.isUrl(url)) {
            return url;
        }
        String protocol = DBYesNo.isYes(isHttps) ? "https://" : "http://";
        String domain = endPoint;
        String bucket = StrUtil.addSuffixIfNot(bucketName, StrPool.DOT);
        if (Validator.isUrl(domain)) {
            domain = URLUtil.url(domain).getHost();
        }
        boolean noBucketName = storageType == MINIO;
        if (noBucketName) {
            bucket = StrUtil.EMPTY;
        }
        return protocol + bucket + domain + StrPool.SLASH + url;
    }

    /**
     * 存储记录转文件信息
     *
     * @param storageRecord 存储记录
     * @return 文件信息
     */
    public static FileInfo storageRecordToFileInfo(StorageRecord storageRecord) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(storageRecord.getUrl());
        fileInfo.setSize(storageRecord.getSize());
        fileInfo.setFilename(storageRecord.getFileName());
        fileInfo.setOriginalFilename(storageRecord.getOriginalFileName());
        fileInfo.setBasePath(storageRecord.getBasePath());
        fileInfo.setPath(storageRecord.getPath());
        fileInfo.setExt(storageRecord.getExt());
        fileInfo.setContentType(storageRecord.getContentType());
        fileInfo.setPlatform(storageRecord.getPlatform());
        fileInfo.setThUrl(storageRecord.getThUrl());
        fileInfo.setThFilename(storageRecord.getThFileName());
        fileInfo.setThSize(storageRecord.getThSize());
        fileInfo.setThContentType(storageRecord.getThContentType());
        fileInfo.setObjectId(storageRecord.getObjectId());
        fileInfo.setObjectType(storageRecord.getObjectType());
        if (StrUtil.isNotBlank(storageRecord.getAttr())) {
            fileInfo.setAttr(JSONUtil.toBean(storageRecord.getAttr(), Dict.class));
        }
        if (StrUtil.isNotBlank(storageRecord.getFileAcl())) {
            fileInfo.setFileAcl(JSONUtil.parse(storageRecord.getFileAcl()));
        }
        if (StrUtil.isNotBlank(storageRecord.getThFileAcl())) {
            fileInfo.setThFileAcl(JSONUtil.parse(storageRecord.getThFileAcl()));
        }
        return fileInfo;
    }

    /**
     * 文件信息转存储记录
     *
     * @param fileInfo 文件信息
     * @return 存储记录
     */
    public static StorageRecord fileInfoToStorageRecord(FileInfo fileInfo) {
        //objectId为空时使用fileName代替
        StorageRecord storageRecord = new StorageRecord()
                .setUrl(fileInfo.getUrl())
                .setSize(fileInfo.getSize())
                .setFileName(fileInfo.getFilename())
                .setOriginalFileName(fileInfo.getOriginalFilename())
                .setBasePath(fileInfo.getBasePath())
                .setPath(fileInfo.getPath())
                .setExt(fileInfo.getExt())
                .setContentType(fileInfo.getContentType())
                .setPlatform(fileInfo.getPlatform())
                .setThUrl(fileInfo.getThUrl())
                .setThFileName(fileInfo.getThFilename())
                .setThSize(fileInfo.getThSize())
                .setThContentType(fileInfo.getThContentType())
                .setObjectId(StrUtil.blankToDefault(fileInfo.getObjectId(), fileInfo.getFilename()))
                .setObjectType(fileInfo.getObjectType());
        if (ObjectUtil.isNotNull(fileInfo.getAttr())) {
            storageRecord.setAttr(JSONUtil.toJsonStr(fileInfo.getAttr()));
        }
        if (ObjectUtil.isNotNull(fileInfo.getFileAcl())) {
            storageRecord.setFileAcl(JSONUtil.toJsonStr(fileInfo.getFileAcl()));
        }
        if (ObjectUtil.isNotNull(fileInfo.getThFileAcl())) {
            storageRecord.setThFileAcl(JSONUtil.toJsonStr(fileInfo.getThFileAcl()));
        }
        return storageRecord;
    }

    /**
     * 创建上传预处理对象
     *
     * @param storageConfig 存储配置
     * @param source        文件源
     * @param name          文件名称
     * @param contentType   文件MIME类型
     * @param size          文件大小
     * @return 上传预处理器
     */
    public static UploadPretreatment of(StorageConfig storageConfig, Object source, String name, String contentType, Long size) {
        Assert.notNull(storageConfig, B0008::getException);
        addOrUpdateFileStorage(storageConfig);
        return fileStorageService.of(source, name, contentType, size)
                .setPlatform(storageConfig.getPlatform());
    }

    /**
     * 创建上传预处理对象
     *
     * @param platform    存储平台
     * @param source      文件源
     * @param name        文件名称
     * @param contentType 文件MIME类型
     * @param size        文件大小
     * @return 上传预处理器
     */
    public static UploadPretreatment of(String platform, Object source, String name, String contentType, Long size) {
        StorageConfig storageConfig = StrUtil.isBlank(platform) ? storageConfigService.loadDefaultStorageConfigCache() :
                storageConfigService.loadStorageConfigCache(platform);
        return of(storageConfig, source, name, contentType, size);
    }

    /**
     * 创建下载器
     *
     * @param fileInfo 文件信息
     * @return 下载器
     */
    public static Downloader download(FileInfo fileInfo) {
        String platform = fileInfo.getPlatform();
        Assert.notNull(platform, B0008::getException);
        StorageConfig storageConfig = storageConfigService.loadStorageConfigCache(platform);
        addOrUpdateFileStorage(storageConfig);
        return fileStorageService.download(fileInfo);
    }

    /**
     * 创建缩略下载器
     *
     * @param fileInfo 文件信息
     * @return 下载器
     */
    public static Downloader downloadTh(FileInfo fileInfo) {
        String platform = fileInfo.getPlatform();
        Assert.notNull(platform, B0008::getException);
        StorageConfig storageConfig = storageConfigService.loadStorageConfigCache(platform);
        addOrUpdateFileStorage(storageConfig);
        return fileStorageService.downloadTh(fileInfo);
    }

    /**
     * 新增或更新存储对象
     *
     * @param newStorageConfig 新的存储配置
     */
    public static void addOrUpdateFileStorage(StorageConfig newStorageConfig) {
        // 不存在存储配置缓存则说明此存储配置对应的FileStorage对象并未创建，需要在线程安全的方法下创建FileStorage对象
        StorageConfig oldStorageConfig = STORAGE_CONFIG_CACHE.computeIfAbsent(newStorageConfig.getPlatform(), key -> {
            FileStorage fileStorage = createFileStorage(newStorageConfig);
            fileStorageService.getFileStorageList().add(fileStorage);
            return newStorageConfig;
        });
        // 如果最新配置和缓存配置不一致则说明配置被更新了需要重新创建FileStorage对象
        if (ObjectUtil.equal(newStorageConfig, oldStorageConfig)) {
            return;
        }
        // 防止并发创建FileStorage对象
        try {
            if (!LOCK.tryLock()) {
                return;
            }
            FileStorage newFileStorage = createFileStorage(newStorageConfig);
            // 先增加新的FileStorage文件存储对象在停止并删除旧的FileStorage对象
            fileStorageService.getFileStorageList().add(newFileStorage);
            // 删除平台名称一致但是又不是最新创建的FileStorage对象
            fileStorageService.getFileStorageList().removeIf(fileStorage -> {
                try {
                    if (ObjectUtil.equal(fileStorage.getPlatform(), newFileStorage.getPlatform()) &&
                            ObjectUtil.notEqual(fileStorage, newFileStorage)) {
                        // 主动关闭存储对象，避免内存泄漏
                        fileStorage.close();
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return true;
                }
            });
            STORAGE_CONFIG_CACHE.put(newStorageConfig.getPlatform(), newStorageConfig);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 根据存储上下文创建文件存储对象
     *
     * @param storageConfig 存储配置
     * @return 文件存储对象
     */
    public static FileStorage createFileStorage(StorageConfig storageConfig) {
        StorageType type = storageConfig.getType();
        if (ObjectUtil.isNull(type)) {
            throw new BizRuntimeException("请指定存储平台类型");
        }
        //平台名称必填
        Assert.notBlank(storageConfig.getPlatform(), () -> new BizRuntimeException("platform必须指定且唯一"));
        //基础存储路径必填
        Assert.notBlank(storageConfig.getBasePath(), () -> new BizRuntimeException("basePath必须指定"));
        //格式化存储配置
        storageConfig.setDomain(formatDomain(storageConfig.getDomain()));
        storageConfig.setBasePath(formatBasePath(storageConfig.getBasePath()));
        //如果domain不是空的则需要判断他是否是合法domain
        if (StrUtil.isNotBlank(storageConfig.getDomain())) {
            Assert.isTrue(ResourceUtils.isUrl(storageConfig.getDomain()), () -> new BizRuntimeException("domain不符合规则"));
        }
        //本地模式
        if (type == LOCAL) {
            FileStorageProperties.LocalPlusConfig config = new FileStorageProperties.LocalPlusConfig();
            BeanUtil.copyProperties(storageConfig, config);
            // 本地存储需要设置存储根目录
            String storagePath = formatStoragePath(storageProperties.getStoragePath());
            config.setStoragePath(storagePath);
            return FileStorageServiceBuilder.buildLocalPlusFileStorage(Collections.singletonList(config)).get(0);
        }
        //不是本地模式则需要判断其它必填参数
        Assert.notBlank(storageConfig.getAccessKey(), () -> new BizRuntimeException("accessKey必须指定"));
        Assert.notBlank(storageConfig.getSecretKey(), () -> new BizRuntimeException("secretKey必须指定"));
        Assert.notBlank(storageConfig.getBucketName(), () -> new BizRuntimeException("bucketName必须指定"));
        //华为
        if (type == StorageType.HUAWEI_OBS) {
            FileStorageProperties.HuaweiObsConfig config = new FileStorageProperties.HuaweiObsConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildHuaweiObsFileStorage(Collections.singletonList(config), null).get(0);
        }
        //阿里云
        if (type == StorageType.ALIYUN_OSS) {
            FileStorageProperties.AliyunOssConfig config = new FileStorageProperties.AliyunOssConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildAliyunOssFileStorage(Collections.singletonList(config), null).get(0);
        }
        //七牛
        if (type == StorageType.QINIU_KODO) {
            FileStorageProperties.QiniuKodoConfig config = new FileStorageProperties.QiniuKodoConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildQiniuKodoFileStorage(Collections.singletonList(config), null).get(0);
        }
        //腾讯
        if (type == StorageType.TENCENT_COS) {
            FileStorageProperties.TencentCosConfig config = new FileStorageProperties.TencentCosConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildTencentCosFileStorage(Collections.singletonList(config), null).get(0);
        }
        //百度
        if (type == StorageType.BAIDU_BOS) {
            FileStorageProperties.BaiduBosConfig config = new FileStorageProperties.BaiduBosConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildBaiduBosFileStorage(Collections.singletonList(config), null).get(0);
        }
        //MINIO
        if (type == StorageType.MINIO) {
            FileStorageProperties.MinioConfig config = new FileStorageProperties.MinioConfig();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildMinioFileStorage(Collections.singletonList(config), null).get(0);
        }
        //amazon
        if (type == StorageType.AMAZON_S3) {
            FileStorageProperties.AmazonS3Config config = new FileStorageProperties.AmazonS3Config();
            BeanUtil.copyProperties(storageConfig, config);
            return FileStorageServiceBuilder.buildAmazonS3FileStorage(Collections.singletonList(config), null).get(0);
        }

        throw new BizRuntimeException("存储平台类型不支持");
    }

    /**
     * 格式化基础路径
     *
     * @param basePath 基础路径
     * @return 格式化结果
     */
    public static String formatBasePath(String basePath) {
        //去除前面前缀/，补齐后面尾缀/
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
     *
     * @param domain 域名
     * @return 格式化结果
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
     *
     * @param storagePath 存储路径
     * @return 格式化结果
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
