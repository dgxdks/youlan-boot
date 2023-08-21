package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.entity.StorageContext;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.system.config.SystemProperties;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.service.StorageRecordService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class StorageBizService {
    private final StorageConfigBizService storageConfigBizService;
    private final StorageRecordService storageRecordService;
    private final SystemProperties systemProperties;
    private final RedisHelper redisHelper;

    /**
     * 上传文件
     */
    public StorageRecord upload(MultipartFile file, String platform) {
        try {
            StorageConfig storageConfig;
            //未指定平台则取默认存储配置
            if (StrUtil.isNotBlank(platform)) {
                storageConfig = storageConfigBizService.getStorageConfigIfExist(platform);
            } else {
                storageConfig = storageConfigBizService.getDefaultStorageConfigIfExist();
            }
            UploadPretreatment uploadPretreatment = StorageHelper.of(file, storageConfig.getPlatform(), () -> createStorageContext(storageConfig));
            //本地存储模式下存储的文件如果较多且都放在同一个目录下则会出现目录”打不开删不掉“的风险，所以在生成存储目录时增加分层目录
            //生成规则按照 年/月/日/时，如果一个小时下还会产生大量文件还是建议走对象存储
            if (StorageType.LOCAL == storageConfig.getType()) {
                String path = DateUtil.format(new Date(), "yyyy/MM/dd/HH/");
                uploadPretreatment.setPath(path);
            }
            FileInfo fileInfo = uploadPretreatment.upload();
            StorageRecord storageRecord = fileInfoToStorageRecord(fileInfo);
            //如果开启存储记录则需要保存
            if (systemProperties.getStorage().getRecordEnabled()) {
                storageRecordService.save(storageRecord);
            }
            return storageRecord;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0007);
        }
    }

    /**
     * 通过url下载本地文件
     */
    public File downloadByUrl(String url) {
        if (StrUtil.isBlank(url)) {
            throw new BizRuntimeException(ApiResultCode.B0008);
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
        StorageRecord storageRecord = getStorageRecordByFileName(fileName);
        return downloadByRecord(storageRecord);
    }

    /**
     * 根据存储对象ID获取文件
     */
    public StorageRecord downloadByObjectId(String objectId) {
        StorageRecord storageRecord = getStorageRecordByObjectId(objectId);
        return downloadByRecord(storageRecord);
    }

    /**
     * 根据存储记录获取文件
     */
    public StorageRecord downloadByRecord(StorageRecord storageRecord) {
        String platform = storageRecord.getPlatform();
        //下载时有可能动态FileStorage是没有被初始化的
        StorageHelper.getFileStorageIfNotExist(platform, () -> {
            StorageConfig storageConfig = storageConfigBizService.getStorageConfigIfExist(platform);
            return createStorageContext(storageConfig);
        });
        FileStorage fileStorage = StorageHelper.getFileStorage(platform);
        ByteArrayOutputStream cacheBos = new ByteArrayOutputStream(1024);
        fileStorage.download(storageRecordToFileInfo(storageRecord), inputStream -> {
            IoUtil.copy(inputStream, cacheBos);
        });
        storageRecord.setData(cacheBos.toByteArray());
        return storageRecord;
    }

    /**
     * 根据文件名称获取存储记录(支持缓存)
     */
    public StorageRecord getStorageRecordByFileName(String fileName) {
        String fileNameRedisKey = SystemUtil.getStorageFileNameRedisKey(fileName);
        StorageRecord storageRecord = redisHelper.get(fileNameRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = storageRecordService.loadOneOpt(StorageRecord::getFileName, fileName)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.B0010));
        long recordCacheTimeout = systemProperties.getStorage().getRecordCacheTimeout();
        redisHelper.set(fileNameRedisKey, storageRecord, recordCacheTimeout, TimeUnit.SECONDS);
        return storageRecord;
    }

    /**
     * 根据对象ID获取存储记录(支持缓存)
     */
    public StorageRecord getStorageRecordByObjectId(String objectId) {
        String objectIdRedisKey = SystemUtil.getStorageObjectIdRedisKey(objectId);
        StorageRecord storageRecord = redisHelper.get(objectIdRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = storageRecordService.loadOneOpt(StorageRecord::getObjectId, objectId)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.B0011));
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
     * 文件信息转存储记录
     */
    public StorageRecord fileInfoToStorageRecord(FileInfo fileInfo) {
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
     * 存储记录转文件信息
     */
    public FileInfo storageRecordToFileInfo(StorageRecord storageRecord) {
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
}
