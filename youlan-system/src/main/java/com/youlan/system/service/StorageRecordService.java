package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.StorageRecord;
import com.youlan.system.mapper.StorageRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StorageRecordService extends BaseServiceImpl<StorageRecordMapper, StorageRecord> {
}
