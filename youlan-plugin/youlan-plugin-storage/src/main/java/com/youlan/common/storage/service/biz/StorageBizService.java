package com.youlan.common.storage.service.biz;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.storage.config.StorageProperties;
import com.youlan.common.storage.entity.StorageConfig;
import com.youlan.common.storage.entity.StorageRecord;
import com.youlan.common.storage.enums.StorageAclType;
import com.youlan.common.storage.enums.StorageType;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.common.storage.service.StorageConfigService;
import com.youlan.common.storage.service.StorageRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.UploadPretreatment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class StorageBizService {
    private final StorageRecordService storageRecordService;
    private final StorageConfigService storageConfigService;
    private final StorageProperties storageProperties;

    /**
     * 上传文件
     *
     * @param platform             存储平台名称
     * @param source               文件源
     * @param pretreatmentConsumer 上传预处理器(可自定义预处理器配置)
     * @return 存储记录
     */
    public StorageRecord upload(String platform, Object source, Consumer<UploadPretreatment> pretreatmentConsumer) {
        return upload(platform, source, null, null, null, pretreatmentConsumer);

    }

    /**
     * 上传文件
     *
     * @param platform             存储平台名称
     * @param source               文件源
     * @param name                 文件名称
     * @param pretreatmentConsumer 上传预处理器(可自定义预处理器配置)
     * @return 存储记录
     */
    public StorageRecord upload(String platform, Object source, String name, Consumer<UploadPretreatment> pretreatmentConsumer) {
        return upload(platform, source, name, null, null, pretreatmentConsumer);
    }

    /**
     * 上传文件
     *
     * @param platform             存储平台名称
     * @param source               文件源
     * @param name                 文件名称
     * @param contentType          文件MIME类型
     * @param pretreatmentConsumer 上传预处理器(可自定义预处理器配置)
     * @return 存储记录
     */
    public StorageRecord upload(String platform, Object source, String name, String contentType, Consumer<UploadPretreatment> pretreatmentConsumer) {
        return upload(platform, source, name, contentType, null, pretreatmentConsumer);
    }

    /**
     * 上传文件
     *
     * @param platform             存储平台名称
     * @param source               文件源
     * @param name                 文件名称
     * @param contentType          文件MIME类型
     * @param size                 文件大小
     * @param pretreatmentConsumer 上传预处理器(可自定义预处理器配置)
     * @return 存储记录
     */
    public StorageRecord upload(String platform, Object source, String name, String contentType, Long size, Consumer<UploadPretreatment> pretreatmentConsumer) {
        // 创建上传预处理器
        UploadPretreatment uploadPretreatment = StorageHelper.of(platform, source, name, contentType, size);
        // 外部自定义设置上传预处理器
        if (ObjectUtil.isNotNull(pretreatmentConsumer)) {
            pretreatmentConsumer.accept(uploadPretreatment);
        }
        try {
            StorageConfig storageConfig = storageConfigService.loadOrDefaultStorageConfigCache(platform);
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
            StorageRecord storageRecord = StorageHelper.fileInfoToStorageRecord(fileInfo);
            //如果开启存储记录则需要保存
            if (storageProperties.getRecordEnabled()) {
                storageRecordService.save(storageRecord);
            }
            //拼接完整路径
            return StorageHelper.processStroageUrl(storageRecord);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(ApiResultCode.B0007);
        }
    }

    /**
     * 通过url下载文件
     */
    public StorageRecord downloadByUrl(String url) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByUrl(url);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setDownloader(StorageHelper.download(fileInfo));
    }

    /**
     * 根据url下载缩略文件
     */
    public StorageRecord downloadThByUrl(String url) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByUrl(url);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setThDownloader(StorageHelper.downloadTh(fileInfo));
    }

    /**
     * 根据文件名称下载文件
     */
    public StorageRecord downloadByFileName(String fileName) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByFileName(fileName);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setDownloader(StorageHelper.download(fileInfo));
    }

    /**
     * 根据文件名称下载缩略文件
     */
    public StorageRecord downloadThByFileName(String fileName) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByFileName(fileName);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setThDownloader(StorageHelper.downloadTh(fileInfo));
    }

    /**
     * 根据存储对象ID下载文件
     */
    public StorageRecord downloadByObjectId(String objectId) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByObjectId(objectId);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setDownloader(StorageHelper.download(fileInfo));
    }

    /**
     * 根据存储对象ID下载缩略文件
     */
    public StorageRecord downloadThByObjectId(String objectId) {
        StorageRecord storageRecord = storageRecordService.loadStorageRecordCacheByObjectId(objectId);
        FileInfo fileInfo = StorageHelper.storageRecordToFileInfo(storageRecord);
        return storageRecord.setThDownloader(StorageHelper.downloadTh(fileInfo));
    }
}
