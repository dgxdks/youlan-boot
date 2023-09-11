package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.system.entity.StorageRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageRecordMapper extends BaseMapper<StorageRecord> {
    /**
     * 清空存储记录
     */
    boolean cleanStorageRecordList();
}
