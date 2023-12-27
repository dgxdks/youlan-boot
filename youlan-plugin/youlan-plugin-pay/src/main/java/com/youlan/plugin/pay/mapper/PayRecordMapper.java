package com.youlan.plugin.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.plugin.pay.entity.PayRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayRecordMapper extends BaseMapper<PayRecord> {

    /**
     * 支付记录分页
     */
    IPage<PayRecord> getPayRecordPageList(@Param("page") IPage<PayRecord> page, @Param("payRecord") PayRecord payRecord);

}
