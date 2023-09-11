package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.enums.DBStatus;
import com.youlan.common.db.enums.DBYesNo;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.entity.StorageContext;
import com.youlan.common.storage.enums.StorageAclType;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.system.config.SystemProperties;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.service.StorageConfigService;
import com.youlan.system.service.StorageRecordService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.youlan.common.db.constant.DBConstant.VAL_YES;
import static com.youlan.common.storage.enums.StorageType.LOCAL;
import static com.youlan.common.storage.enums.StorageType.MINIO;

@Slf4j
@Service
@AllArgsConstructor
public class StorageBizService {
    private final StorageRecordService storageRecordService;
    private final StorageConfigService storageConfigService;
    private final SystemProperties systemProperties;
    private final RedisHelper redisHelper;

    /**
     * 上传文件
     */
    public StorageRecord upload(MultipartFile file, String platform) {
        return upload(file, platform, uploadPretreatment -> {
        });
    }

    /**
     * 上传文件
     */
    public StorageRecord upload(MultipartFile file, String platform, Consumer<UploadPretreatment> pretreatmentConsumer) {
        StorageConfig storageConfig = loadStorageConfigCache(platform);
        UploadPretreatment uploadPretreatment = StorageHelper.of(file, createStorageContext(storageConfig));
        pretreatmentConsumer.accept(uploadPretreatment);
        try {
            //本地存储模式下存储的文件如果较多且都放在同一个目录下则会出现目录”打不开删不掉“的风险，所以在生成存储目录时增加分层目录
            //生成规则按照 年/月/日/时，如果一个小时下还会产生大量文件还是建议走对象存储
            if (StorageType.LOCAL.getCode().equals(uploadPretreatment.getPlatform())) {
                String path = DateUtil.format(new Date(), "yyyy/MM/dd/HH/");
                uploadPretreatment.setPath(path);
            }
            // 如果未设置访问控制ACL则需要判断存储配置中是否设置的访问控制ACL
            Object fileAcl = uploadPretreatment.getFileAcl();
            if (ObjectUtil.isNull(fileAcl) && StorageAclType.isNotDefault(storageConfig.getFileAcl())) {
                uploadPretreatment.setFileAcl(storageConfig.getFileAcl());
            }
            FileInfo fileInfo = uploadPretreatment.upload();
            StorageRecord storageRecord = storageRecordService.fileInfoToStorageRecord(fileInfo);
            //如果开启存储记录则需要保存
            if (systemProperties.getStorage().getRecordEnabled()) {
                storageRecordService.save(storageRecord);
            }
            //拼接完整路径
            setStorageRecordFullUlr(storageRecord);
            return storageRecord;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0007);
        }
    }

    /**
     * 向存储记录中设置完成文件访问地址
     */
    public void setStorageRecordFullUlr(StorageRecord storageRecord) {
        if (ObjectUtil.isNull(storageRecord) || StrUtil.isBlank(storageRecord.getPlatform())) {
            return;
        }
        StorageConfig storageConfig = loadStorageConfigCacheByPlatform(storageRecord.getPlatform());
        if (ObjectUtil.isNull(storageConfig)) {
            return;
        }
        String url = storageRecord.getUrl();
        String thUrl = storageRecord.getThUrl();
        StorageType storageType = storageConfig.getType();
        String endPoint = storageConfig.getEndPoint();
        String bucketName = storageConfig.getBucketName();
        String isHttps = storageConfig.getIsHttps();
        storageRecord.setFullUrl(getStorageFullUrl(storageType, endPoint, bucketName, isHttps, url));
        storageRecord.setThFullUrl(getStorageFullUrl(storageType, endPoint, bucketName, isHttps, thUrl));
    }

    /**
     * 生成OSS完整文件地址
     */
    public String getStorageFullUrl(StorageType storageType, String endPoint, String bucketName, String isHttps, String url) {
        // url是空的直接返回
        if (StrUtil.isBlank(url)) {
            return url;
        }
        // 本地存储直接返回
        if (storageType == LOCAL) {
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
     * 通过url下载本地文件
     */
    public File downloadByUrl(String url) {
        if (StrUtil.isBlank(url)) {
            throw new BizRuntimeException(ApiResultCode.B0008);
        }
        //不允许目录上跳
        if (url.contains("..")) {
            throw new BizRuntimeException(ApiResultCode.B0012);
        }
        String filePath = systemProperties.getStorage().getStoragePath() + url;
        boolean isFile = FileUtil.isFile(filePath);
        if (!isFile) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        return FileUtil.file(filePath);
    }

    /**
     * 根据文件名称获取文件
     */
    public StorageRecord downloadByFileName(String fileName) {
        StorageRecord storageRecord = loadStorageRecordCacheByFileName(fileName);
        return downloadByRecord(storageRecord);
    }

    /**
     * 根据存储对象ID获取文件
     */
    public StorageRecord downloadByObjectId(String objectId) {
        StorageRecord storageRecord = loadStorageRecordCacheByObjectId(objectId);
        return downloadByRecord(storageRecord);
    }

    /**
     * 根据存储记录获取文件
     */
    public StorageRecord downloadByRecord(StorageRecord storageRecord) {
        String platform = storageRecord.getPlatform();
        try (FileStorage fileStorage = StorageHelper.createFileStorage(createStorageContext(loadStorageConfigCache(platform)))) {
            ByteArrayOutputStream cacheBos = new ByteArrayOutputStream(1024);
            fileStorage.download(storageRecordService.storageRecordToFileInfo(storageRecord), inputStream -> IoUtil.copy(inputStream, cacheBos));
            storageRecord.setData(cacheBos.toByteArray());
            return storageRecord;
        }
    }

    /**
     * 根据文件名称获取存储记录(支持缓存)
     */
    public StorageRecord loadStorageRecordCacheByFileName(String fileName) {
        String fileNameRedisKey = SystemUtil.getStorageFileNameRedisKey(fileName);
        StorageRecord storageRecord = redisHelper.get(fileNameRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = storageRecordService.loadStorageRecordCacheByFileName(fileName);
        long recordCacheTimeout = systemProperties.getStorage().getRecordCacheTimeout();
        redisHelper.set(fileNameRedisKey, storageRecord, recordCacheTimeout, TimeUnit.SECONDS);
        return storageRecord;
    }

    /**
     * 根据对象ID获取存储记录(支持缓存)
     */
    public StorageRecord loadStorageRecordCacheByObjectId(String objectId) {
        String objectIdRedisKey = SystemUtil.getStorageObjectIdRedisKey(objectId);
        StorageRecord storageRecord = redisHelper.get(objectIdRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = storageRecordService.loadStorageRecordCacheByObjectId(objectId);
        long recordCacheTimeout = systemProperties.getStorage().getRecordCacheTimeout();
        redisHelper.set(objectIdRedisKey, storageRecord, recordCacheTimeout, TimeUnit.SECONDS);
        return storageRecord;
    }

    /**
     * 创建存储上下文
     */
    public StorageContext createStorageContext(StorageConfig storageConfig) {
        StorageContext storageContext = BeanUtil.copyProperties(storageConfig, StorageContext.class);
        String storagePath = systemProperties.getStorage().getStoragePath();
        return storageContext.setStoragePath(storagePath);
    }

    /**
     * 根据平台名称获取对应存储配置
     */
    public StorageConfig loadStorageConfigCache(String platform) {
        StorageConfig storageConfig;
        //未指定平台则取默认存储配置
        if (StrUtil.isNotBlank(platform)) {
            storageConfig = loadStorageConfigCacheByPlatform(platform);
        } else {
            storageConfig = loadDefaultStorageConfigCache();
        }
        if (ObjectUtil.isNull(storageConfig)) {
            throw new BizRuntimeException(StrUtil.format("无法找到[{}]对应的存储配置", platform));
        }
        if (DBStatus.isDisabled(storageConfig.getStatus())) {
            throw new BizRuntimeException("存储配置已被禁用");
        }
        return storageConfig;
    }

    /**
     * 获取默认存储配置缓存
     */
    public StorageConfig loadDefaultStorageConfigCache() {
        StorageConfig storageConfig = redisHelper.get(SystemUtil.getDefaultStorageConfigRedisKey());
        if (ObjectUtil.isNotNull(storageConfig)) {
            return storageConfig;
        }
        storageConfig = storageConfigService.loadDefaultStorageConfig();
        if (ObjectUtil.isNotNull(storageConfig)) {
            redisHelper.set(SystemUtil.getDefaultStorageConfigRedisKey(), storageConfig);
        }
        return storageConfig;
    }

    /**
     * 重新加载存储配置缓存
     */
    public void reloadDefaultStorageConfigCache() {
        StorageConfig storageConfig = storageConfigService.loadDefaultStorageConfig();
        redisHelper.set(SystemUtil.getDefaultStorageConfigRedisKey(), storageConfig);
    }

    /**
     * 根据平台名称获取对应存储配置缓存
     */
    public StorageConfig loadStorageConfigCacheByPlatform(String platform) {
        StorageConfig storageConfig = redisHelper.get(SystemUtil.getStorageConfigRedisKey(platform));
        if (ObjectUtil.isNotNull(storageConfig)) {
            return storageConfig;
        }
        storageConfig = storageConfigService.loadStorageConfigByPlatform(platform);
        if (ObjectUtil.isNotNull(storageConfig)) {
            redisHelper.set(SystemUtil.getStorageConfigRedisKey(platform), storageConfig);
        }
        return storageConfig;
    }

    /**
     * 重新根据平台名称获取对应存储配置缓存
     */
    public void reloadStorageConfigCacheByPlatform(String platform) {
        StorageConfig storageConfig = storageConfigService.loadStorageConfigByPlatform(platform);
        redisHelper.set(SystemUtil.getStorageConfigRedisKey(platform), storageConfig);
    }

    /**
     * 新增存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStorageConfig(StorageConfig storageConfig) {
        beforeOrUpdateStorageConfig(storageConfig);
        //生成platform
        storageConfig.setPlatform(SystemUtil.getStoragePlatform(storageConfig.getType()));
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        boolean isDefault = VAL_YES.equals(storageConfig.getIsDefault());
        if (isDefault) {
            storageConfigService.resetDefaultStorageConfig();
        }
        boolean save = storageConfigService.save(storageConfig);
        if (!save) {
            throw new BizRuntimeException("新增存储配置失败");
        }
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 修改存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfig(StorageConfig storageConfig) {
        String platform = storageConfig.getPlatform();
        beforeOrUpdateStorageConfig(storageConfig);
        // 平台名称不能更改
        storageConfig.setPlatform(null);
        //如果当前配置是默认存储配置则需要先重置库中默认存储配置然后再保存
        boolean isDefault = VAL_YES.equals(storageConfig.getIsDefault());
        if (isDefault) {
            storageConfigService.resetDefaultStorageConfig();
        }
        boolean update = storageConfigService.updateById(storageConfig);
        if (!update) {
            throw new BizRuntimeException("更新存储配置失败");
        }
        this.reloadStorageConfigCacheByPlatform(platform);
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 删除存储配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeStorageConfigs(List<Long> ids) {
        List<StorageConfig> storageConfigs = storageConfigService.listByIds(ids);
        //如果包含默认存储配置则不允许删除
        List<StorageConfig> defaultStorageConfigs = ListHelper.filterList(storageConfigs, storageConfig -> VAL_YES.equals(storageConfig.getIsDefault()));
        if (CollectionUtil.isNotEmpty(defaultStorageConfigs)) {
            throw new BizRuntimeException("存储配置设置为默认时不允许删除");
        }
        //存储配置被使用时不允许被删除
        storageConfigs.forEach(storageConfig -> {
            boolean storageRecordExists = storageRecordService.lambdaQuery()
                    .eq(StorageRecord::getPlatform, storageConfig.getPlatform())
                    .exists();
            if (storageRecordExists) {
                throw new BizRuntimeException("存储配置被使用是不允许删除");
            }
        });
        storageConfigService.removeByIds(ids);
        //删除配置缓存
        storageConfigs.forEach(storageConfig -> {
            try {
                redisHelper.delete(SystemUtil.getStorageConfigRedisKey(storageConfig.getPlatform()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 新增或修改前要执行的操作
     */
    public void beforeOrUpdateStorageConfig(StorageConfig storageConfig) {
        storageConfig.setDomain(StorageHelper.formatDomain(storageConfig.getDomain()));
        storageConfig.setBasePath(StorageHelper.formatBasePath(storageConfig.getBasePath()));
    }

    /**
     * 存储配置状态修改
     */
    public void updateStorageConfigStatus(Long id, String status) {
        StorageConfig storageConfig = storageConfigService.loadStorageConfigIfExists(id);
        StorageConfig updateStorageConfig = new StorageConfig();
        updateStorageConfig.setId(id);
        updateStorageConfig.setStatus(status);
        boolean update = storageConfigService.updateById(updateStorageConfig);
        if (!update) {
            throw new BizRuntimeException("存储配置状态修改失败");
        }
        this.reloadStorageConfigCacheByPlatform(storageConfig.getPlatform());
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 存储配置是否默认修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfigIsDefault(Long id, String isDefault) {
        StorageConfig storageConfig = storageConfigService.loadStorageConfigIfExists(id);
        storageConfigService.resetDefaultStorageConfig();
        storageConfig.setIsDefault(isDefault);
        boolean update = storageConfigService.updateById(storageConfig);
        if (!update) {
            throw new BizRuntimeException("存储配置是否默认修改失败");
        }
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 存储配置缓存刷新
     */
    public void refreshStorageConfigCache() {
        String defaultStorageConfigRedisKey = SystemUtil.getDefaultStorageConfigRedisKey();
        String storageConfigRedisKeyPattern = SystemUtil.getStorageConfigRedisKey(StringPool.ASTERISK);
        redisHelper.deleteByPattern(storageConfigRedisKeyPattern);
        redisHelper.delete(defaultStorageConfigRedisKey);
        this.reloadDefaultStorageConfigCache();
    }

    /**
     * 存储记录详情
     */
    public StorageRecord loadStorageRecord(Long id) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordIfExists(id);
        setStorageRecordFullUlr(storageRecord);
        return storageRecord;
    }

    /**
     * 存储记录分页
     */
    public IPage<StorageRecord> getStorageRecordPageList(StorageRecord storageRecord) {
        List<String> sortColumns = List.of("create_time");
        IPage<StorageRecord> pageResult = storageRecordService.loadPage(storageRecord, sortColumns, DBHelper.getQueryWrapper(storageRecord));
        pageResult.getRecords().forEach(this::setStorageRecordFullUlr);
        return pageResult;
    }
}
