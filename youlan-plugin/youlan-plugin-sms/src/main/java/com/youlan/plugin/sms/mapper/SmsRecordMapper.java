package com.youlan.plugin.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.plugin.sms.entity.SmsRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsRecordMapper extends BaseMapper<SmsRecord> {
    /**
     * 清空短信记录
     */
    boolean cleanSmsRecordList();
}
