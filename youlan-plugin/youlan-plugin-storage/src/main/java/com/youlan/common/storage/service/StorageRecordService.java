package com.youlan.common.storage.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.storage.config.StorageProperties;
import com.youlan.common.storage.entity.StorageRecord;
import com.youlan.common.storage.helper.StorageHelper;
import com.youlan.common.storage.mapper.StorageRecordMapper;
import com.youlan.common.storage.utils.StorageUtil;
import lombok.AllArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.youlan.common.core.restful.enums.ApiResultCode.B0009;

@Service
@AllArgsConstructor
public class StorageRecordService extends BaseServiceImpl<StorageRecordMapper, StorageRecord> implements FileRecorder {

    private final StorageProperties storageProperties;

    /**
     * 存储记录详情
     */
    public StorageRecord loadStorageRecord(Long id) {
        StorageRecord storageRecord = this.loadOneOpt(id)
                .orElseThrow(B0009::getException);
        return StorageHelper.processStroageUrl(storageRecord);
    }

    /**
     * 存储记录分页
     */
    public IPage<StorageRecord> getStorageRecordPageList(StorageRecord storageRecord) {
        List<String> sortColumns = List.of("create_time");
        IPage<StorageRecord> pageResult = this.loadPage(storageRecord, sortColumns, DBHelper.getQueryWrapper(storageRecord));
        pageResult.getRecords().forEach(StorageHelper::processStroageUrl);
        return pageResult;
    }

    /**
     * 根据文件地址获取存储记录
     */
    public StorageRecord loadStorageRecordByUrl(String url) {
        List<StorageRecord> storageRecords = this.lambdaQuery()
                .eq(StorageRecord::getUrl, url)
                .list();
        Assert.notEmpty(storageRecords, B0009::getException);
        return CollectionUtil.getFirst(storageRecords);
    }

    /**
     * 根据文件地址获取存储记录缓存
     */
    public StorageRecord loadStorageRecordCacheByUrl(String url) {
        String urlRedisKey = StorageUtil.getStorageUrlRedisKey(url);
        StorageRecord storageRecord = RedisHelper.getCacheObject(urlRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = this.loadStorageRecordByUrl(url);
        RedisHelper.setCacheObject(urlRedisKey, storageRecord, Duration.ofSeconds(storageProperties.getRecordCacheTimeout()));
        return storageRecord;
    }

    /**
     * 根据文件名称获取存储记录缓存
     */
    public StorageRecord loadStorageRecordCacheByFileName(String fileName) {
        String fileNameRedisKey = StorageUtil.getStorageFileNameRedisKey(fileName);
        StorageRecord storageRecord = RedisHelper.getCacheObject(fileNameRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = this.loadStorageRecordCacheByFileName(fileName);
        RedisHelper.setCacheObject(fileNameRedisKey, storageRecord, Duration.ofSeconds(storageProperties.getRecordCacheTimeout()));
        return storageRecord;
    }

    /**
     * 根据文件名称获取存储记录
     */
    public StorageRecord loadStorageRecordByFileName(String fileName) {
        List<StorageRecord> storageRecords = this.lambdaQuery()
                .eq(StorageRecord::getFileName, fileName)
                .list();
        Assert.notEmpty(storageRecords, B0009::getException);
        return CollectionUtil.getFirst(storageRecords);
    }

    /**
     * 根据对象ID获取存储记录缓存
     */
    public StorageRecord loadStorageRecordCacheByObjectId(String objectId) {
        String objectIdRedisKey = StorageUtil.getStorageObjectIdRedisKey(objectId);
        StorageRecord storageRecord = RedisHelper.getCacheObject(objectIdRedisKey);
        if (ObjectUtil.isNotNull(storageRecord)) {
            return storageRecord;
        }
        storageRecord = this.loadStorageRecordByObjectId(objectId);
        RedisHelper.setCacheObject(objectIdRedisKey, storageRecord, Duration.ofSeconds(storageProperties.getRecordCacheTimeout()));
        return storageRecord;
    }

    /**
     * 根据对象ID获取存储记录
     */
    public StorageRecord loadStorageRecordByObjectId(String objectId) {
        List<StorageRecord> storageRecords = this.lambdaQuery()
                .eq(StorageRecord::getObjectId, objectId)
                .list();
        Assert.notEmpty(storageRecords, B0009::getException);
        return CollectionUtil.getFirst(storageRecords);
    }

    @Override
    public boolean save(FileInfo fileInfo) {
        return true;
    }

    @Override
    public FileInfo getByUrl(String url) {
        StorageRecord storageRecord = this.loadStorageRecordCacheByUrl(url);
        return StorageHelper.storageRecordToFileInfo(storageRecord);
    }

    @Override
    public boolean delete(String url) {
        return true;
    }
}
