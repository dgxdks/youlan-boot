package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.storage.entity.StorageContext;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.system.config.SystemProperties;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.service.StorageRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
@AllArgsConstructor
public class StorageBizService {
    private final StorageConfigBizService storageConfigBizService;
    private final StorageRecordService storageRecordService;
    private final SystemProperties systemProperties;

    /**
     * 上传文件
     */
    public StorageRecord upload(MultipartFile file, String platform) {
        try {
            StorageConfig storageConfig;
            if (StrUtil.isNotBlank(platform)) {
                storageConfig = storageConfigBizService.getStorageConfigIfExist(platform);
            } else {
                storageConfig = storageConfigBizService.getDefaultStorageConfigIfExist();
            }
            FileInfo fileInfo = StorageHelper.of(file, storageConfig.getPlatform(), () -> createStorageContext(storageConfig))
                    .upload();
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
                    .setObjectId(fileInfo.getObjectId())
                    .setObjectType(fileInfo.getObjectType())
                    .setAttr(StrUtil.toStringOrNull(fileInfo.getAttr()))
                    .setFileAcl(StrUtil.toStringOrNull(fileInfo.getFileAcl()))
                    .setThFileAcl(StrUtil.toStringOrNull(fileInfo.getThFileAcl()));
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
    public File downloadUrl(String url) {
        try {
            //如果能生成URL则说明是带协议的url直接返回
            new URL(url);
            throw new BizRuntimeException(ApiResultCode.B0008);
        } catch (MalformedURLException ignore) {
        }
        String filePath = systemProperties.getStorage().getStoragePath() + url;
        boolean isFile = FileUtil.isFile(filePath);
        if (!isFile) {
            throw new BizRuntimeException(ApiResultCode.B0009);
        }
        return FileUtil.file(filePath);
    }

    /**
     * 创建存储上下文
     */
    public StorageContext createStorageContext(StorageConfig storageConfig) {
        StorageContext storageContext = BeanUtil.copyProperties(storageConfig, StorageContext.class);
        String storagePath = systemProperties.getStorage().getStoragePath();
        return storageContext.setStoragePath(storagePath);
    }
}
