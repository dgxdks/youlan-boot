package com.youlan.system.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.mapper.StorageRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@AllArgsConstructor
public class StorageRecordService extends BaseServiceImpl<StorageRecordMapper, StorageRecord> {

    public StorageRecord loadStorageRecordIfExists(Serializable id) {
        return this.loadOneOpt(id)
                .orElseThrow(() -> new BizRuntimeException("存储记录不存在"));
    }

    /**
     * 根据文件名称获取存储记录
     */
    public StorageRecord loadStorageRecordCacheByFileName(String fileName) {
        return this.loadOneOpt(StorageRecord::getFileName, fileName)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.B0010));
    }

    /**
     * 根据对象ID获取存储记录
     */
    public StorageRecord loadStorageRecordCacheByObjectId(String objectId) {
        return this.loadOneOpt(StorageRecord::getObjectId, objectId)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.B0011));
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
